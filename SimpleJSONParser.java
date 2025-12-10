class SimpleJSONParser {
    
    public static String extractValueFromArray(String json, String key) {
        try {
            // Handle array responses: [{"generated_text": "..."}]
            if (json.trim().startsWith("[") && json.trim().endsWith("]")) {
                String arrayContent = json.trim().substring(1, json.trim().length() - 1);
                return extractValue(arrayContent, key);
            }
            
            // Handle nested arrays in objects
            int arrayStart = json.indexOf("[");
            int arrayEnd = json.lastIndexOf("]");
            
            if (arrayStart != -1 && arrayEnd != -1 && arrayEnd > arrayStart) {
                String arrayContent = json.substring(arrayStart, arrayEnd + 1);
                if (arrayContent.trim().startsWith("[") && arrayContent.trim().endsWith("]")) {
                    String innerContent = arrayContent.trim().substring(1, arrayContent.trim().length() - 1);
                    return extractValue(innerContent, key);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String extractValue(String json, String key) {
        try {
            // Look for pattern: "key": "value" 
            String pattern = "\"" + key + "\":";
            int startIndex = json.indexOf(pattern);
            
            if (startIndex == -1) return null;
            
            startIndex += pattern.length();
            
            // Skip whitespace
            while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
                startIndex++;
            }
            
            if (startIndex >= json.length()) return null;
            
            char firstChar = json.charAt(startIndex);
            
            if (firstChar == '"') {
                // String value
                startIndex++; // skip opening quote
                int endIndex = startIndex;
                boolean escaped = false;
                
                while (endIndex < json.length()) {
                    if (!escaped && json.charAt(endIndex) == '"') {
                        break;
                    }
                    escaped = (json.charAt(endIndex) == '\\' && !escaped);
                    endIndex++;
                }
                
                if (endIndex < json.length()) {
                    String value = json.substring(startIndex, endIndex);
                    return unescapeJson(value);
                }
            } else if (firstChar == '{') {
                // Object value - extract recursively
                int braceCount = 1;
                int endIndex = startIndex + 1;
                
                while (endIndex < json.length() && braceCount > 0) {
                    if (json.charAt(endIndex) == '{') braceCount++;
                    if (json.charAt(endIndex) == '}') braceCount--;
                    endIndex++;
                }
                
                if (braceCount == 0) {
                    String objectValue = json.substring(startIndex, endIndex);
                    // Try to extract text from the object
                    return extractValue(objectValue, "text");
                }
            }
            
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String extractContentFromChoices(String json) {
        // Fallback method for compatibility
        return extractValueFromArray(json, "generated_text");
    }
    
    private static String unescapeJson(String str) {
        if (str == null) return null;
        return str.replace("\\n", "\n")
                  .replace("\\\"", "\"")
                  .replace("\\\\", "\\")
                  .replace("\\t", "\t")
                  .replace("\\r", "\r")
                  .replace("\\b", "\b")
                  .replace("\\f", "\f");
    }
}
