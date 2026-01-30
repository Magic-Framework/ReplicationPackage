package magic.experiment;

import magic.data.DataProvider;
import magic.dto.CodeViolation;
import magic.dto.ImprovedCode;
import magic.tool.JavaCodeChecker;
import magic.tool.PythonCodeChecker;
import magic.util.TestUtils;

import java.util.List;

public class Evaluation {

    private static final JavaCodeChecker javaCodeChecker = new JavaCodeChecker();
    private static final PythonCodeChecker pythonCodeChecker = new PythonCodeChecker();

    public static void main(String[] args) throws Exception {
        String magic_file1 = "magic/deepseek-output-java.jsonl";
        String magic_file2 = "magic/gpt-5-output-java.jsonl";
        String magic_file3 = "magic/deepseek-output-python.jsonl";
        String magic_file4 = "magic/gpt-5-output-python.jsonl";
        System.out.println("magic");
        run(magic_file1, magic_file2, magic_file3, magic_file4);
        System.out.println();

        /* String baseline1_file1 = "baseline1/deepseek-baseline1-java.jsonl";
        String baseline1_file2 = "baseline1/gpt-5-baseline1-java.jsonl";
        String baseline1_file3 = "baseline1/deepseek-baseline1-python.jsonl";
        String baseline1_file4 = "baseline1/gpt-5-baseline1-python.jsonl";
        System.out.println("baseline1");
        run(baseline1_file1, baseline1_file2, baseline1_file3, baseline1_file4);
        System.out.println();

        String baseline1_gpt_file1 = "baseline1/deepseek-baseline1-gpt-java.jsonl";
        String baseline1_gpt_file2 = "baseline1/gpt-5-baseline1-gpt-java.jsonl";
        String baseline1_gpt_file3 = "baseline1/deepseek-baseline1-gpt-python.jsonl";
        String baseline1_gpt_file4 = "baseline1/gpt-5-baseline1-gpt-python.jsonl";
        System.out.println("baseline1-gpt");
        run(baseline1_gpt_file1, baseline1_gpt_file2, baseline1_gpt_file3, baseline1_gpt_file4);
        System.out.println();

        String baseline2_file1 = "baseline2/deepseek-baseline2-java.jsonl";
        String baseline2_file2 = "baseline2/gpt-5-baseline2-java.jsonl";
        String baseline2_file3 = "baseline2/deepseek-baseline2-python.jsonl";
        String baseline2_file4 = "baseline2/gpt-5-baseline2-python.jsonl";
        System.out.println("baseline2");
        run(baseline2_file1, baseline2_file2, baseline2_file3, baseline2_file4);
        System.out.println();

        String baseline2_gpt_file1 = "baseline2/deepseek-baseline2-gpt-java.jsonl";
        String baseline2_gpt_file2 = "baseline2/gpt-5-baseline2-gpt-java.jsonl";
        String baseline2_gpt_file3 = "baseline2/deepseek-baseline2-gpt-python.jsonl";
        String baseline2_gpt_file4 = "baseline2/gpt-5-baseline2-gpt-python.jsonl";
        System.out.println("baseline2-gpt");
        run(baseline2_gpt_file1, baseline2_gpt_file2, baseline2_gpt_file3, baseline2_gpt_file4);
        System.out.println();

        String baseline3_gpt_file1 = "baseline3/deepseek-baseline3-java.jsonl";
        String baseline3_gpt_file2 = "baseline3/gpt-5-baseline3-java.jsonl";
        String baseline3_gpt_file3 = "baseline3/deepseek-baseline3-python.jsonl";
        String baseline3_gpt_file4 = "baseline3/gpt-5-baseline3-python.jsonl";
        System.out.println("baseline3");
        run(baseline3_gpt_file1, baseline3_gpt_file2, baseline3_gpt_file3, baseline3_gpt_file4);
        System.out.println();

        String magic_without_dsl_file1 = "magic/deepseek-without-dsl-java.jsonl";
        String magic_without_dsl_file2 = "magic/gpt-5-without-dsl-java.jsonl";
        String magic_without_dsl_file3 = "magic/deepseek-without-dsl-python.jsonl";
        String magic_without_dsl_file4 = "magic/gpt-5-without-dsl-python.jsonl";
        System.out.println("magic-without-dsl");
        run(magic_without_dsl_file1, magic_without_dsl_file2, magic_without_dsl_file3, magic_without_dsl_file4);
        System.out.println();

        String magic_without_validation_file1 = "magic/deepseek-without-validation-java.jsonl";
        String magic_without_validation_file2 = "magic/gpt-5-without-validation-java.jsonl";
        String magic_without_validation_file3 = "magic/deepseek-without-validation-python.jsonl";
        String magic_without_validation_file4 = "magic/gpt-5-without-validation-python.jsonl";
        System.out.println("magic-without-validation");
        run(magic_without_validation_file1, magic_without_validation_file2, magic_without_validation_file3, magic_without_validation_file4);
        System.out.println(); */
    }
    public static void run(String fileName1, String fileName2, String fileName3, String fileName4) throws Exception {
        List<ImprovedCode> codeList1 = DataProvider.readImprovedCode(fileName1);
        List<ImprovedCode> codeList2 = DataProvider.readImprovedCode(fileName2);
        List<ImprovedCode> codeList3 = DataProvider.readImprovedCode(fileName3);
        List<ImprovedCode> codeList4 = DataProvider.readImprovedCode(fileName4);
        int oldIssues = 0;
        int newIssues = 0;
        int unresolvedIssues = 0;
        int bugs = 0;

        for (ImprovedCode code : codeList1) {
            String taskId = code.getTaskId();
            System.out.println("Processing task: " + taskId);
            String generatedCode = code.getGeneratedCode();
            String improvedCode = code.getImprovedCode();
            String test = code.getTest();
            boolean b = TestUtils.executeJavaCode(improvedCode, test);
            if (!b) {
                bugs++;
            }
            List<CodeViolation> codeViolations1 = javaCodeChecker.checkIssues(generatedCode);
            List<CodeViolation> codeViolations2 = javaCodeChecker.checkIssues(improvedCode);
            oldIssues += codeViolations1.size();
            unresolvedIssues += codeViolations2.size();
            for (CodeViolation violation : codeViolations2) {
                if (!codeViolations1.contains(violation)) {
                    newIssues++;
                }
            }
        }

         for (ImprovedCode code : codeList2) {
            String taskId = code.getTaskId();
            System.out.println("Processing task: " + taskId);
            String generatedCode = code.getGeneratedCode();
            String improvedCode = code.getImprovedCode();
            String test = code.getTest();
            boolean b = TestUtils.executeJavaCode(improvedCode, test);
            if (!b) {
                bugs ++;
            }
            List<CodeViolation> codeViolations1 = javaCodeChecker.checkIssues(generatedCode);
            List<CodeViolation> codeViolations2 = javaCodeChecker.checkIssues(improvedCode);
            oldIssues += codeViolations1.size();
            unresolvedIssues += codeViolations2.size();
            for (CodeViolation violation : codeViolations2) {
                if (!codeViolations1.contains(violation)) {
                    newIssues++;
                }
            }
        }

        for (ImprovedCode code : codeList3) {
            String taskId = code.getTaskId();
            System.out.println("Processing task: " + taskId);
            String generatedCode = code.getGeneratedCode();
            String improvedCode = code.getImprovedCode();
            String test = code.getTest();
            boolean b = TestUtils.executePythonCode(improvedCode, test);
            if (!b) {
                bugs++;
            }
            List<CodeViolation> codeViolations1 = pythonCodeChecker.checkIssues(generatedCode);
            List<CodeViolation> codeViolations2 = pythonCodeChecker.checkIssues(improvedCode);
            oldIssues += codeViolations1.size();
            unresolvedIssues += codeViolations2.size();
            for (CodeViolation violation : codeViolations2) {
                if (!codeViolations1.contains(violation)) {
                    newIssues++;
                }
            }
        }

        for (ImprovedCode code : codeList4) {
            String taskId = code.getTaskId();
            System.out.println("Processing task: " + taskId);
            String generatedCode = code.getGeneratedCode();
            String improvedCode = code.getImprovedCode();
            String test = code.getTest();
            boolean b = TestUtils.executePythonCode(improvedCode, test);
            if (!b) {
                bugs++;
            }
            List<CodeViolation> codeViolations1 = pythonCodeChecker.checkIssues(generatedCode);
            List<CodeViolation> codeViolations2 = pythonCodeChecker.checkIssues(improvedCode);
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
