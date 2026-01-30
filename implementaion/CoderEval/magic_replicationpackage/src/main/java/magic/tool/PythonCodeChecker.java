package magic.tool;

import magic.dto.CodeViolation;

import java.util.ArrayList;
import java.util.List;

public class PythonCodeChecker {

    private static final PylintChecker pylintChecker = new PylintChecker();
    private static final Flake8Checker flake8Checker = new Flake8Checker();

    public List<CodeViolation> checkIssues(String pythonCode) {
        List<CodeViolation> allViolations = new ArrayList<>();
        allViolations.addAll(pylintChecker.checkIssues(pythonCode));
        allViolations.addAll(flake8Checker.checkIssues(pythonCode));
        return allViolations;
    }

    public List<CodeViolation> checkIssues(String fileContent, String pythonCode, int startLine, int endLine)
            throws Exception {
        List<CodeViolation> allViolations = new ArrayList<>();
        String[] codeList = fileContent.split("\n");
        StringBuilder tempsss = new StringBuilder();
        for (int i = startLine - 1; i < endLine; i++) {
            tempsss.append(codeList[i]).append("\n");
        }
        if (tempsss.length() > 0) {
            tempsss.deleteCharAt(tempsss.length() - 1);
        }
        String newCode = fileContent.replace(tempsss.toString(), pythonCode.indent(4));
        allViolations.addAll(pylintChecker.checkIssues(newCode));
        allViolations.addAll(flake8Checker.checkIssues(newCode));
        List<CodeViolation> filteredViolations = new ArrayList<>();
        for (CodeViolation violation : allViolations) {
            if (violation.getStartLine() > startLine && violation.getEndLine() < startLine + pythonCode.split("\n").length - 1) {
                filteredViolations.add(violation);
            }
        }
        return filteredViolations;
    }
}
