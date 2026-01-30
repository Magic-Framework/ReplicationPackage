package magic.experiment;

import com.fasterxml.jackson.databind.ObjectMapper;
import magic.data.DataProvider;
import magic.dto.CodeViolation;
import magic.dto.CoderEval4Java;
import magic.dto.CoderEval4Python;
import magic.dto.GenerateCode4CoderEval;
import magic.service.IssuesFixingAgent3;
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

public class MagicWithoutDSL {

    private static final IssuesFixingAgent3 fixingAgent = new IssuesFixingAgent3();
    private static final RuntimeFixingAgent runtimeFixingAgent = new RuntimeFixingAgent();
    private static final ValidationAgent validationAgent = new ValidationAgent();
    private static final JavaCodeChecker javaCodeChecker = new JavaCodeChecker();
    private static final PythonCodeChecker pythonCodeChecker = new PythonCodeChecker();
    private static final Map<String, CoderEval4Java> taskEntryMap4Java = new LinkedHashMap<>();
    private static final Map<String, CoderEval4Python> taskEntryMap4Python = new LinkedHashMap<>();
    private static final String outputFile = "magic_replicationpackage/src/main/resources/codereval/magic/deepseek-without-dsl-java.jsonl";
    private static final String outputFile2 = "magic_replicationpackage/src/main/resources/codereval/magic/gpt-5-without-dsl-java.jsonl";
    private static final String outputFile3 = "magic_replicationpackage/src/main/resources/codereval/magic/deepseek-without-dsl-python.jsonl";
    private static final String outputFile4 = "magic_replicationpackage/src/main/resources/codereval/magic/gpt-5-without-dsl-python.jsonl";

    public static void main(String[] args) throws Exception {
        List<CoderEval4Java> taskEntries4Java = DataProvider.readCoderEval4Java();
        for (CoderEval4Java taskEntry : taskEntries4Java) {
            taskEntryMap4Java.put(taskEntry.getId(), taskEntry);
        }
        List<CoderEval4Python> taskEntries4Python = DataProvider.readCoderEval4Python();
        for (CoderEval4Python taskEntry : taskEntries4Python) {
            taskEntryMap4Python.put(taskEntry.getId(), taskEntry);
        }

        a();
        b();
        c();
        d();
    }

    public static void a() throws Exception {
        List<GenerateCode4CoderEval> generateCodes = DataProvider
                .readGenerateCode4CoderEval("codereval/coding/deepseek-coding-java.jsonl");
        ObjectMapper mapper = new ObjectMapper();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            for (GenerateCode4CoderEval entry : generateCodes) {
                long startTime = System.nanoTime();
                fixingAgent.clearMemory();
                runtimeFixingAgent.clearMemory();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = generatedCode;
                String bestCode = generatedCode;

                CoderEval4Java coderEval4Java = taskEntryMap4Java.get(taskId);
                String fileContent = coderEval4Java.getFileContent();
                int startLine = Integer.parseInt(coderEval4Java.getLineno());
                int endLine = Integer.parseInt(coderEval4Java.getEndLineno());
                List<CodeViolation> violations = javaCodeChecker.checkIssues(fileContent, improvedCode, startLine,
                        endLine);
                int bestViolationCount = violations.size();
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("iteration", String.valueOf(0));
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = fixingAgent.fixJavaIssue(improvedCode, violations, startLine);
                int i;
                for (i = 1; i < 5; i++) {
                    boolean isPass = TestUtils.execJava(taskId, improvedCode);
                    if (isPass) {
                        List<CodeViolation> newViolations = javaCodeChecker.checkIssues(fileContent, improvedCode,
                                startLine, endLine);
                        if (newViolations.isEmpty()) {
                            bestCode = improvedCode;
                            break;
                        }
                        int currentViolationCount = newViolations.size();
                        if (currentViolationCount < bestViolationCount) {
                            bestViolationCount = currentViolationCount;
                            bestCode = improvedCode;
                        }
                        improvedCode = fixingAgent.fixJavaIssue(improvedCode, newViolations, startLine);
                    } else {
                        String errorMessage = validationAgent.validateJavaCode(bestCode, improvedCode);
                        improvedCode = runtimeFixingAgent.fixJavaCode(improvedCode, errorMessage);
                    }
                }
                long endTime = System.nanoTime();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
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
        List<GenerateCode4CoderEval> generateCodes = DataProvider
                .readGenerateCode4CoderEval("codereval/coding/gpt-5-coding-java.jsonl");
        ObjectMapper mapper = new ObjectMapper();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile2, true))) {
            for (GenerateCode4CoderEval entry : generateCodes) {
                long startTime = System.nanoTime();
                fixingAgent.clearMemory();
                runtimeFixingAgent.clearMemory();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = generatedCode;
                String bestCode = generatedCode;

                CoderEval4Java coderEval4Java = taskEntryMap4Java.get(taskId);
                String fileContent = coderEval4Java.getFileContent();
                int startLine = Integer.parseInt(coderEval4Java.getLineno());
                int endLine = Integer.parseInt(coderEval4Java.getEndLineno());
                List<CodeViolation> violations = javaCodeChecker.checkIssues(fileContent, improvedCode, startLine,
                        endLine);
                int bestViolationCount = violations.size();
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("iteration", String.valueOf(0));
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = fixingAgent.fixJavaIssue(improvedCode, violations, startLine);
                int i;
                for (i = 1; i < 5; i++) {
                    boolean isPass = TestUtils.execJava(taskId, improvedCode);
                    if (isPass) {
                        List<CodeViolation> newViolations = javaCodeChecker.checkIssues(fileContent, improvedCode,
                                startLine, endLine);
                        if (newViolations.isEmpty()) {
                            bestCode = improvedCode;
                            break;
                        }
                        int currentViolationCount = newViolations.size();
                        if (currentViolationCount < bestViolationCount) {
                            bestViolationCount = currentViolationCount;
                            bestCode = improvedCode;
                        }
                        improvedCode = fixingAgent.fixJavaIssue(improvedCode, newViolations, startLine);
                    } else {
                        String errorMessage = validationAgent.validateJavaCode(bestCode, improvedCode);
                        improvedCode = runtimeFixingAgent.fixJavaCode(improvedCode, errorMessage);
                    }
                }
                long endTime = System.nanoTime();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
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
        List<GenerateCode4CoderEval> generateCodes = DataProvider
                .readGenerateCode4CoderEval("codereval/coding/deepseek-coding-python.jsonl");
        ObjectMapper mapper = new ObjectMapper();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile3, true))) {
            for (GenerateCode4CoderEval entry : generateCodes) {
                long startTime = System.nanoTime();
                fixingAgent.clearMemory();
                runtimeFixingAgent.clearMemory();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = generatedCode;
                String bestCode = generatedCode;

                CoderEval4Python coderEval4Python = taskEntryMap4Python.get(taskId);
                String fileContent = coderEval4Python.getFileContent();
                int startLine = Integer.parseInt(coderEval4Python.getLineno());
                int endLine = Integer.parseInt(coderEval4Python.getEndLineno());
                List<CodeViolation> violations = pythonCodeChecker.checkIssues(fileContent, improvedCode, startLine,
                        endLine);
                int bestViolationCount = violations.size();
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("iteration", String.valueOf(0));
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = fixingAgent.fixPythonIssue(improvedCode, violations, startLine);
                int i;
                for (i = 1; i < 5; i++) {
                    boolean isPass = TestUtils.execPython(taskId, improvedCode);
                    if (isPass) {
                        List<CodeViolation> newViolations = pythonCodeChecker.checkIssues(fileContent, improvedCode,
                                startLine, endLine);
                        if (newViolations.isEmpty()) {
                            bestCode = improvedCode;
                            break;
                        }
                        int currentViolationCount = newViolations.size();
                        if (currentViolationCount < bestViolationCount) {
                            bestViolationCount = currentViolationCount;
                            bestCode = improvedCode;
                        }
                        improvedCode = fixingAgent.fixPythonIssue(improvedCode, newViolations, startLine);
                    } else {
                        String errorMessage = validationAgent.validatePythonCode(bestCode, improvedCode);
                        improvedCode = runtimeFixingAgent.fixPythonCode(improvedCode, errorMessage);
                    }
                }
                long endTime = System.nanoTime();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
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
        List<GenerateCode4CoderEval> generateCodes = DataProvider
                .readGenerateCode4CoderEval("codereval/coding/gpt-5-coding-python.jsonl");
        ObjectMapper mapper = new ObjectMapper();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile4, true))) {
            for (GenerateCode4CoderEval entry : generateCodes) {
                long startTime = System.nanoTime();
                fixingAgent.clearMemory();
                runtimeFixingAgent.clearMemory();
                String taskId = entry.getTaskId();
                System.out.println("Starting code fixing... : " + taskId);
                String generatedCode = entry.getGeneratedCode();
                String improvedCode = generatedCode;
                String bestCode = generatedCode;

                CoderEval4Python coderEval4Python = taskEntryMap4Python.get(taskId);
                String fileContent = coderEval4Python.getFileContent();
                int startLine = Integer.parseInt(coderEval4Python.getLineno());
                int endLine = Integer.parseInt(coderEval4Python.getEndLineno());
                List<CodeViolation> violations = pythonCodeChecker.checkIssues(fileContent, improvedCode, startLine,
                        endLine);
                int bestViolationCount = violations.size();
                if (violations.isEmpty()) {
                    Map<String, String> json = new LinkedHashMap<>();
                    json.put("taskId", taskId);
                    json.put("generatedCode", generatedCode);
                    json.put("improvedCode", improvedCode);
                    json.put("iteration", String.valueOf(0));
                    json.put("nanoTime", String.valueOf(0));
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    continue;
                }
                improvedCode = fixingAgent.fixPythonIssue(improvedCode, violations, startLine);
                int i;
                for (i = 1; i < 5; i++) {
                    boolean isPass = TestUtils.execPython(taskId, improvedCode);
                    if (isPass) {
                        List<CodeViolation> newViolations = pythonCodeChecker.checkIssues(fileContent, improvedCode,
                                startLine, endLine);
                        if (newViolations.isEmpty()) {
                            bestCode = improvedCode;
                            break;
                        }
                        int currentViolationCount = newViolations.size();
                        if (currentViolationCount < bestViolationCount) {
                            bestViolationCount = currentViolationCount;
                            bestCode = improvedCode;
                        }
                        improvedCode = fixingAgent.fixPythonIssue(improvedCode, newViolations, startLine);
                    } else {
                        String errorMessage = validationAgent.validatePythonCode(bestCode, improvedCode);
                        improvedCode = runtimeFixingAgent.fixPythonCode(improvedCode, errorMessage);
                    }
                }
                long endTime = System.nanoTime();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
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
