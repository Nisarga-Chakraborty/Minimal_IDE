import java.io.*;
import java.util.*;
import javax.swing.*;

public class PythonExecutor implements LanguageExecutor {
    private Process currentProcess;
    private Thread outputThread;

    @Override
    public boolean compile(File sourceFile) {
        // Python doesn't require compilation
        return true;
    }

    @Override
    public void run(String filePath, TerminalPanel terminalPanel) throws IOException {
        // Check if Python is installed
        if (!isPythonInstalled()) {
            terminalPanel.appendOutput("[ERROR] Python is not installed or not in PATH");
            return;
        }

        ProcessBuilder pb = new ProcessBuilder("python", filePath);
        pb.redirectErrorStream(true);
        currentProcess = pb.start();

        outputThread = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(currentProcess.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    terminalPanel.appendOutput(line);
                }
            } catch (IOException e) {
                if (!e.getMessage().contains("Stream closed")) {
                    terminalPanel.appendOutput("[Error] Failed to read Python output: " + e.getMessage());
                }
            }
        });
        outputThread.start();

        // Add process termination handler
        new Thread(() -> {
            try {
                int exitCode = currentProcess.waitFor();
                if (exitCode != 0) {
                    terminalPanel.appendOutput("[Python Process] Exited with code: " + exitCode);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void kill() {
        if (currentProcess != null && currentProcess.isAlive()) {
            // Kill the process and its children
            currentProcess.descendants().forEach(ProcessHandle::destroy);
            currentProcess.destroyForcibly();

            if (outputThread != null && outputThread.isAlive()) {
                outputThread.interrupt();
            }
        }
    }

    private boolean isPythonInstalled() {
        try {
            Process process = Runtime.getRuntime().exec("python --version");
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
