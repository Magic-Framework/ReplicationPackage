package magic.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandUtils {

    public static List<String> executeCommand(ProcessBuilder processBuilder, String inputData) {
        List<String> outputLines = new ArrayList<>();

        // 将错误流重定向到标准输出流，这样我们只需要读取一个流
        processBuilder.redirectErrorStream(true);

        Process process = null;
        try {
            process = processBuilder.start();

            // 如果有需要写入标准输入的数据 (用于 flake8 和 pylint)
            if (inputData != null) {
                try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                    writer.write(inputData);
                    writer.flush(); // 确保所有数据都已写入
                }
            }
            // 关闭输入流，表示输入已完成
            process.getOutputStream().close();

            // 读取进程的输出
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Pylint 的输出可能包含不需要的星号行，可以过滤掉，但这里我们先收集所有行，让解析器处理
                    if (line.startsWith("stdin"))
                        outputLines.add(line);
                }
            }

            // 等待进程执行完毕，设置一个超时时间以防进程卡死
            boolean finished = process.waitFor(30, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly(); // 强制终止
                outputLines.add("ERROR: Code checking process timed out."); // 添加错误信息
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("执行命令时出错: " + e.getMessage());
            outputLines.add("ERROR: Failed to execute checker - " + e.getMessage());
        } finally {
            if (process != null) {
                process.destroy(); // 确保进程被终止
            }
        }

        return outputLines;
    }

    public static int getSeverityFromRule(String ruleName) {
        if (ruleName == null || ruleName.isEmpty()) {
            return 0;
        }
        char prefix = ruleName.charAt(0);
        return switch (prefix) {
            case 'F' -> 1;
            case 'E' -> 2;
            case 'W' -> 3;
            case 'C' -> 4;
            case 'R' -> 5;
            default -> 0;
        };
    }
}
