package magic.experiment;

import magic.data.DataProvider;
import magic.dto.GenerateCode4CoderEval;
import magic.service.Baseline2Agent;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Baseline2 {

    private static final Baseline2Agent baseline2Agent = new Baseline2Agent();
    private static final String outputFile = "magic_replicationpackage/src/main/resources/codereval/baseline2/deepseek-baseline2-gpt-java.jsonl";
    private static final String outputFile2 = "magic_replicationpackage/src/main/resources/codereval/baseline2/gpt-5-baseline2-gpt-java.jsonl";
    private static final String outputFile3 = "magic_replicationpackage/src/main/resources/codereval/baseline2/deepseek-baseline2-gpt-python.jsonl";
    private static final String outputFile4 = "magic_replicationpackage/src/main/resources/codereval/baseline2/gpt-5-baseline2-gpt-python.jsonl";

    public static void main(String[] args) throws Exception {
        a();
        b();
        c();
        d();
    }

    public static void a() throws IOException {
        List<GenerateCode4CoderEval> generateCodes = DataProvider.readGenerateCode4CoderEval("codereval/coding/deepseek-coding-java.jsonl");
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            for (GenerateCode4CoderEval entry : generateCodes) {
                long startTime = System.nanoTime();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = baseline2Agent.fixJavaIssue(generatedCode);
                long endTime = System.nanoTime();

                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", entry.getTaskId());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", improvedCode);
                json.put("nanoTime", String.valueOf(endTime - startTime));
                writer.write(mapper.writeValueAsString(json));
                writer.newLine();
            }
        }
    }

    public static void b() throws IOException {
        List<GenerateCode4CoderEval> generateCodes = DataProvider.readGenerateCode4CoderEval("codereval/coding/gpt-5-coding-java.jsonl");
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile2, true))) {
            for (GenerateCode4CoderEval entry : generateCodes) {
                long startTime = System.nanoTime();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = baseline2Agent.fixJavaIssue(generatedCode);
                long endTime = System.nanoTime();

                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", entry.getTaskId());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", improvedCode);
                json.put("nanoTime", String.valueOf(endTime - startTime));
                writer.write(mapper.writeValueAsString(json));
                writer.newLine();
            }
        }
    }

    public static void c() throws IOException {
        List<GenerateCode4CoderEval> generateCodes = DataProvider.readGenerateCode4CoderEval("codereval/coding/deepseek-coding-python.jsonl");
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile3, true))) {
            for (GenerateCode4CoderEval entry : generateCodes) {
                long startTime = System.nanoTime();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = baseline2Agent.fixPythonIssue(generatedCode);
                long endTime = System.nanoTime();

                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", entry.getTaskId());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", improvedCode);
                json.put("nanoTime", String.valueOf(endTime - startTime));
                writer.write(mapper.writeValueAsString(json));
                writer.newLine();
            }
        }
    }

    public static void d() throws IOException {
        List<GenerateCode4CoderEval> generateCodes = DataProvider.readGenerateCode4CoderEval("codereval/coding/gpt-5-coding-python.jsonl");
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile4, true))) {
            for (GenerateCode4CoderEval entry : generateCodes) {
                long startTime = System.nanoTime();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = baseline2Agent.fixPythonIssue(generatedCode);
                long endTime = System.nanoTime();

                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", entry.getTaskId());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", improvedCode);
                json.put("nanoTime", String.valueOf(endTime - startTime));
                writer.write(mapper.writeValueAsString(json));
                writer.newLine();
            }
        }
    }
}
