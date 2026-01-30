package magic.tool;

import magic.dto.CodeViolation;

import java.util.ArrayList;
import java.util.List;

public class JavaCodeChecker {

    private static final PMDChecker pmdChecker = new PMDChecker();
    private static final CheckstyleChecker checkstyleChecker = new CheckstyleChecker();

    public List<CodeViolation> checkIssues(String javaCode) throws Exception {
        List<CodeViolation> allViolations = new ArrayList<>();
        allViolations.addAll(pmdChecker.checkIssues(javaCode));
        allViolations.addAll(checkstyleChecker.checkIssues(javaCode));
        return allViolations;
    }

    public List<CodeViolation> checkIssues(String fileContent, String javaCode, int startLine, int endLine)
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
        String newCode = fileContent.replace(tempsss.toString(), javaCode.indent(4));
        allViolations.addAll(pmdChecker.checkIssues(newCode));
        allViolations.addAll(checkstyleChecker.checkIssues(newCode));
        List<CodeViolation> filteredViolations = new ArrayList<>();
        for (CodeViolation violation : allViolations) {
            if (violation.getStartLine() > startLine + 3 && violation.getEndLine() < startLine + javaCode.split("\n").length - 1) {
                filteredViolations.add(violation);
            }
        }
        return filteredViolations;
    }
}
