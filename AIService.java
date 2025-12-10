import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

class AIService {
    
    static MyApi api = new MyApi();
    private static final String API_KEY = api.apikey();
    private static final String HUGGING_FACE_API_URL = api.apiUrl();

    public AIService() {
        validateConfiguration();
    }
    
    private void validateConfiguration() {
        // Check if API_KEY is properly set (not null, not empty, and not the placeholder)
        if (API_KEY == null || API_KEY.isEmpty() || 
            API_KEY.equals("hf_your_actual_token_here") || 
            API_KEY.equals("your_actual_api_key_here") ||
            API_KEY.startsWith("hf_your_")) {
            throw new RuntimeException("Hugging Face API Key not configured. Please check MyApi class and replace the placeholder with your actual API key.");
        }
        
        // Check if API URL is properly set
        if (HUGGING_FACE_API_URL == null || HUGGING_FACE_API_URL.isEmpty() || 
        HUGGING_FACE_API_URL.contains("your_") ||
        HUGGING_FACE_API_URL.equals("https://router.huggingface.co/hf-inference/models/")) {
        throw new RuntimeException("API URL not configured. Please check MyApi class and specify a complete model URL.");
}
        
        System.out.println("✅ Hugging Face API Configuration validated.");
        System.out.println("🔑 API Key length: " + API_KEY.length());
        System.out.println("🤖 Model: " + HUGGING_FACE_API_URL);
    }

    public String sendMessage(String userMessage) {
    HttpURLConnection connection = null;
    try {
        System.out.println("=== DEBUG: Starting API Call ===");
        System.out.println("API URL: " + HUGGING_FACE_API_URL);
        System.out.println("API Key present: " + (API_KEY != null && !API_KEY.isEmpty()));
        System.out.println("User message: " + userMessage);
        
        URL url = new URL(HUGGING_FACE_API_URL);
        connection = (HttpURLConnection) url.openConnection();
        
        // Set up the connection for Hugging Face
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setConnectTimeout(60000);
        connection.setReadTimeout(60000);
        
        // Create the request body for Hugging Face
        String requestBody = buildRequestBody(userMessage);
        System.out.println("Request body: " + requestBody);
        
        // Send the request
        OutputStream os = connection.getOutputStream();
        os.write(requestBody.getBytes("UTF-8"));
        os.flush();
        os.close();
        
        // Get the response
        int responseCode = connection.getResponseCode();
        System.out.println("📥 Response code: " + responseCode);
        
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            System.out.println("✅ Raw response: " + response.toString());
            return parseAIResponse(response.toString());
            
        } else {
            // Read error stream
            BufferedReader errorReader = new BufferedReader(
                new InputStreamReader(connection.getErrorStream()));
            String errorLine;
            StringBuilder errorResponse = new StringBuilder();
            
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine);
            }
            errorReader.close();
            
            System.out.println("❌ Full error response: " + errorResponse.toString());
            String errorMsg = handleHuggingFaceError(responseCode, errorResponse.toString());
            return errorMsg;
        }
        
    } catch (Exception e) {
        System.err.println("💥 Exception: " + e.getMessage());
        e.printStackTrace();
        return "Error: " + e.getMessage();
    } finally {
        if (connection != null) {
            connection.disconnect();
        }
    }
}
    
    private String handleHuggingFaceError(int responseCode, String errorResponse) {
        switch (responseCode) {
            case 401:
                return "Error: Invalid Hugging Face API Token";
            case 403:
                return "Error: Model access denied - you may need to accept the model terms";
            case 404:
                return "Error: Model not found - check the model URL";
            case 503:
                if (errorResponse.contains("loading")) {
                    return "Model is loading, please try again in 30-60 seconds...";
                }
                return "Error: Service temporarily unavailable";
            case 429:
                return "Error: Rate limit exceeded - try again later";
            default:
                return "Error: API request failed with code " + responseCode + 
                       ". Message: " + errorResponse;
        }
    }
    
    /*private String buildRequestBody(String userMessage) {
        // Hugging Face API format
        String escapedMessage = userMessage
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
        
        return "{"
            + "\"inputs\": \"" + escapedMessage + "\","
            + "\"model\": \"facebook/blenderbot-400M-distill\","
            + "\"parameters\": {"
            + "\"max_new_tokens\": 1000,"
            + "\"temperature\": 0.7,"
            + "\"top_p\": 0.9,"
            + "\"do_sample\": true,"
            + "\"return_full_text\": false"
            + "}"
            + "}";
    }*/
   private String buildRequestBody(String userMessage) {
    // Hugging Face API format
    String escapedMessage = userMessage
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t");

    return "{"
        + "\"inputs\": \"" + escapedMessage + "\","
        + "\"model\": \"gpt2\","    // <-- Use just "gpt2" here
        + "\"parameters\": {"
        + "\"max_new_tokens\": 100,"
        + "\"temperature\": 0.7,"
        + "\"top_p\": 0.9,"
        + "\"do_sample\": true,"
        + "\"return_full_text\": false"
        + "}"
        + "}";
}

    
    private String parseAIResponse(String response) {
        try {
            System.out.println("🔍 Parsing response: " + response);
            
            // Handle model loading case
            if (response.contains("\"error\":\"Model\"") && response.contains("loading")) {
                return "The AI model is currently loading. This usually takes 30-60 seconds on first use. Please try again in a moment.";
            }
            
            // Try different extraction methods for Hugging Face responses
            String content = SimpleJSONParser.extractValueFromArray(response, "generated_text");
            if (content != null && !content.trim().isEmpty()) {
                return content.trim();
            }
            
            content = SimpleJSONParser.extractValue(response, "generated_text");
            if (content != null && !content.trim().isEmpty()) {
                return content.trim();
            }
            
            // Some models return the text directly in an array
            if (response.trim().startsWith("[") && response.trim().endsWith("]")) {
                String cleaned = response.trim().substring(1, response.trim().length() - 1);
                content = SimpleJSONParser.extractValue(cleaned, "generated_text");
                if (content != null && !content.trim().isEmpty()) {
                    return content.trim();
                }
            }
            
            // If we can't parse but got a 200 response, show raw response for debugging
            return "AI Response (raw): " + response.substring(0, Math.min(200, response.length()));
            
        } catch (Exception e) {
            return "Error parsing AI response: " + e.getMessage();
        }
    }
    
    // Test method specifically for Hugging Face
    /*public static void main(String[] args) {
        try {
            AIService aiService = new AIService();
            System.out.println("=== Testing Hugging Face API ===");
            
            // Test messages
            String[] testMessages = {
                "Hello! Say 'SUCCESS' if you can read this.",
                "What is 2+2?",
                "Tell me a short joke."
            };
            
            for (String message : testMessages) {
                System.out.println("\n--- Sending: " + message);
                String response = aiService.sendMessage(message);
                System.out.println("Response: " + response);
                Thread.sleep(2000); // Wait between requests
            }
            
        } catch (Exception e) {
            System.err.println("Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }*/
    public static void main(String[] args) {
    try {
        System.out.println("=== Testing Hugging Face Connection ===");
        AIService aiService = new AIService();
        
        // Simple test
        String response = aiService.sendMessage("Hello, are you working?");
        System.out.println("Final Response: " + response);
        
    } catch (Exception e) {
        System.err.println("Configuration Error: " + e.getMessage());
        System.out.println("Please check:");
        System.out.println("1. Your Hugging Face token in MyApi class");
        System.out.println("2. The model URL in MyApi class");
    }
    
}
}
