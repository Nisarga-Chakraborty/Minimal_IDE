import java.io.*;
import javax.swing.*;

public class CExecutor implements LanguageExecutor {
    private Process currentProcess;

    @Override
    public boolean compile(File sourceFile) throws Exception {
        try {
            String sourcePath = sourceFile.getAbsolutePath();
            String execPath = sourcePath.replace(".c", ".exe");

            System.out.println("[C] Compiling: " + sourceFile.getName());
            System.out.println("[DEBUG] Source: " + sourcePath);
            System.out.println("[DEBUG] Output: " + execPath);

            ProcessBuilder pb = new ProcessBuilder("gcc", sourcePath, "-o", execPath);
            pb.redirectErrorStream(true);

            Process process = pb.start();

            // Read compilation output - show EVERYTHING
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("GCC OUTPUT: " + line); // This will show the actual error
            }

            int exitCode = process.waitFor();
            System.out.println("[DEBUG] GCC exit code: " + exitCode);

            if (exitCode == 0) {
                System.out.println("✅ C compilation successful!");
                // Verify the executable was created
                File exe = new File(execPath);
                System.out.println("[DEBUG] Executable exists: " + exe.exists());
                return true;
            } else {
                System.out.println("❌ C compilation failed!");
                System.out.println("Full GCC output:\n" + output.toString());
                return false;
            }

        } catch (Exception e) {
            System.out.println("[ERROR] Compilation exception: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void run(String filePath, TerminalPanel terminalPanel) throws Exception {
        // For C, filePath should be the executable name (like "program.exe")
        File execFile = new File(filePath);

        if (!execFile.exists()) {
            terminalPanel.appendOutput("[ERROR] Executable not found: " + filePath);
            terminalPanel.appendOutput("[INFO] Make sure compilation was successful");
            return;
        }

        terminalPanel.appendOutput("[C] Executing: " + execFile.getName());
        terminalPanel.appendOutput("────────────────────────────────────────");

        try {
            ProcessBuilder pb = new ProcessBuilder(filePath);
            pb.redirectErrorStream(true);
            currentProcess = pb.start();

            // Read program output
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

            // Monitor process completion
            new Thread(() -> {
                try {
                    int exitCode = currentProcess.waitFor();
                    terminalPanel.appendOutput("────────────────────────────────────────");
                    if (exitCode == 0) {
                        terminalPanel.appendOutput("✅ C program executed successfully!");
                    } else {
                        terminalPanel.appendOutput("❌ C program exited with code: " + exitCode);
                    }
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (IOException e) {
            terminalPanel.appendOutput("[ERROR] Execution failed: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void kill() {
        if (currentProcess != null && currentProcess.isAlive()) {
            currentProcess.destroy();
        }
    }
}
