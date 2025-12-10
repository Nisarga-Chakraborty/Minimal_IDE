
import java.io.*;

public class JavaExecutor {
    private Process currentProcess;

    public boolean compile(File javaFile) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("javac", javaFile.getAbsolutePath());// gets the path of the file
                                                                                    // command-> "javac "filepath""
        Process process = pb.start();
        return process.waitFor() == 0;
    }

    public void run(String className, TerminalPanel terminalPanel) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("java", className);
        currentProcess = pb.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(currentProcess.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(currentProcess.getErrorStream()));

        new Thread(() -> {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    terminalPanel.appendOutput(line);
                }
            } catch (IOException e) {
                terminalPanel.appendOutput("[Error] Failed to read output");
            }
        }).start();

        new Thread(() -> {
            try {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    terminalPanel.appendOutput("[Error] " + line + "\n");
                }
            } catch (IOException e) {
                terminalPanel.appendOutput("[Error] Failed to read error stream");
            }
        }).start();
    }
}
/*
 * import java.io.*;
 * 
 * public class JavaExecutor implements LanguageExecutor {
 * private Process currentProcess;
 * 
 * @Override
 * public boolean compile(File javaFile) throws Exception {
 * ProcessBuilder pb = new ProcessBuilder("javac", javaFile.getAbsolutePath());
 * Process process = pb.start();
 * return process.waitFor() == 0;
 * }
 * 
 * @Override
 * public void run(String className, TerminalPanel terminalPanel) throws
 * Exception {
 * ProcessBuilder pb = new ProcessBuilder("java", className);
 * currentProcess = pb.start();
 * 
 * BufferedReader reader = new BufferedReader(new
 * InputStreamReader(currentProcess.getInputStream()));
 * BufferedReader errorReader = new BufferedReader(new
 * InputStreamReader(currentProcess.getErrorStream()));
 * 
 * new Thread(() -> {
 * try {
 * String line;
 * while ((line = reader.readLine()) != null) {
 * terminalPanel.appendOutput(line);
 * }
 * } catch (IOException e) {
 * terminalPanel.appendOutput("[Error] Failed to read output");
 * }
 * }).start();
 * 
 * new Thread(() -> {
 * try {
 * String line;
 * while ((line = errorReader.readLine()) != null) {
 * terminalPanel.appendOutput("[Error] " + line);
 * }
 * } catch (IOException e) {
 * terminalPanel.appendOutput("[Error] Failed to read error stream");
 * }
 * }).start();
 * }
 * 
 * @Override
 * public void kill() {
 * if (currentProcess != null && currentProcess.isAlive()) {
 * currentProcess.destroy();
 * }
 * }
 * }
 */
