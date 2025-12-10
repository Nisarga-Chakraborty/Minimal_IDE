import java.io.*;

public interface LanguageExecutor {
    boolean compile(File sourceFile) throws Exception;

    void run(String classOrBinaryName, TerminalPanel terminalPanel) throws Exception;

    void kill(); // Optional: for stopping execution
}
