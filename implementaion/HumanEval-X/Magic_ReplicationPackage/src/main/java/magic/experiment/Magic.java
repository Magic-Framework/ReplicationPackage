package magic.experiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import magic.data.DataProvider;
import magic.dto.CodeViolation;
import magic.dto.GenerateCode;
import magic.service.IssuesFixingAgent;
import magic.service.RuntimeFixingAgent;
import magic.service.ValidationAgent;
import magic.tool.JavaCodeChecker;
import magic.tool.PythonCodeChecker;
import magic.util.TestUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Magic {

    private static final IssuesFixingAgent fixingAgent = new IssuesFixingAgent();
    private static final RuntimeFixingAgent runtimeFixingAgent = new RuntimeFixingAgent();
    private static final ValidationAgent validationAgent = new ValidationAgent();
    private static final JavaCodeChecker javaCodeChecker = new JavaCodeChecker();
    private static final PythonCodeChecker pythonCodeChecker = new PythonCodeChecker();
    private static final String outputFile = "E:/deepseek-output-java.jsonl";
    private static final String outputFile2 = "E:/gpt-5-output-java.jsonl";
    private static final String outputFile3 = "E:/deepseek-output-python.jsonl";
    private static final String outputFile4 = "E:/gpt-5-output-python.jsonl";

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
                fixingAgent.clearMemory();
                runtimeFixingAgent.clearMemory();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String test = entry.getTest();
                String improvedCode = generatedCode;
                String bestCode = generatedCode;
                List<CodeViolation> violations = javaCodeChecker.checkIssues(improvedCode);
                int bestViolationCount = violations.size();
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("test", entry.getTest());
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("iteration", String.valueOf(0));
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = fixingAgent.fixJavaIssue(improvedCode, violations);
                int i;
                for (i = 1; i < 5; i++) {
                    String error = TestUtils.executeJavaCode2(improvedCode, test);
                    if (error == null) {
                        List<CodeViolation> newViolations = javaCodeChecker.checkIssues(improvedCode);
                        if (newViolations.isEmpty()) {
                            bestCode = improvedCode;
                            break;
                        }
                        int currentViolationCount = newViolations.size();
                        if (currentViolationCount < bestViolationCount) {
                            bestCode = improvedCode;
                            bestViolationCount = currentViolationCount;
                        }
                        improvedCode = fixingAgent.fixJavaIssue(improvedCode, newViolations);
                    } else {
                        if (error.startsWith("Exception in thread \"main\" java.lang.AssertionError")) {
                            String errorMessage = validationAgent.validateJavaCode(bestCode, improvedCode);
                            improvedCode = runtimeFixingAgent.fixJavaCode(improvedCode, test, errorMessage);
                        } else improvedCode = runtimeFixingAgent.fixJavaCode(improvedCode, test, error);
                    }
                }
                long endTime = System.nanoTime();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
                json.put("test", entry.getTest());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", bestCode);
                json.put("iteration", String.valueOf(i + 1));
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
                fixingAgent.clearMemory();
                runtimeFixingAgent.clearMemory();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String test = entry.getTest();
                String improvedCode = generatedCode;
                String bestCode = generatedCode;
                List<CodeViolation> violations = javaCodeChecker.checkIssues(improvedCode);
                int bestViolationCount = violations.size();
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("test", entry.getTest());
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("iteration", String.valueOf(0));
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = fixingAgent.fixJavaIssue(improvedCode, violations);
                int i;
                for (i = 1; i < 5; i++) {
                    String error = TestUtils.executeJavaCode2(improvedCode, test);
                    if (error == null) {
                        List<CodeViolation> newViolations = javaCodeChecker.checkIssues(improvedCode);
                        if (newViolations.isEmpty()) {
                            bestCode = improvedCode;
                            break;
                        }
                        int currentViolationCount = newViolations.size();
                        if (currentViolationCount < bestViolationCount) {
                            bestCode = improvedCode;
                            bestViolationCount = currentViolationCount;
                        }
                        improvedCode = fixingAgent.fixJavaIssue(improvedCode, newViolations);
                    } else {
                        if (error.startsWith("Exception in thread \"main\" java.lang.AssertionError")) {
                            String errorMessage = validationAgent.validateJavaCode(bestCode, improvedCode);
                            improvedCode = runtimeFixingAgent.fixJavaCode(improvedCode, test, errorMessage);
                        } else improvedCode = runtimeFixingAgent.fixJavaCode(improvedCode, test, error);
                    }
                }
                long endTime = System.nanoTime();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
                json.put("test", entry.getTest());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", bestCode);
                json.put("iteration", String.valueOf(i + 1));
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
                fixingAgent.clearMemory();
                runtimeFixingAgent.clearMemory();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String test = entry.getTest();
                String improvedCode = generatedCode;
                String bestCode = generatedCode;
                List<CodeViolation> violations = pythonCodeChecker.checkIssues(improvedCode);
                int bestViolationCount = violations.size();
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("test", entry.getTest());
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("iteration", String.valueOf(0));
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = fixingAgent.fixPythonIssue(improvedCode, violations);
                int i;
                for (i = 1; i < 5; i++) {
                    String error = TestUtils.executePythonCode2(improvedCode, test);
                    if (error == null) {
                        List<CodeViolation> newViolations = pythonCodeChecker.checkIssues(improvedCode);
                        if (newViolations.isEmpty()) {
                            bestCode = improvedCode;
                            break;
                        }
                        int currentViolationCount = newViolations.size();
                        if (currentViolationCount < bestViolationCount) {
                            bestCode = improvedCode;
                            bestViolationCount = currentViolationCount;
                        }
                        improvedCode = fixingAgent.fixPythonIssue(improvedCode, newViolations);
                    } else {
                        if (error.endsWith("AssertionError\n")) {
                            String errorMessage = validationAgent.validatePythonCode(bestCode, improvedCode);
                            improvedCode = runtimeFixingAgent.fixPythonCode(improvedCode, test, errorMessage);
                        } else improvedCode = runtimeFixingAgent.fixPythonCode(improvedCode, test, error);
                    }
                }
                long endTime = System.nanoTime();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
                json.put("test", entry.getTest());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", bestCode);
                json.put("iteration", String.valueOf(i + 1));
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
                fixingAgent.clearMemory();
                runtimeFixingAgent.clearMemory();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String test = entry.getTest();
                String improvedCode = generatedCode;
                String bestCode = generatedCode;
                List<CodeViolation> violations = pythonCodeChecker.checkIssues(improvedCode);
                int bestViolationCount = violations.size();
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("test", entry.getTest());
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("iteration", String.valueOf(0));
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = fixingAgent.fixPythonIssue(improvedCode, violations);
                int i;
                for (i = 1; i < 5; i++) {
                    String error = TestUtils.executePythonCode2(improvedCode, test);
                    if (error == null) {
                        List<CodeViolation> newViolations = pythonCodeChecker.checkIssues(improvedCode);
                        if (newViolations.isEmpty()) {
                            bestCode = improvedCode;
                            break;
                        }
                        int currentViolationCount = newViolations.size();
                        if (currentViolationCount < bestViolationCount) {
                            bestCode = improvedCode;
                            bestViolationCount = currentViolationCount;
                        }
                        improvedCode = fixingAgent.fixPythonIssue(improvedCode, newViolations);
                    } else {
                        if (error.endsWith("AssertionError\n")) {
                            String errorMessage = validationAgent.validatePythonCode(bestCode, improvedCode);
                            improvedCode = runtimeFixingAgent.fixPythonCode(improvedCode, test, errorMessage);
                        } else {
                            improvedCode = runtimeFixingAgent.fixPythonCode(improvedCode, test, error);
                        }
                    }
                }
                long endTime = System.nanoTime();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
                json.put("test", entry.getTest());
                json.put("generatedCode", generatedCode);
                json.put("improvedCode", bestCode);
                json.put("iteration", String.valueOf(i + 1));
                json.put("nanoTime", String.valueOf(endTime - startTime));
                writer.write(mapper.writeValueAsString(json));
                writer.newLine();
            }
        }
    }
}
