package magic.experiment;

import magic.data.DataProvider;
import magic.dto.CodeViolation;
import magic.dto.CoderEval4Java;
import magic.dto.CoderEval4Python;
import magic.dto.ImprovedCode4CoderEval;
import magic.tool.JavaCodeChecker;
import magic.tool.PythonCodeChecker;
import magic.util.TestUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Evaluation {

    private static final JavaCodeChecker javaCodeChecker = new JavaCodeChecker();
    private static final PythonCodeChecker pythonCodeChecker = new PythonCodeChecker();
    private static final Map<String, CoderEval4Java> taskEntryMap4Java = new LinkedHashMap<>();
    private static final Map<String, CoderEval4Python> taskEntryMap4Python = new LinkedHashMap<>();

    public static void main(String[] args) throws Exception {
        List<CoderEval4Java> taskEntries4Java = DataProvider.readCoderEval4Java();
        for (CoderEval4Java taskEntry : taskEntries4Java) {
            taskEntryMap4Java.put(taskEntry.getId(), taskEntry);
        }
        List<CoderEval4Python> taskEntries4Python = DataProvider.readCoderEval4Python();
        for (CoderEval4Python taskEntry : taskEntries4Python) {
            taskEntryMap4Python.put(taskEntry.getId(), taskEntry);
        }

        String magic_file1 = "codereval/magic/deepseek-output-java.jsonl";
        String magic_file2 = "codereval/magic/gpt-5-output-java.jsonl";
        String magic_file3 = "codereval/magic/deepseek-output-python.jsonl";
        String magic_file4 = "codereval/magic/gpt-5-output-python.jsonl";
        System.out.println("magic");
        run(magic_file1, magic_file2, magic_file3, magic_file4);
        System.out.println();

        /* String baseline1_file1 = "codereval/baseline1/deepseek-baseline1-java.jsonl";
        String baseline1_file2 = "codereval/baseline1/gpt-5-baseline1-java.jsonl";
        String baseline1_file3 = "codereval/baseline1/deepseek-baseline1-python.jsonl";
        String baseline1_file4 = "codereval/baseline1/gpt-5-baseline1-python.jsonl";
        System.out.println("baseline1");
        run(baseline1_file1, baseline1_file2, baseline1_file3, baseline1_file4);
        System.out.println(); 

        String baseline1_gpt_file1 = "codereval/baseline1/deepseek-baseline1-gpt-java.jsonl";
        String baseline1_gpt_file2 = "codereval/baseline1/gpt-5-baseline1-gpt-java.jsonl";
        String baseline1_gpt_file3 = "codereval/baseline1/deepseek-baseline1-gpt-python.jsonl";
        String baseline1_gpt_file4 = "codereval/baseline1/gpt-5-baseline1-gpt-python.jsonl";
        System.out.println("baseline1-gpt");
        run(baseline1_gpt_file1, baseline1_gpt_file2, baseline1_gpt_file3, baseline1_gpt_file4);
        System.out.println();

        String baseline2_file1 = "codereval/baseline2/deepseek-baseline2-java.jsonl";
        String baseline2_file2 = "codereval/baseline2/gpt-5-baseline2-java.jsonl";
        String baseline2_file3 = "codereval/baseline2/deepseek-baseline2-python.jsonl";
        String baseline2_file4 = "codereval/baseline2/gpt-5-baseline2-python.jsonl";
        System.out.println("baseline2");
        run(baseline2_file1, baseline2_file2, baseline2_file3, baseline2_file4);
        System.out.println();

        String baseline2_gpt_file1 = "codereval/baseline2/deepseek-baseline2-gpt-java.jsonl";
        String baseline2_gpt_file2 = "codereval/baseline2/gpt-5-baseline2-gpt-java.jsonl";
        String baseline2_gpt_file3 = "codereval/baseline2/deepseek-baseline2-gpt-python.jsonl";
        String baseline2_gpt_file4 = "codereval/baseline2/gpt-5-baseline2-gpt-python.jsonl";
        System.out.println("baseline2-gpt");
        run(baseline2_gpt_file1, baseline2_gpt_file2, baseline2_gpt_file3, baseline2_gpt_file4);
        System.out.println();

        String baseline3_gpt_file1 = "codereval/baseline3/deepseek-baseline3-java.jsonl";
        String baseline3_gpt_file2 = "codereval/baseline3/gpt-5-baseline3-java.jsonl";
        String baseline3_gpt_file3 = "codereval/baseline3/deepseek-baseline3-python.jsonl";
        String baseline3_gpt_file4 = "codereval/baseline3/gpt-5-baseline3-python.jsonl";
        System.out.println("baseline3");
        run(baseline3_gpt_file1, baseline3_gpt_file2, baseline3_gpt_file3, baseline3_gpt_file4);
        System.out.println();

        String magic_without_dsl_file1 = "codereval/magic/deepseek-without-dsl-java.jsonl";
        String magic_without_dsl_file2 = "codereval/magic/gpt-5-without-dsl-java.jsonl";
        String magic_without_dsl_file3 = "codereval/magic/deepseek-without-dsl-python.jsonl";
        String magic_without_dsl_file4 = "codereval/magic/gpt-5-without-dsl-python.jsonl";
        System.out.println("magic-without-dsl");
        run(magic_without_dsl_file1, magic_without_dsl_file2, magic_without_dsl_file3, magic_without_dsl_file4);
        System.out.println();

        String magic_without_validation_file1 = "codereval/magic/deepseek-without-validation-java.jsonl";
        String magic_without_validation_file2 = "codereval/magic/gpt-5-without-validation-java.jsonl";
        String magic_without_validation_file3 = "codereval/magic/deepseek-without-validation-python.jsonl";
        String magic_without_validation_file4 = "codereval/magic/gpt-5-without-validation-python.jsonl";
        System.out.println("magic-without-validation");
        run(magic_without_validation_file1, magic_without_validation_file2, magic_without_validation_file3, magic_without_validation_file4);
        System.out.println(); */
    }

    public static void run(String fileName1, String fileName2, String fileName3, String fileName4) throws Exception {
        List<ImprovedCode4CoderEval> codeList1 = DataProvider.readImprovedCode4CoderEval(fileName1);
        List<ImprovedCode4CoderEval> codeList2 = DataProvider.readImprovedCode4CoderEval(fileName2);
        List<ImprovedCode4CoderEval> codeList3 = DataProvider.readImprovedCode4CoderEval(fileName3);
        List<ImprovedCode4CoderEval> codeList4 = DataProvider.readImprovedCode4CoderEval(fileName4);
        int oldIssues = 0;
        int newIssues = 0;
        int unresolvedIssues = 0;
        int bugs = 0;

        for (ImprovedCode4CoderEval code : codeList1) {
            String taskId = code.getTaskId();
            System.out.println("Processing task: " + taskId);
            String generatedCode = code.getGeneratedCode();
            String improvedCode = code.getImprovedCode();
            boolean b = TestUtils.execJava(taskId, improvedCode);
            if (!b) {
                bugs++;
            }
            CoderEval4Java coderEval4Java = taskEntryMap4Java.get(taskId);
            String fileContent = coderEval4Java.getFileContent();
            int startLine = Integer.parseInt(coderEval4Java.getLineno());
            int endLine = Integer.parseInt(coderEval4Java.getEndLineno());
            List<CodeViolation> codeViolations1 = javaCodeChecker.checkIssues(fileContent, generatedCode, startLine,
                    endLine);
            List<CodeViolation> codeViolations2 = javaCodeChecker.checkIssues(fileContent, improvedCode, startLine,
                    endLine);
            oldIssues += codeViolations1.size();
            unresolvedIssues += codeViolations2.size();
            for (CodeViolation violation : codeViolations2) {
                if (!codeViolations1.contains(violation)) {
                    newIssues++;
                }
            }
        }

        for (ImprovedCode4CoderEval code : codeList2) {
            String taskId = code.getTaskId();
            System.out.println("Processing task: " + taskId);
            String generatedCode = code.getGeneratedCode();
            String improvedCode = code.getImprovedCode();
            boolean b = TestUtils.execJava(taskId, improvedCode);
            if (!b) {
                bugs++;
            }
            CoderEval4Java coderEval4Java = taskEntryMap4Java.get(taskId);
            String fileContent = coderEval4Java.getFileContent();
            int startLine = Integer.parseInt(coderEval4Java.getLineno());
            int endLine = Integer.parseInt(coderEval4Java.getEndLineno());
            List<CodeViolation> codeViolations1 = javaCodeChecker.checkIssues(fileContent, generatedCode, startLine,
                    endLine);
            List<CodeViolation> codeViolations2 = javaCodeChecker.checkIssues(fileContent, improvedCode, startLine,
                    endLine);
            oldIssues += codeViolations1.size();
            unresolvedIssues += codeViolations2.size();
            for (CodeViolation violation : codeViolations2) {
                if (!codeViolations1.contains(violation)) {
                    newIssues++;
                }
            }          
        }

        for (ImprovedCode4CoderEval code : codeList3) {
            String taskId = code.getTaskId();
            System.out.println("Processing task: " + taskId);
            String generatedCode = code.getGeneratedCode();
            String improvedCode = code.getImprovedCode();
            boolean b = TestUtils.execPython(taskId, improvedCode);
            if (!b) {
                bugs++;
            }
            CoderEval4Python coderEval4Python = taskEntryMap4Python.get(taskId);
            String fileContent = coderEval4Python.getFileContent();
            int startLine = Integer.parseInt(coderEval4Python.getLineno());
            int endLine = Integer.parseInt(coderEval4Python.getEndLineno());
            List<CodeViolation> codeViolations1 = pythonCodeChecker.checkIssues(fileContent, generatedCode, startLine,
                    endLine);
            List<CodeViolation> codeViolations2 = pythonCodeChecker.checkIssues(fileContent, improvedCode, startLine,
                    endLine);
            System.out.println(improvedCode);
            System.out.println(codeViolations2);
            oldIssues += codeViolations1.size();
            unresolvedIssues += codeViolations2.size();
            for (CodeViolation violation : codeViolations2) {
                if (!codeViolations1.contains(violation)) {
                    newIssues++;
                }
            }
        }

        for (ImprovedCode4CoderEval code : codeList4) {
            String taskId = code.getTaskId();
            System.out.println("Processing task: " + taskId);
            String generatedCode = code.getGeneratedCode();
            String improvedCode = code.getImprovedCode();
            boolean b = TestUtils.execPython(taskId, improvedCode);
            if (!b) {
                bugs++;
            }
            CoderEval4Python coderEval4Python = taskEntryMap4Python.get(taskId);
            String fileContent = coderEval4Python.getFileContent();
            int startLine = Integer.parseInt(coderEval4Python.getLineno());
            int endLine = Integer.parseInt(coderEval4Python.getEndLineno());
            List<CodeViolation> codeViolations1 = pythonCodeChecker.checkIssues(fileContent, generatedCode, startLine,
                    endLine);
            List<CodeViolation> codeViolations2 = pythonCodeChecker.checkIssues(fileContent, improvedCode, startLine,
                    endLine);
            oldIssues += codeViolations1.size();
            unresolvedIssues += codeViolations2.size();
            for (CodeViolation violation : codeViolations2) {
                if (!codeViolations1.contains(violation)) {
                    newIssues++;
                }
            }
        }

        System.out.println("oldIssues, unresolvedIssues, newIssues, bugs");
        System.out.println(oldIssues + "\t" + unresolvedIssues + "\t" + newIssues + "\t" + bugs);
    }
}
