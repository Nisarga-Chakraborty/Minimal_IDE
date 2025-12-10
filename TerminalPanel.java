import javax.swing.*;
import java.awt.*;

class TerminalPanel {
    private JTextArea outputArea;
    private JPanel panel;

    public TerminalPanel() {
        outputArea = new JTextArea();
        JLabel terminalLabel = new JLabel("TERMINAL");
        terminalLabel.setForeground(Color.yellow);
        terminalLabel.setBackground(Color.black);
        terminalLabel.setOpaque(true);
        terminalLabel.setFont(new Font("Times New Roman", Font.BOLD, 25));

        outputArea.setEditable(false);
        outputArea.setBackground(Color.black);
        outputArea.setForeground(Color.white);
        outputArea.setFont(new Font("Times New Roman", Font.PLAIN, 15));
        outputArea.setMargin(new Insets(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(outputArea);

        panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(terminalLabel, BorderLayout.NORTH);
    }

    public JPanel getPanel() {
        return panel;
    }

    public void appendOutput(String text) {
        outputArea.append(text + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }

    public void clear() {
        outputArea.setText("");
    }

    public JTextArea getOutputArea() {
        return outputArea;
    }

}
