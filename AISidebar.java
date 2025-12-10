import java.io.*;
import java.net.HttpURLConnection;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringTokenizer;

public class AISidebar extends JPanel {
    private JTextArea chatArea;
    private JTextField messageField;
    private JButton sendButton;
     AIService aiService;
    
    public AISidebar() {
        initializeUI();
        setupActionListeners();
        aiService = new AIService(); // or OpenAIStyleService()
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 600));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Chat area (top blank area)
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 12));
        chatArea.setMargin(new Insets(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        messageField = new JTextField();
        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80, 30));
        
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        add(scrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        
        // Welcome message
        appendToChat("AI Assistant Ready!\nType your message below.\n\n---\n\n");
    }
    
    private void setupActionListeners() {
        // Send button action
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        
        // Enter key in text field
        messageField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
    }
    
    private void sendMessage() {
    String userMessage = messageField.getText().trim();
    
    if (!userMessage.isEmpty()) {
        // Add user message to chat area
        appendToChat("You: " + userMessage + "\n\n");
        messageField.setText("");
        
        // Disable send button while processing
        sendButton.setEnabled(false);
        sendButton.setText("Sending...");
        
        // Use SwingWorker to avoid freezing the UI
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    System.out.println("Sending message to AI: " + userMessage);
                    String response = aiService.sendMessage(userMessage);
                    System.out.println("AI Response received: " + (response != null ? response.substring(0, Math.min(50, response.length())) : "null"));
                    return response;
                } catch (Exception e) {
                    System.err.println("Error in doInBackground: " + e.getMessage());
                    e.printStackTrace();
                    return "Error: " + e.getMessage();
                }
            }
            
            @Override
    protected void done() {
        try {
            String aiResponse = get();
            if (aiResponse != null && aiResponse.startsWith("Error:")) {
                appendToChat("AI: ❌ " + aiResponse + "\n\n---\n\n");
            } else {
                appendToChat("AI: " + aiResponse + "\n\n---\n\n");
            }
        } catch (java.util.concurrent.ExecutionException ex) {
            String errorMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            appendToChat("AI: ❌ Execution Error - " + errorMessage + "\n\n---\n\n");
            System.err.println("ExecutionException: " + errorMessage);
            ex.printStackTrace();
        } catch (java.lang.InterruptedException ex) {
            appendToChat("AI: ❌ Request interrupted - " + ex.getMessage() + "\n\n---\n\n");
            System.err.println("InterruptedException: " + ex.getMessage());
        } catch (Exception ex) {
            appendToChat("AI: ❌ Unexpected Error - " + ex.getMessage() + "\n\n---\n\n");
            System.err.println("Unexpected error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            sendButton.setEnabled(true);
            sendButton.setText("Send");
        }
    }
        };
        
        worker.execute();
    }
}
    
    private void appendToChat(String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                chatArea.append(text);
                // Auto-scroll to bottom
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            }
        });
    }

    public void toggleVisibility() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toggleVisibility'");
    }
}
