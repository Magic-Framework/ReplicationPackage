package magic.experiment;

import magic.data.DataProvider;
import magic.dto.CodeViolation;
import magic.dto.GenerateCode;
import magic.service.Baseline1Agent;
import magic.tool.JavaCodeChecker;
import magic.tool.PythonCodeChecker;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Baseline1 {

    private static final Baseline1Agent baseline1Agent = new Baseline1Agent();
    private static final JavaCodeChecker javaCodeChecker = new JavaCodeChecker();
    private static final PythonCodeChecker pythonCodeChecker = new PythonCodeChecker();
    private static final String outputFile = "E:/deepseek-baseline1-gpt-java.jsonl";
    private static final String outputFile2 = "E:/gpt-5-baseline1-gpt-java.jsonl";
    private static final String outputFile3 = "E:/deepseek-baseline1-gpt-python.jsonl";
    private static final String outputFile4 = "E:/gpt-5-baseline1-gpt-python.jsonl";

    public static void main(String[] args) throws Exception {
        a();
        b();
        c();
        d();
    }

    public static void a() throws Exception {
        List<GenerateCode> generateCodes = DataProvider.readGenerateCode("coding/deepseek-coding-java.jsonl");
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            for (GenerateCode entry : generateCodes) {
                long startTime = System.nanoTime();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = generatedCode;
                List<CodeViolation> violations = javaCodeChecker.checkIssues(improvedCode);
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("test", entry.getTest());
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = baseline1Agent.fixJavaIssue(improvedCode, violations);
                long endTime = System.nanoTime();

                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", entry.getTaskId());
                json.put("test", entry.getTest());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", improvedCode);
                json.put("nanoTime", String.valueOf(endTime - startTime));
                writer.write(mapper.writeValueAsString(json));
                writer.newLine();
            }
        }
    }

    public static void b() throws Exception {
        List<GenerateCode> generateCodes = DataProvider.readGenerateCode("coding/gpt-5-coding-java.jsonl");
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile2, true))) {
            for (GenerateCode entry : generateCodes) {
                long startTime = System.nanoTime();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = generatedCode;
                List<CodeViolation> violations = javaCodeChecker.checkIssues(improvedCode);
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("test", entry.getTest());
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = baseline1Agent.fixJavaIssue(improvedCode, violations);
                long endTime = System.nanoTime();

                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", entry.getTaskId());
                json.put("test", entry.getTest());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", improvedCode);
                json.put("nanoTime", String.valueOf(endTime - startTime));
                writer.write(mapper.writeValueAsString(json));
                writer.newLine();
            }
        }
    }

    public static void c() throws Exception {
        List<GenerateCode> generateCodes = DataProvider.readGenerateCode("coding/deepseek-coding-python.jsonl");
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile3, true))) {
            for (GenerateCode entry : generateCodes) {
                long startTime = System.nanoTime();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = generatedCode;
                List<CodeViolation> violations = pythonCodeChecker.checkIssues(improvedCode);
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("test", entry.getTest());
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = baseline1Agent.fixPythonIssue(improvedCode, violations);
                long endTime = System.nanoTime();

                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", entry.getTaskId());
                json.put("test", entry.getTest());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", improvedCode);
                json.put("nanoTime", String.valueOf(endTime - startTime));
                writer.write(mapper.writeValueAsString(json));
                writer.newLine();
            }
        }
    }

    public static void d() throws Exception {
        List<GenerateCode> generateCodes = DataProvider.readGenerateCode("coding/gpt-5-coding-python.jsonl");
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile4, true))) {
            for (GenerateCode entry : generateCodes) {
                long startTime = System.nanoTime();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = generatedCode;
                List<CodeViolation> violations = pythonCodeChecker.checkIssues(improvedCode);
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("test", entry.getTest());
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = baseline1Agent.fixPythonIssue(improvedCode, violations);
                long endTime = System.nanoTime();

                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", entry.getTaskId());
                json.put("test", entry.getTest());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", improvedCode);
                json.put("nanoTime", String.valueOf(endTime - startTime));
                writer.write(mapper.writeValueAsString(json));
                writer.newLine();
            }
        }
    }
}
