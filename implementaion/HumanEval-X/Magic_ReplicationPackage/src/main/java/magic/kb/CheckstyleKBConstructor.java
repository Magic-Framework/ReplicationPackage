package magic.kb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import magic.core.CheckstyleRulesParser;
import magic.dto.CheckstyleRule;
import magic.dto.CodeViolation;
import magic.dto.RefactoringRule;
import magic.service.KBAgent;
import magic.tool.CheckstyleChecker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CheckstyleKBConstructor {

    private static final String KNOWLEDGE_BASE_JSON = "knowledgebase/dsl-rules-java.json";
    private static final CheckstyleRulesParser parser = new CheckstyleRulesParser();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final CheckstyleChecker checker = new CheckstyleChecker();
    private static final String language = "java";
    private static final KBAgent kbAgent = new KBAgent();

    public static void main(String[] args) throws Exception {
        CheckstyleKBConstructor construction = new CheckstyleKBConstructor();
        List<String> ruleNames = new ArrayList<>();
        ruleNames.add("MultipleVariableDeclarations");
        ruleNames.add("MissingSwitchDefault");
        ruleNames.add("VisibilityModifier");
        ruleNames.add("HiddenField");
        ruleNames.add("EmptyBlock");
        ruleNames.add("MissingOverride");
        ruleNames.add("RightCurly");
        for (String ruleName : ruleNames) {
            RefactoringRule rule = construction.constructKB(ruleName);
//            System.out.println(rule);
        }
    }

    public RefactoringRule constructKB(String ruleName) throws Exception {
        File targetFile = new File("src/main/resources/" + KNOWLEDGE_BASE_JSON);
        List<RefactoringRule> list = objectMapper.readValue(targetFile, new TypeReference<>() {
        });
        for (RefactoringRule rule : list)
            if (rule.getRuleName().equals(ruleName)) return rule;
        CheckstyleRule checkstyleRule = parser.extractRule(ruleName);
        String description = checkstyleRule.getDescription();
        String antiPattern = checkstyleRule.getCodeBefore();
        String bestPractice = checkstyleRule.getCodeAfter();
        Map<String, Object> response = kbAgent.fixIssues(language, ruleName, description, antiPattern, bestPractice);
        String sessionId = response.get("sessionId").toString();
        boolean isCorrect = Boolean.parseBoolean(response.get("isCorrect").toString());
        String correctFix = isCorrect ? bestPractice : response.get("correctFix").toString();
        String refactoringType = response.get("refactoringType").toString();
        boolean isResolved = false;
        for (int i = 0; i < 4; i++) {
            List<CodeViolation> resolved = isResolved(ruleName, correctFix);
            isResolved = resolved.isEmpty();
            if (isResolved) break;

            response = kbAgent.refineSolution(sessionId, language, ruleName, description, antiPattern, correctFix, resolved);
            correctFix = response.get("correctFix").toString();
            refactoringType = response.get("refactoringType").toString();
        }
        kbAgent.clearMemory(sessionId);
        RefactoringRule newRule = new RefactoringRule(null, ruleName, description, refactoringType, antiPattern, correctFix,
                isCorrect, isResolved);
        list.add(newRule);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, list);
        return newRule;
    }

    private List<CodeViolation> isResolved(String name, String correctFix) throws Exception {
        List<CodeViolation> newIssues = checker.checkIssues(correctFix);
        return newIssues.stream().filter(issue -> issue.getRuleName().equals(name)).collect(Collectors.toList());
    }
}
