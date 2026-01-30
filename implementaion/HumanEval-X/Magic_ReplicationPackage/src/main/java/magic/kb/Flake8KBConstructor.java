package magic.kb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import magic.core.Flake8RulesParser;
import magic.dto.CodeViolation;
import magic.dto.Flake8Rule;
import magic.dto.RefactoringRule;
import magic.service.KBAgent;
import magic.tool.Flake8Checker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Flake8KBConstructor {

    private static final String KNOWLEDGE_BASE_JSON = "knowledgebase/dsl-rules-python.json";
    private static final Flake8RulesParser parser = new Flake8RulesParser();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Flake8Checker checker = new Flake8Checker();
    private static final String language = "python";
    private static final KBAgent kbAgent = new KBAgent();

    public static void main(String[] args) throws Exception {
        Flake8KBConstructor construction = new Flake8KBConstructor();
        List<String> ruleIDs = new ArrayList<>();
        ruleIDs.add("E741");
        ruleIDs.add("F841");
        ruleIDs.add("E128");
        ruleIDs.add("E306");
        for (String ruleID : ruleIDs) {
            RefactoringRule rule = construction.constructKB(ruleID);
//            System.out.println(rule);
        }
    }

    public RefactoringRule constructKB(String ruleID) throws IOException {
        File targetFile = new File("src/main/resources/" + KNOWLEDGE_BASE_JSON);
        List<RefactoringRule> list = objectMapper.readValue(targetFile, new TypeReference<>() {
        });
        for (RefactoringRule rule : list)
            if (rule.getId().equals(ruleID)) return rule;
        Flake8Rule flake8Rule = parser.extractRule(ruleID);
        String ruleName = flake8Rule.getName();
        String description = flake8Rule.getDescription();
        String antiPattern = flake8Rule.getAntiPattern();
        String bestPractice = flake8Rule.getBestPractice();
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
        RefactoringRule newRule = new RefactoringRule(ruleID, ruleName, description, refactoringType, antiPattern, correctFix,
                isCorrect, isResolved);
        list.add(newRule);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetFile, list);
        return newRule;
    }

    private static List<CodeViolation> isResolved(String id, String fixedCode) {
        List<CodeViolation> newIssues = checker.checkIssues(fixedCode);
        return newIssues.stream().filter(issue -> issue.getRuleName().equals(id)).collect(Collectors.toList());
    }
}
