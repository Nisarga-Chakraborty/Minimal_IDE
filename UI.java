import javax.swing.*;
import java.awt.*;
import java.io.*;

public class UI extends JFrame {
    private JTabbedPane tabbedPane;
    public TerminalPanel terminalPanel;
    private EditorTab editorTab;

    public enum Language {
        JAVA, C, CPP, PYTHON, FLUTTER
    };

    public UI() {
        setSize(1360, 720);
        setTitle("IDE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.black);
        setLocationRelativeTo(null);
        setResizable(true);

        setLayout(new BorderLayout());

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.black);
        menuBar.setForeground(Color.white);
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setBackground(Color.black);
        fileMenu.setForeground(Color.white);
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setBackground(Color.black);
        editMenu.setForeground(Color.white);
        menuBar.add(editMenu);

        JMenu runMenu = new JMenu("Run");
        runMenu.setBackground(Color.black);
        runMenu.setForeground(Color.white);
        menuBar.add(runMenu);

        JMenu settingsMenu = new JMenu("Settings");
        settingsMenu.setBackground(Color.black);
        settingsMenu.setForeground(Color.white);
        menuBar.add(settingsMenu);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setBackground(Color.black);
        helpMenu.setForeground(Color.white);
        menuBar.add(helpMenu);

        // Tabbed Pane for code editor

        tabbedPane = new JTabbedPane();

        terminalPanel = new TerminalPanel(); // Also declared as a class-level field

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, tabbedPane, terminalPanel.getPanel());
        splitPane.setResizeWeight(0.85);
        splitPane.setDividerSize(5);// lessens the divider line size i.e. width
        splitPane.setBorder(null);// sets a border for good UI

        add(splitPane, BorderLayout.CENTER); // ✅ Full-screen placement

        // adding components to "File" menu
        JMenuItem newFileItem = new JMenuItem("New File");
        newFileItem.setBackground(Color.black);
        newFileItem.setForeground(Color.white);
        fileMenu.add(newFileItem);

        JMenuItem openFileItem = new JMenuItem("Open File");
        openFileItem.setBackground(Color.black);
        openFileItem.setForeground(Color.white);
        fileMenu.add(openFileItem);

        JMenuItem openFolder = new JMenuItem("Open Folder");
        openFolder.setBackground(Color.black);
        openFolder.setForeground(Color.white);
        fileMenu.add(openFolder);

        JMenuItem saveFileItem = new JMenuItem("Save File");
        saveFileItem.setBackground(Color.black);
        saveFileItem.setForeground(Color.white);
        fileMenu.add(saveFileItem);

        JMenuItem saveAsFileItem = new JMenuItem("Save As");
        saveAsFileItem.setBackground(Color.black);
        saveAsFileItem.setForeground(Color.white);
        fileMenu.add(saveAsFileItem);

        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.setBackground(Color.black);
        exitItem.setForeground(Color.white);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);

        // adding components to "Edit" menu
        JMenuItem undoItem = new JMenuItem("Undo");
        undoItem.setBackground(Color.black);
        undoItem.setForeground(Color.white);
        editMenu.add(undoItem);

        JMenuItem redoItem = new JMenuItem("Redo");
        redoItem.setBackground(Color.black);
        redoItem.setForeground(Color.white);
        editMenu.add(redoItem);

        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.setBackground(Color.black);
        cutItem.setForeground(Color.white);
        editMenu.add(cutItem);

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.setBackground(Color.black);
        copyItem.setForeground(Color.white);
        editMenu.add(copyItem);

        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.setBackground(Color.black);
        pasteItem.setForeground(Color.white);
        editMenu.add(pasteItem);

        JMenuItem findItem = new JMenuItem("Find");
        findItem.setBackground(Color.black);
        findItem.setForeground(Color.white);
        editMenu.add(findItem);

        JMenuItem replaceItem = new JMenuItem("Replace");
        replaceItem.setBackground(Color.black);
        replaceItem.setForeground(Color.white);
        editMenu.add(replaceItem);

        // adding components to "Run" menu

        JMenuItem javaRun = new JMenuItem("Run Java Program");
        javaRun.setBackground(Color.black);
        javaRun.setForeground(Color.white);
        runMenu.add(javaRun);

        javaRun.addActionListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof EditorTab) {// checking if "selected" is an instance of EditorTab i.e. when a new
                                                // file is created, then the focus should be on that tab
                EditorTab editorTab = (EditorTab) selected;
                JTextPane codeArea = editorTab.getEditor(); // getEditor() returns the JTextPane

                try {
                    String className = JOptionPane.showInputDialog("Enter class name:");
                    if (className == null || className.isBlank())
                        return;

                    File tempFile = new File(className + ".java");
                    try (FileWriter writer = new FileWriter(tempFile)) {
                        writer.write(codeArea.getText());
                    }

                    JavaExecutor executor = new JavaExecutor();
                    boolean success = executor.compile(tempFile);
                    if (success) {
                        terminalPanel.clear(); // Clear terminal before running new output
                        executor.run(className, terminalPanel);
                    } else {
                        terminalPanel.appendOutput("Compilation failed.");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    terminalPanel.appendOutput("[Exceptioon] " + ex.getMessage());
                }
            }

        });

        JMenuItem pythonRun = new JMenuItem("Run Python Program");
        pythonRun.setBackground(Color.black);
        pythonRun.setForeground(Color.white);
        runMenu.add(pythonRun);

        pythonRun.addActionListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof EditorTab) {
                EditorTab editorTab = (EditorTab) selected;
                JTextPane codeArea = editorTab.getEditor();

                try {
                    String fileName = JOptionPane.showInputDialog("Enter Python file name (with .py extension):");
                    if (fileName == null || fileName.isBlank())
                        return;

                    File tempFile = new File(fileName);
                    try (FileWriter writer = new FileWriter(tempFile)) {
                        writer.write(codeArea.getText());
                    }

                    PythonExecutor executor = new PythonExecutor();
                    terminalPanel.clear(); // Clear terminal before running new output
                    executor.run(tempFile.getAbsolutePath(), terminalPanel);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    terminalPanel.appendOutput("[Exception] " + ex.getMessage());
                }
            }
        });

        JMenuItem cRun = new JMenuItem("Run C Program");
        cRun.setBackground(Color.black);
        cRun.setForeground(Color.white);
        runMenu.add(cRun);

        cRun.addActionListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof EditorTab) {
                EditorTab editorTab = (EditorTab) selected;
                JTextPane codeArea = editorTab.getEditor();

                try {
                    String fileName = JOptionPane.showInputDialog("Enter C filename (with .c extension):");
                    if (fileName == null || fileName.isBlank()) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid filename");
                        return;
                    }

                    if (!fileName.endsWith(".c")) {
                        fileName = fileName + ".c";
                    }

                    File tempFile = new File(fileName);
                    try (FileWriter writer = new FileWriter(tempFile)) {
                        writer.write(codeArea.getText());
                    }

                    CExecutor executor = new CExecutor();
                    terminalPanel.clear();

                    // Step 1: Show compilation starting message
                    terminalPanel.appendOutput("[C] Compiling: " + fileName);
                    terminalPanel.appendOutput("────────────────────────────────────────");

                    // Step 2: Compile (with only one parameter as per interface)
                    boolean success = executor.compile(tempFile); // CORRECT: Only one parameter

                    if (success) {
                        terminalPanel.appendOutput("✅ C compilation successful!");
                        terminalPanel.appendOutput("");

                        // Step 3: Run the program
                        // We need to pass the executable name, not the source file
                        String executableName = fileName.substring(0, fileName.lastIndexOf('.'));
                        if (System.getProperty("os.name").toLowerCase().contains("win")) {
                            executableName += ".exe";
                        }

                        executor.run(executableName, terminalPanel);
                    } else {
                        terminalPanel.appendOutput("❌ C compilation failed!");
                        terminalPanel.appendOutput("Check the errors above and fix your code.");
                    }
                } catch (Exception ex) {
                    terminalPanel.appendOutput("[Exception] " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        JMenuItem cppRun = new JMenuItem("Run C++ Program");
        cppRun.setBackground(Color.black);
        cppRun.setForeground(Color.white);
        runMenu.add(cppRun);

        cppRun.addActionListener(e -> {
            Component selected = tabbedPane.getSelectedComponent();
            if (selected instanceof EditorTab) {
                EditorTab editorTab = (EditorTab) selected;
                JTextPane codeArea = editorTab.getEditor();

                try {
                    String fileName = JOptionPane.showInputDialog("Enter C++ filename (with .cpp extension):");
                    if (fileName == null || fileName.isBlank()) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid filename");
                        return;
                    }

                    if (!fileName.endsWith(".cpp")) {
                        fileName = fileName + ".cpp";
                    }

                    File tempFile = new File(fileName);
                    try (FileWriter writer = new FileWriter(tempFile)) {
                        writer.write(codeArea.getText());
                    }

                    CPPExecutor executor = new CPPExecutor();// calling the CPPExecutor class from the UI class
                    terminalPanel.clear();// clearing any previous text if present from the terminalPanel

                    // Step 1: Show compilation starting message
                    terminalPanel.appendOutput("[C++] Compiling: " + fileName);
                    terminalPanel.appendOutput("────────────────────────────────────────");

                    // Step 2: Compile (with only one parameter as per interface)
                    boolean success = executor.compile(tempFile); // CORRECT: Only one parameter

                    if (success) {
                        terminalPanel.appendOutput("✅ C++ compilation successful!");
                        terminalPanel.appendOutput("");

                        // Step 3: Run the program
                        // We need to pass the executable name, not the source file
                        String executableName = fileName.substring(0, fileName.lastIndexOf('.'));
                        if (System.getProperty("os.name").toLowerCase().contains("win")) {
                            executableName += ".exe";
                        }

                        executor.run(executableName, terminalPanel);
                    } else {
                        terminalPanel.appendOutput("❌ C++ compilation failed!");
                        terminalPanel.appendOutput("Check the errors above and fix your code.");
                    }
                } catch (Exception ex) {
                    terminalPanel.appendOutput("[Exception] " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        JMenuItem flutterRun = new JMenuItem("Run Flutter Program");
        flutterRun.setBackground(Color.black);
        flutterRun.setForeground(Color.white);
        runMenu.add(flutterRun);

        // adding components to "Settings" menu
        JMenuItem themeItem = new JMenuItem("Theme");
        themeItem.setBackground(Color.black);
        themeItem.setForeground(Color.white);
        settingsMenu.add(themeItem);

        /*
         * themeItem.addActionListener(e -> {
         * 
         * EditorTab ab = new EditorTab();
         * 
         * });
         */

        JMenuItem fontItem = new JMenuItem("Font");
        fontItem.setBackground(Color.black);
        fontItem.setForeground(Color.white);
        settingsMenu.add(fontItem);

        JMenuItem languageItem = new JMenuItem("Language");
        languageItem.setBackground(Color.black);
        languageItem.setForeground(Color.white);
        settingsMenu.add(languageItem);
        languageItem.addActionListener(e -> {
            String[] languages = { "Java", "C", "C++", "Python", "Flutter" };
            String selectedLanguage = (String) JOptionPane.showInputDialog(this, "Select Language:",
                    "Language Selection",
                    JOptionPane.PLAIN_MESSAGE, null, languages, languages[0]);
            if (selectedLanguage != null) {
                switch (selectedLanguage.toLowerCase()) {
                    case "java":
                        selectedLanguage = Language.JAVA.name();
                        break;
                    case "c":
                        selectedLanguage = Language.C.name();
                        break;
                    case "c++":
                        selectedLanguage = Language.CPP.name();
                        break;
                    case "python":
                        selectedLanguage = Language.PYTHON.name();
                        break;
                    case "flutter":
                        selectedLanguage = Language.FLUTTER.name();
                        break;
                }
                JOptionPane.showMessageDialog(this, "You selected: " + selectedLanguage);
            } else {
                JOptionPane.showMessageDialog(this,
                        "No language selected.....You need to select a language to run any Program");
            }

        });

        JMenuItem splitterminalItem = new JMenuItem("Split Terminal");
        splitterminalItem.setBackground(Color.black);
        splitterminalItem.setForeground(Color.white);
        settingsMenu.add(splitterminalItem);

        JMenuItem AIMenu = new JMenuItem("AI Sidebar");
        AIMenu.setBackground(Color.black);
        AIMenu.setForeground(Color.white);
        settingsMenu.add(AIMenu);

        AIMenu.addActionListener(e -> {

            String AI = JOptionPane.showInputDialog(this, "Do you want to open the AI Sidebar? (yes/no)");
            if (AI.equalsIgnoreCase("yes")) {
                AISidebar aiSidebar = new AISidebar();
                aiSidebar.setVisible(false); // Initially hidden
                add(aiSidebar, BorderLayout.EAST);
                aiSidebar.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "You chose not to open the AI Sidebar");
            }

        });

        // adding components to "Help" menu
        JMenuItem documentationItem = new JMenuItem("Documentation");
        documentationItem.setBackground(Color.black);
        documentationItem.setForeground(Color.white);
        helpMenu.add(documentationItem);
        documentationItem.addActionListener(e -> {
            // Create a dialog to show documentation
            JDialog docDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(helpMenu), "Documentation", true);
            docDialog.setSize(600, 500);
            docDialog.setLocationRelativeTo(null);

            // Create a JTextArea with documentation content
            JTextArea docArea = new JTextArea();
            docArea.setEditable(false);
            docArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
            docArea.setBackground(Color.BLACK);
            docArea.setForeground(Color.WHITE);
            docArea.setText(
                    "Welcome to Nisarga's Modular IDE 🎉\n\n" +
                            "This IDE helps you write, edit, and run code across multiple languages.\n\n" +
                            "────────────────────────────────────────────\n" +
                            "🧠 Features:\n" +
                            "• Tabbed code editor with syntax highlighting\n" +
                            "• Supports Java, C, C++, Python, and Flutter\n" +
                            "• Workspace folder selection and file management\n" +
                            "• Compile and Run integration\n" +
                            "• Terminal panel for output and error display\n\n" +
                            "────────────────────────────────────────────\n" +
                            "🖋 Syntax Highlighting:\n" +
                            "• Keywords → Blue\n" +
                            "• Datatypes → Cyan\n" +
                            "• Functions → Orange\n" +
                            "• Strings → Green\n" +
                            "• Comments → Gray\n" +
                            "• Numbers → Red\n\n" +
                            "────────────────────────────────────────────\n" +
                            "📂 File Operations:\n" +
                            "• File → Open: Load a file into a new tab\n" +
                            "• File → Save: Write changes to disk\n" +
                            "• Workspace folder selection for organized editing\n\n" +
                            "────────────────────────────────────────────\n" +
                            "▶️ Compile & Run:\n" +
                            "• Select language from dropdown\n" +
                            "• Click 'Run' to compile and execute\n" +
                            "• Output appears in terminal panel\n\n" +
                            "────────────────────────────────────────────\n" +
                            "📌 Note:\n" +
                            "This IDE is a personal project built with Java Swing.\n" +
                            "More features coming soon!\n\n" +
                            "Made with ❤️ by Nisarga");

            // Wrap the text area in a scroll pane
            JScrollPane scrollPane = new JScrollPane(docArea);
            docDialog.add(scrollPane);

            // Show the dialog
            docDialog.setVisible(true);
        });

        JMenuItem supportItem = new JMenuItem("Support");
        supportItem.setBackground(Color.black);
        supportItem.setForeground(Color.white);
        helpMenu.add(supportItem);
        supportItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "This is a basic implementation of an IDE. \n For support please SMS in this number -> 9333879821");
        });

        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.setBackground(Color.black);
        aboutItem.setForeground(Color.white);
        aboutItem.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "IDE Version 1.0\nDeveloped by Nisarga Chakraborty\n", "About",
                JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);

        newFileItem.addActionListener(e -> {
            EditorTab newTab = new EditorTab();
            String name = JOptionPane.showInputDialog(this, "Enter the Name of the file");
            if (name == null) {
                tabbedPane.addTab("Untitled", newTab);
            }
            tabbedPane.addTab(name, newTab);
            tabbedPane.setTabComponentAt(tabbedPane.indexOfComponent(newTab),
                    createTabHeader(name, newTab, tabbedPane));
            tabbedPane.setSelectedComponent(newTab);
        });

        openFileItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int returnValue = fileChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                EditorTab newTab = new EditorTab();

                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    newTab.getEditor().setText(content.toString());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage());
                }

                tabbedPane.addTab(file.getName(), newTab);
                tabbedPane.setSelectedComponent(newTab);
            }
        });

        openFolder.addActionListener(e -> {
            JFileChooser folderChooser = new JFileChooser();
            folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnValue = folderChooser.showOpenDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File folder = folderChooser.getSelectedFile();
                setTitle("IDE-" + folder.getName());
                JOptionPane.showMessageDialog(this, "Workspace folder is set to" + folder.getAbsolutePath());
            }

        });
        saveFileItem.addActionListener(e -> {
            EditorTab currentTab = (EditorTab) tabbedPane.getSelectedComponent();
            if (currentTab != null) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(this);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    try {
                        java.nio.file.Files.writeString(file.toPath(), currentTab.getEditor().getText());
                        tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                    }
                }
            }
        });

        saveAsFileItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showSaveDialog(this);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                EditorTab currentTab = (EditorTab) tabbedPane.getSelectedComponent();
                try {
                    java.nio.file.Files.writeString(file.toPath(), currentTab.getEditor().getText());
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), file.getName());
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                }
            }
        });

        cutItem.addActionListener(e -> {
            EditorTab currentTab = (EditorTab) tabbedPane.getSelectedComponent();
            if (currentTab != null) {
                currentTab.getEditor().cut();
            } else
                JOptionPane.showMessageDialog(this, "Nothing is selected");
        });

        copyItem.addActionListener(e -> {
            EditorTab currentTab = (EditorTab) tabbedPane.getSelectedComponent();
            if (currentTab != null) {
                currentTab.getEditor().copy();
            } else
                JOptionPane.showMessageDialog(this, "Nothing is selected");
        });

        pasteItem.addActionListener(e -> {
            EditorTab currentTab = (EditorTab) tabbedPane.getSelectedComponent();
            if (currentTab != null) {
                currentTab.getEditor().paste();
            } else
                JOptionPane.showMessageDialog(this, "Nothing is selected");
        });

        findItem.addActionListener(e -> {
            EditorTab currentTab = (EditorTab) tabbedPane.getSelectedComponent();// calling the class EditorTab with an
                                                                                 // object "currentTab"
            if (currentTab != null) {
                String toFind = JOptionPane.showInputDialog(this, "Enter text to find:");
                if (toFind != null && !toFind.isEmpty()) {
                    String content = currentTab.getEditor().getText();
                    int index = content.indexOf(toFind);
                    if (index >= 0) {
                        currentTab.getEditor().setCaretPosition(index);
                        currentTab.getEditor().select(index, index + toFind.length());
                        currentTab.getEditor().requestFocusInWindow();
                    } else {
                        JOptionPane.showMessageDialog(this, "Text not found");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No active tab to search in");
            }
        });

        replaceItem.addActionListener(e -> {
            EditorTab currentTab = (EditorTab) tabbedPane.getSelectedComponent();
            if (currentTab != null) {
                JPanel panel = new JPanel(new GridLayout(2, 2));
                JTextField findField = new JTextField();
                JTextField replaceField = new JTextField();
                panel.add(new JLabel("Find:"));
                panel.add(findField);
                panel.add(new JLabel("Replace with:"));
                panel.add(replaceField);

                int result = JOptionPane.showConfirmDialog(this, panel, "Find and Replace",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result == JOptionPane.OK_OPTION) {
                    String toFind = findField.getText();
                    String toReplace = replaceField.getText();
                    if (!toFind.isEmpty()) {
                        String content = currentTab.getEditor().getText();
                        content = content.replace(toFind, toReplace);
                        currentTab.getEditor().setText(content);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No active tab to perform replace");
            }
        });
    }

    public JPanel createTabHeader(String title, Component tabContent, JTabbedPane tabbedPane) {
        JPanel tabHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabHeader.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        JButton closeButton = new JButton("×");

        closeButton.setMargin(new Insets(0, 5, 0, 5));
        closeButton.setBorder(null);
        closeButton.setFocusable(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setForeground(Color.RED);

        closeButton.addActionListener(e -> {
            int index = tabbedPane.indexOfComponent(tabContent);
            if (index != -1) {
                tabbedPane.remove(index);
            }
        });

        tabHeader.add(titleLabel);
        tabHeader.add(closeButton);

        return tabHeader;
    }

    public void updateEditorForLanguage(Language lang) {
        switch (lang) {
            case JAVA -> editorTab.getEditor().setText("// Write your Java code here");
            case PYTHON -> editorTab.getEditor().setText("# Write your Python code here");
            case C -> editorTab.getEditor().setText("//C code starts here");
            case CPP -> editorTab.getEditor().setText("// C++ code starts here");
            case FLUTTER -> editorTab.getEditor().setText("// Flutter code starts here");

        }
    }

    public static void main(String args[]) {
        UI ob = new UI();
        ob.setVisible(true);
    }
}
