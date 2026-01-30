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
}
