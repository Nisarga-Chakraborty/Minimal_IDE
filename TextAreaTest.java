    import javax.swing.*;

    public class TextAreaTest {
         public static void main(String[] args) {
                // Ensure GUI updates happen on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                 // Create the main frame
                JFrame frame = new JFrame("JTextArea Test");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(500, 400);

                // Create a JTextArea
                JTextArea textArea = new JTextArea();
                textArea.setText("This is a sample JTextArea.\nYou can type here to test your IDE.");
                textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14));

               // Add the text area to a scroll pane
               JScrollPane scrollPane = new JScrollPane(textArea);

               // Add scroll pane to the frame
               frame.add(scrollPane);

                // Make the frame visible
                frame.setVisible(true);
             });
          }
        }
