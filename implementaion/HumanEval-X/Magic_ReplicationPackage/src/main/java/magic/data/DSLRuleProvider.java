package magic.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import magic.dto.CodeViolation;
import magic.dto.RefactoringRule;
import magic.kb.CheckstyleKBConstructor;
import magic.kb.Flake8KBConstructor;
import magic.kb.PMDKBConstructor;
import magic.kb.PylintKBConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DSLRuleProvider {

    public static Map<String, RefactoringRule> readJavaRefactoringRules(List<CodeViolation> codeViolations)
            throws Exception {
        Map<String, RefactoringRule> ruleMap = new LinkedHashMap<>();
        List<RefactoringRule> javaRules = readRefactoringRules("knowledgebase/dsl-rules-java.json");
        for (CodeViolation violation : codeViolations) {
            String toolName = violation.getToolName();
            String ruleName = violation.getRuleName();
            boolean isFound = false;
            for (RefactoringRule rule : javaRules) {
                if (rule.getRuleName().equals(ruleName)) {
                    ruleMap.put(ruleName, rule);
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                if ("checkstyle".equals(toolName)) {
                    CheckstyleKBConstructor checkstyleKBConstructor = new CheckstyleKBConstructor();
                    RefactoringRule refactoringRule = checkstyleKBConstructor.constructKB(ruleName);
                    ruleMap.put(ruleName, refactoringRule);
                    break;
                }
                if ("pmd".equals(toolName)) {
                    PMDKBConstructor pmdKBConstructor = new PMDKBConstructor();
                    RefactoringRule refactoringRule = pmdKBConstructor.constructKB(ruleName);
                    ruleMap.put(ruleName, refactoringRule);
                    break;
                }
            }
        }
        return ruleMap;
    }

    public static Map<String, RefactoringRule> readPythonRefactoringRules(List<CodeViolation> codeViolations)
            throws IOException {
        Map<String, RefactoringRule> ruleMap = new LinkedHashMap<>();
        List<RefactoringRule> pythonRules = readRefactoringRules("knowledgebase/dsl-rules-python.json");
        for (CodeViolation violation : codeViolations) {
            String toolName = violation.getToolName();
            String ruleName = violation.getRuleName();
            boolean isFound = false;
            for (RefactoringRule rule : pythonRules) {
                if (rule.getId().equals(ruleName)) {
                    ruleMap.put(ruleName, rule);
                    isFound = true;
                    break;
                }
            }
            if (!isFound) {
                if ("pylint".equals(toolName)) {
                    PylintKBConstructor pylintKBConstructor = new PylintKBConstructor();
                    RefactoringRule refactoringRule = pylintKBConstructor.constructKB(ruleName);
                    ruleMap.put(ruleName, refactoringRule);
                    break;
                }
                if ("flake8".equals(toolName)) {
                    Flake8KBConstructor flake8KBConstructor = new Flake8KBConstructor();
                    RefactoringRule refactoringRule = flake8KBConstructor.constructKB(ruleName);
                    ruleMap.put(ruleName, refactoringRule);
                    break;
                }
            }
        }
        return ruleMap;
    }

    private static List<RefactoringRule> readRefactoringRules(String fileName) throws IOException {
        TypeReference<List<RefactoringRule>> valueTypeRef = new TypeReference<>() {
        };
        ClassLoader classLoader = DSLRuleProvider.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileName)) {
            if (inputStream == null) {
                throw new IOException(fileName + " Not Found!");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(inputStream, valueTypeRef);
        }
    }
}
