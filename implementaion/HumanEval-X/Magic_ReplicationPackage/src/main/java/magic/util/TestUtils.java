package magic.util;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class TestUtils {

    public static boolean executeJavaCode(String solutionCode, String testCode) throws Exception {
        Path tempDir = null;
        Process runProcess = null;
        try {
            tempDir = Files.createTempDirectory("java_exec");
            File mainFile = new File(tempDir.toFile(), "Main.java");
            String fullCode = solutionCode + "\n\n" + testCode;
            Files.writeString(mainFile.toPath(), fullCode, StandardCharsets.UTF_8);

            // 编译
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", "Main.java");
            compileProcessBuilder.directory(tempDir.toFile());
            compileProcessBuilder.redirectErrorStream(true);
            Process compileProcess = compileProcessBuilder.start();
            drainInputStream(compileProcess.getInputStream());
            int compileResult = compileProcess.waitFor();
            if (compileResult != 0) {
                return false;
            }

            // 执行 Main 类
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "-Xmx256m", "Main");
            runProcessBuilder.directory(tempDir.toFile());
            runProcessBuilder.redirectErrorStream(true);
            runProcess = runProcessBuilder.start();
            boolean finished = runProcess.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                runProcess.destroyForcibly();
                return false;
            }
            return runProcess.exitValue() == 0;
        } finally {
            if (runProcess != null && runProcess.isAlive()) runProcess.destroyForcibly();
            if (tempDir != null) {
                try {
                    FileUtils.deleteDirectory(tempDir.toFile());
                } catch (IOException e) {
                    System.err.println("Failed to delete temp directory: " + e.getMessage());
                }
            }
        }
    }

    private static void drainInputStream(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            while (reader.readLine() != null) {
            }
        }
    }

    public static String executeJavaCode2(String solutionCode, String testCode) throws Exception {
        Path tempDir = null;
        Process runProcess = null;
        try {
            tempDir = Files.createTempDirectory("java_exec");
            File mainFile = new File(tempDir.toFile(), "Main.java");
            String fullCode = solutionCode + "\n\n" + testCode;
            Files.writeString(mainFile.toPath(), fullCode, StandardCharsets.UTF_8);
            Charset consoleCharset = System.getProperty("os.name").toLowerCase().contains("win")
                    ? Charset.forName("GBK")
                    : StandardCharsets.UTF_8;

            // 编译
            ProcessBuilder compileProcessBuilder = new ProcessBuilder("javac", "Main.java");
            compileProcessBuilder.directory(tempDir.toFile());
            compileProcessBuilder.redirectErrorStream(true);
            Process compileProcess = compileProcessBuilder.start();
            String compileOutput = readProcessOutput(compileProcess, consoleCharset);
            int compileResult = compileProcess.waitFor();
            if (compileResult != 0) {
                return compileOutput;
            }

            // 执行 Main 类
            ProcessBuilder runProcessBuilder = new ProcessBuilder("java", "Main");
            runProcessBuilder.directory(tempDir.toFile());
            runProcessBuilder.redirectErrorStream(true);
            runProcess = runProcessBuilder.start();
            boolean finished = runProcess.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                runProcess.destroyForcibly();
                return "Time Limit Exceeded (Timeout)";
            }
            if (runProcess.exitValue() != 0) {
                return readProcessOutput(runProcess, consoleCharset);
            }
            return null;
        } finally {
            if (runProcess != null && runProcess.isAlive()) runProcess.destroyForcibly();
            if (tempDir != null) {
                try {
                    FileUtils.deleteDirectory(tempDir.toFile());
                } catch (IOException e) {
                    System.err.println("Failed to delete temp directory: " + e.getMessage());
                }
            }
        }
    }

    private static String readProcessOutput(Process process, Charset charset) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream(), charset))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }
        return output.toString();
    }

    public static boolean executePythonCode(String solutionCode, String testCode) throws Exception {
        Path tempDir = null;
        Process runProcess = null;
        try {
            tempDir = Files.createTempDirectory("python_exec");
            File mainFile = new File(tempDir.toFile(), "Main.py");
            String fullCode = solutionCode + "\n\n" + testCode;
            Files.writeString(mainFile.toPath(), fullCode, StandardCharsets.UTF_8);

            // 执行 Main 类
            ProcessBuilder runProcessBuilder = new ProcessBuilder("python", "Main.py");
            runProcessBuilder.directory(tempDir.toFile());
            runProcessBuilder.redirectErrorStream(true);
            runProcess = runProcessBuilder.start();
            boolean finished = runProcess.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                runProcess.destroyForcibly();
                return false;
            }
            return runProcess.exitValue() == 0;
        } finally {
            if (runProcess != null && runProcess.isAlive()) runProcess.destroyForcibly();
            if (tempDir != null) {
                try {
                    FileUtils.deleteDirectory(tempDir.toFile());
                } catch (IOException e) {
                    System.err.println("Failed to delete temp directory: " + e.getMessage());
                }
            }
        }
    }

    public static String executePythonCode2(String solutionCode, String testCode) throws Exception {
        Path tempDir = null;
        Process runProcess = null;
        try {
            tempDir = Files.createTempDirectory("python_exec");
            File mainFile = new File(tempDir.toFile(), "main.py");
            String fullCode = solutionCode + "\n\n" + testCode;
            Files.writeString(mainFile.toPath(), fullCode, StandardCharsets.UTF_8);
            Charset consoleCharset = System.getProperty("os.name").toLowerCase().contains("win")
                    ? Charset.forName("GBK")
                    : StandardCharsets.UTF_8;

            // 执行 Main 类
            ProcessBuilder runProcessBuilder = new ProcessBuilder("python", "main.py");
            runProcessBuilder.directory(tempDir.toFile());
            runProcessBuilder.redirectErrorStream(true);
            runProcess = runProcessBuilder.start();
            boolean finished = runProcess.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                runProcess.destroyForcibly();
                return "Time Limit Exceeded (Timeout)";
            }
            if (runProcess.exitValue() != 0) {
                return readProcessOutput(runProcess, consoleCharset);
            }
            return null;
        } finally {
            if (runProcess != null && runProcess.isAlive()) runProcess.destroyForcibly();
            if (tempDir != null) {
                try {
                    FileUtils.deleteDirectory(tempDir.toFile());
                } catch (IOException e) {
                    System.err.println("Failed to delete temp directory: " + e.getMessage());
                }
            }
        }
    }
}
