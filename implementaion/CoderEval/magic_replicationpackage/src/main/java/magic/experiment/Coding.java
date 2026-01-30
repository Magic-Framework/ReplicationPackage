package magic.experiment;

import magic.config.LLMConfig;
import magic.data.DataProvider;
import magic.dto.CoderEval4Java;
import magic.dto.CoderEval4Python;
import magic.service.CodingAgent;
import magic.util.TestUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Coding {

    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        CompletableFuture<Void> task1 = CompletableFuture.runAsync(() -> {
            try {
                generateJavaCode4DeepSeek();
            } catch (Exception e) {
                System.err.println("任务1出错: " + e.getMessage());
            }
        }, executor);

        CompletableFuture<Void> task2 = CompletableFuture.runAsync(() -> {
            try {
                generatePythonCode4DeepSeek();
            } catch (Exception e) {
                System.err.println("任务2出错: " + e.getMessage());
            }
        }, executor);

        CompletableFuture<Void> task3 = CompletableFuture.runAsync(() -> {
            try {
                generateJavaCode4GPT();
            } catch (Exception e) {
                System.err.println("任务3出错: " + e.getMessage());
            }
        }, executor);

        CompletableFuture<Void> task4 = CompletableFuture.runAsync(() -> {
            try {
                generatePythonCode4GPT();
            } catch (Exception e) {
                System.err.println("任务4出错: " + e.getMessage());
            }
        }, executor);

        CompletableFuture.allOf(task1, task2, task3, task4).join();
        executor.shutdown();
    }

    public static void generateJavaCode4GPT() throws Exception {
        CodingAgent codingAgent = new CodingAgent(LLMConfig.getGPT5Model());
        List<CoderEval4Java> taskEntries = DataProvider.readCoderEval4Java();
        ObjectMapper mapper = new ObjectMapper();
        String outputFile = "magic_replicationpackage/src/main/resources/codereval/coding/gpt-5-coding-java.jsonl";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            int id = 0;
            for (CoderEval4Java taskEntry : taskEntries) {
                id++;
                String taskId = taskEntry.getId();
                System.out.println("starting... GPT-5 for Java: " + id);
                String prompt = "/**\n * " + taskEntry.getHumanLabel() + " \n */";
                String methodSignature = taskEntry.getCode().split("\n ")[0];
                prompt = (prompt + "\n" + methodSignature).strip();
                String context = taskEntry.getAllContext();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
                boolean isOk = false;
                for (int i = 0; i < 5; i++) {
                    String code = codingAgent.generateJavaCode(prompt, context);
                    json.put("generatedCode", code);
                    boolean flag = TestUtils.execJava(taskId, code);
                    if (flag) {
                        isOk = true;
                        break;
                    }
                }
                if (isOk) {
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    writer.flush();
                } else {
                    System.out.println("failed... " + id);
                }
            }
        }
    }

    public static void generatePythonCode4GPT() throws Exception {
        CodingAgent codingAgent = new CodingAgent(LLMConfig.getGPT5Model());
        List<CoderEval4Python> taskEntries = DataProvider.readCoderEval4Python();
        ObjectMapper mapper = new ObjectMapper();
        String outputFile = "magic_replicationpackage/src/main/resources/codereval/coding/gpt-5-coding-python.jsonl";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            int id = 0;
            for (CoderEval4Python taskEntry : taskEntries) {
                id++;
                String taskId = taskEntry.getId();
                System.out.println("starting... GPT-5 for Python: " + id);
                String prompt = "\"\"\" " + taskEntry.getHumanLabel() + " \"\"\"";
                String methodSignature = taskEntry.getCode().split("\n ")[0];
                prompt = (methodSignature + "\n" + prompt.indent(4)).strip();
                String context = taskEntry.getAllContext();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
                boolean isOk = false;
                for (int i = 0; i < 5; i++) {
                    String code = codingAgent.generatePythonCode(prompt, context);
                    json.put("generatedCode", code);
                    boolean flag = TestUtils.execPython(taskId, code);
                    if (flag) {
                        isOk = true;
                        break;
                    }
                }
                if (isOk) {
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    writer.flush();
                } else {
                    System.out.println("failed... " + id);
                }
            }
        }
    }

    public static void generateJavaCode4DeepSeek() throws Exception {
        CodingAgent codingAgent = new CodingAgent(LLMConfig.getDeepSeekModel());
        List<CoderEval4Java> taskEntries = DataProvider.readCoderEval4Java();
        ObjectMapper mapper = new ObjectMapper();
        String outputFile = "magic_replicationpackage/src/main/resources/codereval/coding/deepseek-coding-java.jsonl";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            int id = 0;
            for (CoderEval4Java taskEntry : taskEntries) {
                id++;
                String taskId = taskEntry.getId();
                System.out.println("starting... DeepSeek for Java: " + id);
                String prompt = "/**\n * " + taskEntry.getHumanLabel() + " \n */";
                String methodSignature = taskEntry.getCode().split("\n ")[0].strip();
                prompt = (prompt + "\n" + methodSignature).strip();
                String context = taskEntry.getAllContext();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
                boolean isOk = false;
                for (int i = 0; i < 5; i++) {
                    String code = codingAgent.generateJavaCode(prompt, context);
                    json.put("generatedCode", code);
                    boolean flag = TestUtils.execJava(taskId, code);
                    if (flag) {
                        isOk = true;
                        break;
                    }
                }
                if (isOk) {
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    writer.flush();
                } else {
                    System.out.println("failed... " + id);
                }
            }
        }
    }

    public static void generatePythonCode4DeepSeek() throws Exception {
        CodingAgent codingAgent = new CodingAgent(LLMConfig.getDeepSeekModel());
        List<CoderEval4Python> taskEntries = DataProvider.readCoderEval4Python();
        ObjectMapper mapper = new ObjectMapper();
        String outputFile = "magic_replicationpackage/src/main/resources/codereval/coding/deepseek-coding-python.jsonl";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            int id = 0;
            for (CoderEval4Python taskEntry : taskEntries) {
                id++;
                String taskId = taskEntry.getId();
                System.out.println("starting... DeepSeek for Python: " + id);
                String prompt = "\"\"\" " + taskEntry.getHumanLabel() + " \"\"\"";
                String methodSignature = taskEntry.getCode().split("\n ")[0].strip();
                prompt = (methodSignature + "\n" + prompt.indent(4)).strip();
                String context = taskEntry.getAllContext();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
                boolean isOk = false;
                for (int i = 0; i < 5; i++) {
                    String code = codingAgent.generatePythonCode(prompt, context);
                    json.put("generatedCode", code);
                    boolean flag = TestUtils.execPython(taskId, code);
                    if (flag) {
                        isOk = true;
                        break;
                    }
                }
                if (isOk) {
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    writer.flush();
                } else {
                    System.out.println("failed... " + id);
                }
            }
        }
    }
}
