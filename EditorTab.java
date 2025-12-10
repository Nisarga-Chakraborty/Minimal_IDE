
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Highlighter.Highlight;

public class EditorTab extends JPanel {
    private JTextPane codeArea;
    private JScrollPane scrollPane;
    private SyntaxHighlighter highlighter;
    private boolean isHighlighting = false;

    public EditorTab() {
        setLayout(new BorderLayout());

        codeArea = new JTextPane();
        codeArea.setBackground(Color.black);
        codeArea.setForeground(Color.white);
        codeArea.setOpaque(true);
        codeArea.setFont(new Font("Monospaced", Font.PLAIN, 14));

        highlighter = new JavaHighlighter();

        // Create a document listener that delays highlighting
        codeArea.getDocument().addDocumentListener(new DocumentListener() {
            private javax.swing.Timer timer;

            {
                timer = new javax.swing.Timer(100, e -> {
                    if (!isHighlighting) {
                        isHighlighting = true;
                        highlighter.highlight(codeArea);
                        isHighlighting = false;
                    }
                });
                timer.setRepeats(false);
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                timer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                timer.restart();
            }
        });

        scrollPane = new JScrollPane(codeArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public File saveToTempFile(String classname) throws IOException {
        File tempFile = new File(classname + ".java");// creating a temporary file and giving the name as the
                                                      // "classname".java
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(codeArea.getText());
        }
        return tempFile;
    }

    public JTextPane getEditor() {
        return codeArea;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

}
