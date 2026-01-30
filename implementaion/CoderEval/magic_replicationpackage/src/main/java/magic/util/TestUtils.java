package magic.util;

import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.s;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            if (runProcess != null && runProcess.isAlive())
                runProcess.destroyForcibly();
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
            if (runProcess != null && runProcess.isAlive())
                runProcess.destroyForcibly();
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
            if (runProcess != null && runProcess.isAlive())
                runProcess.destroyForcibly();
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
            if (runProcess != null && runProcess.isAlive())
                runProcess.destroyForcibly();
            if (tempDir != null) {
                try {
                    FileUtils.deleteDirectory(tempDir.toFile());
                } catch (IOException e) {
                    System.err.println("Failed to delete temp directory: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {
        String taskId = "62e60ed4d76274f8a4026da0";
        String code = "def keys(self) -> List:\n    return self.__keys";
        boolean result = execPython(taskId, code);
        System.out.println(result);
    }

    public static boolean execJava(String taskId, String code) {
        String homePath = System.getProperty("user.dir");

        String travisDir = homePath + File.separator + "travis" + File.separator + "builds" + File.separator + "java";
        String inputFilePath = travisDir + File.separator + taskId + "_input.jsonl";
        String outputFilePath = travisDir + File.separator + taskId + "_input.jsonl_out.jsonl";
        String pythonScriptPath = travisDir + File.separator + "JavaExec.py";

        try {
            // Step 1: 使用 Jackson 生成并保存 JSONL
            writeJsonl(inputFilePath, taskId, code);

            // Step 2: 运行 Python 脚本
            // 参数1: HumanLabel_Input.jsonl (脚本内部通常处理相对路径，所以传文件名)
            // 参数2: 1
            runPython(pythonScriptPath, travisDir, taskId + "_input.jsonl", "1");

            // Step 3: 解析结果
            boolean isPass = parseIsPass(outputFilePath);
            Files.deleteIfExists(Paths.get(inputFilePath));
            Files.deleteIfExists(Paths.get(outputFilePath));
            return isPass;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean execPython(String taskId, String code) {
        String homePath = System.getProperty("user.dir");

        String travisDir = homePath + File.separator + "travis" + File.separator + "builds";
        String inputFilePath = travisDir + File.separator + taskId + "_input.jsonl";
        String outputFilePath = travisDir + File.separator + taskId + "_input.jsonl_out.jsonl";
        String pythonScriptPath = travisDir + File.separator + "PythonExec.py";

        try {
            // Step 1: 使用 Jackson 生成并保存 JSONL
            writeJsonl(inputFilePath, taskId, code);

            // Step 2: 运行 Python 脚本
            // 参数1: HumanLabel_Input.jsonl (脚本内部通常处理相对路径，所以传文件名)
            // 参数2: 1
            runPython(pythonScriptPath, travisDir, taskId + "_input.jsonl", "1");

            // Step 3: 解析结果
            boolean isPass = parseIsPass(outputFilePath);
            Files.deleteIfExists(Paths.get(inputFilePath));
            Files.deleteIfExists(Paths.get(outputFilePath));
            return isPass;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 使用 Jackson 写入 JSONL 格式
     */
    private static void writeJsonl(String path, String id, String code) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        root.put("_id", id);
        ArrayNode results = root.putArray("generate_results");
        results.add(code);

        // JSONL 要求不换行，且每行一个对象
        String jsonString = mapper.writeValueAsString(root);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write(jsonString);
            writer.newLine();
            writer.flush();
        }
    }

    /**
     * 执行外部 Python
     */
    private static void runPython(String scriptPath, String workingDir, String arg1, String arg2) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("python", scriptPath, arg1, arg2);
        pb.directory(new File(workingDir));
        pb.redirectErrorStream(true);
        Process process = pb.start();

        // 1. 等待进程结束，设置合理的超时
        boolean finished = process.waitFor(30, TimeUnit.SECONDS);
        if (!finished) {
            process.destroyForcibly(); // 强制杀死超时进程
        }

        // 2. 检查退出码（0通常表示成功）
        int exitCode = process.exitValue();
        if (exitCode != 0) {
            System.err.println("Python 脚本执行失败，退出码: " + exitCode);
        }
    }

    /**
     * 使用 Jackson 解析结果文件
     */
    private static boolean parseIsPass(String path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(path);
        if (!file.exists() || file.length() == 0)
            return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) {
                JsonNode root = mapper.readTree(line);

                // 1. 先获取 generate_results 数组
                JsonNode resultsArray = root.get("generate_results");

                // 2. 检查数组是否存在且不为空
                if (resultsArray != null && resultsArray.isArray() && resultsArray.size() > 0) {
                    // 3. 获取数组中的第一个对象，从中提取 is_pass
                    JsonNode firstResult = resultsArray.get(0);
                    JsonNode isPassNode = firstResult.get("is_pass");
                    if (isPassNode != null) {
                        return isPassNode.asBoolean();
                    }
                }
            }
        }
        return false;
    }
}
