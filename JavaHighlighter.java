import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaHighlighter implements SyntaxHighlighter {
    int start = 0, length = 0;
    private static final Set<String> KEYWORDS = Set.of("abstract", "assert", "break", "case",
            "catch", "class", "const", "continue", "default", "do", "double", "else", "enum",
            "extends", "final", "finally", "for", "goto", "if", "implements", "import", "instanceof",
            "interface", "native", "new", "package", "private", "protected", "public", "return",
            "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while", "true", "false", "null", "System", "printf", "print",
            "include", "println");

    private static final Set<String> datatypes = Set.of("String", "char", "int", "boolean", "float", "long", "double",
            "short", "byte");

    private AttributeSet keywordStyle;
    private AttributeSet datatypesStyle;
    private AttributeSet defaultStyle;
    private AttributeSet stringStyle;
    private AttributeSet commentStyle;
    private final AttributeSet numberStyle;
    private AttributeSet functionStyle;

    public JavaHighlighter() {
        StyleContext sc = StyleContext.getDefaultStyleContext();

        // Default style
        defaultStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.WHITE);
        defaultStyle = sc.addAttribute(defaultStyle, StyleConstants.FontFamily, "Monospaced");
        defaultStyle = sc.addAttribute(defaultStyle, StyleConstants.FontSize, 14);

        // Datatypes Style
        datatypesStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.CYAN);
        datatypesStyle = sc.addAttribute(datatypesStyle, StyleConstants.FontFamily, "Monospaced");
        datatypesStyle = sc.addAttribute(datatypesStyle, StyleConstants.FontSize, 14);

        // Keyword style
        keywordStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.BLUE);
        keywordStyle = sc.addAttribute(keywordStyle, StyleConstants.Bold, true);

        // function style
        functionStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(255, 165, 0)); // Orange
        functionStyle = sc.addAttribute(functionStyle, StyleConstants.Italic, true);

        // String style
        stringStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(0, 128, 0));
        stringStyle = sc.addAttribute(stringStyle, StyleConstants.Italic, false);

        // Comment style
        commentStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(128, 128, 128));
        commentStyle = sc.addAttribute(commentStyle, StyleConstants.Italic, true);

        // Number style
        numberStyle = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, new Color(255, 0, 0));
    }

    @Override
    public void highlight(JTextPane textPane) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyledDocument doc = textPane.getStyledDocument();
                String text = doc.getText(0, doc.getLength());

                // Apply default style to entire text first
                doc.setCharacterAttributes(0, text.length(), defaultStyle, true);

                // Highlight patterns
                highlightPattern(doc, text, "\\b(" + String.join("|", KEYWORDS) + ")\\b", keywordStyle);
                highlightPattern(doc, text, "\\b(" + String.join("|", datatypes) + ")\\b", datatypesStyle);
                highlightPattern(doc, text, "\"([^\"\\\\]|\\\\.)*\"", stringStyle);
                highlightPattern(doc, text, "'([^'\\\\]|\\\\.)*'", stringStyle);
                highlightPattern(doc, text, "//[^\n]*", commentStyle);
                highlightPattern(doc, text, "/\\*.*?\\*/", commentStyle);
                highlightPattern(doc, text, "\\b\\d+(\\.\\d+)?\\b", numberStyle);
                highlightFunctionCalls(doc, text);
            } catch (BadLocationException e) {
                // Ignore errors during text retrieval
            }
        });
    }

    private void highlightPattern(StyledDocument doc, String text, String regex, AttributeSet style) {
        try {
            Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);

            while (matcher.find()) {
                int start = matcher.start();
                int length = matcher.end() - start;
                doc.setCharacterAttributes(start, length, style, false);
            }
        } catch (Exception e) {
            // Ignore pattern matching errors
        }
    }

    private void highlightFunctionCalls(StyledDocument doc, String text) {
        Pattern pattern = Pattern.compile("\\b(\\w+)\\s*\\(");// identifies the Regex pattern
        Matcher matcher = pattern.matcher(text);// checks for any match in the text with the pattern

        while (matcher.find()) {// if it finds a match i.e. a word containing a "(" just after a word , it will
                                // highlight it in orange
            String word = matcher.group(1);// extract the matched word
            if (!KEYWORDS.contains(word)) {// ensures that the word is not a Java keyword present in the list of
                                           // KEYWORDS
                start = matcher.start(1);
                length = matcher.end(1) - start;
                doc.setCharacterAttributes(start, length, functionStyle, false);// Applies the functionStyle to that
                                                                                // range in the document
                // the "false" tells Swing to merge this style with existing attributes (like
                // font size or family), rather than replacing them.
            } else {
                doc.setCharacterAttributes(start, length, functionStyle, true);
            }

        }
    }
}
