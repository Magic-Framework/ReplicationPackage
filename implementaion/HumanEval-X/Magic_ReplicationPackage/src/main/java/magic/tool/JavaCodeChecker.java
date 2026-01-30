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
}
