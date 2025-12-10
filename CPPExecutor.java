import java.io.*;
import javax.swing.*;

public class CPPExecutor implements LanguageExecutor {
    private Process currentProcess;
    private String executablePath;

    @Override
    public boolean compile(File sourceFile) throws Exception {
        try {
            String sourcePath = sourceFile.getAbsolutePath();
            String baseName = sourcePath.substring(0, sourcePath.lastIndexOf('.'));
            executablePath = baseName + ".exe";

            System.out.println("[C++] Compiling: " + sourceFile.getName());
            System.out.println("[DEBUG] Source path: " + sourcePath);
            System.out.println("[DEBUG] Output path: " + executablePath);

            ProcessBuilder pb = new ProcessBuilder("g++", sourcePath, "-o", executablePath);

            // Set the working directory to where the source file is located
            File parentDir = sourceFile.getParentFile();
            if (parentDir != null) {
                pb.directory(parentDir);
            } else {
                pb.directory(new File("."));
            }

            System.out.println("[DEBUG] Working directory: " + pb.directory().getAbsolutePath());

            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("G++: " + line);
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("✅ C++ compilation successful!");
                File exeFile = new File(executablePath);
                System.out.println("[DEBUG] Executable exists: " + exeFile.exists());
                return true;
            } else {
                System.out.println("❌ C++ compilation failed!");
                System.out.println("Error output:\n" + output.toString());
                return false;
            }

        } catch (Exception e) {
            System.out.println("[ERROR] C++ compilation error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void run(String filePath, TerminalPanel terminalPanel) throws Exception {
        terminalPanel.appendOutput("[C++] Starting C++ program execution...");

        if (filePath.endsWith(".exe")) {
            File executable = new File(filePath);
            if (!executable.exists()) {
                terminalPanel.appendOutput("[ERROR] Executable not found: " + filePath);
                return;
            }

            terminalPanel.appendOutput("[C++] Executing: " + executable.getName());
            terminalPanel.appendOutput("────────────────────────────────────────");
            executeProgram(filePath, terminalPanel);
            return;
        }

        File sourceFile = new File(filePath);
        if (!sourceFile.exists()) {
            terminalPanel.appendOutput("[ERROR] Source file not found: " + filePath);
            return;
        }

        terminalPanel.appendOutput("[C++] Compiling: " + sourceFile.getName());
        terminalPanel.appendOutput("────────────────────────────────────────");

        boolean compileSuccess = compile(sourceFile);

        if (!compileSuccess) {
            terminalPanel.appendOutput("❌ C++ compilation failed! Cannot run the program.");
            return;
        }

        terminalPanel.appendOutput("✅ C++ compilation successful!");

        if (executablePath == null) {
            terminalPanel.appendOutput("[ERROR] No executable found after compilation.");
            return;
        }

        File executable = new File(executablePath);
        if (!executable.exists()) {
            terminalPanel.appendOutput("[ERROR] Executable not found: " + executablePath);
            return;
        }

        terminalPanel.appendOutput("[C++] Executing: " + executable.getName());
        terminalPanel.appendOutput("────────────────────────────────────────");

        executeProgram(executablePath, terminalPanel);
    }

    private void executeProgram(String executablePath, TerminalPanel terminalPanel) {
        try {
            ProcessBuilder pb;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                pb = new ProcessBuilder(executablePath);
            } else {
                pb = new ProcessBuilder("./" + executablePath);
            }

            pb.redirectErrorStream(true);
            currentProcess = pb.start();

            new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(currentProcess.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        terminalPanel.appendOutput(line);
                    }
                } catch (IOException e) {
                    if (!e.getMessage().contains("Stream closed")) {
                        terminalPanel.appendOutput("[Error] " + e.getMessage());
                    }
                }
            }).start();

            new Thread(() -> {
                try {
                    int exitCode = currentProcess.waitFor();
                    terminalPanel.appendOutput("────────────────────────────────────────");
                    if (exitCode == 0) {
                        terminalPanel.appendOutput("✅ C++ program executed successfully!");
                    } else {
                        terminalPanel.appendOutput("❌ C++ program exited with code: " + exitCode);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (IOException e) {
            terminalPanel.appendOutput("[ERROR] Execution failed: " + e.getMessage());
        }
    }

    @Override
    public void kill() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.descendants().forEach(ProcessHandle::destroy);
            currentProcess.destroyForcibly();
            System.out.println("[C++] Process terminated by user");
        }
    }
}
