package magic.kb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import magic.core.PylintRulesParser;
import magic.dto.CodeViolation;
import magic.dto.PylintRule;
import magic.dto.RefactoringRule;
import magic.service.KBAgent;
import magic.tool.PylintChecker;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PylintKBConstructor {

    private static final String KNOWLEDGE_BASE_JSON = "knowledgebase/dsl-rules-python.json";
    private static final PylintRulesParser parser = new PylintRulesParser();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final PylintChecker checker = new PylintChecker();
    private static final String language = "python";
    private static final KBAgent kbAgent = new KBAgent();

    public static void main(String[] args) throws Exception {
        PylintKBConstructor construction = new PylintKBConstructor();
        List<String> ruleIDs = new ArrayList<>();
        /*ruleIDs.add("C0200");
        ruleIDs.add("R1731");
        ruleIDs.add("R1705");
        ruleIDs.add("W0612");
        ruleIDs.add("R1713");
        ruleIDs.add("R1728");
        ruleIDs.add("R1714");
        ruleIDs.add("R1716");
        ruleIDs.add("R0912");
        ruleIDs.add("W0622");
        ruleIDs.add("R1730");
        ruleIDs.add("W0105");
        ruleIDs.add("R0911");
        ruleIDs.add("R0914");
        ruleIDs.add("C0325");
        ruleIDs.add("R1703");
        ruleIDs.add("C0123");
        ruleIDs.add("W0123");
        ruleIDs.add("W0212");
        ruleIDs.add("W1203");
        ruleIDs.add("W0718");
        ruleIDs.add("R0915");
        ruleIDs.add("W0404");
        ruleIDs.add("E0102");
        ruleIDs.add("W1510");
        ruleIDs.add("W1514");
        ruleIDs.add("C0209");
        ruleIDs.add("R1720");
        ruleIDs.add("E1101");
        ruleIDs.add("R1724");
        ruleIDs.add("C2801");*/
        ruleIDs.add("E0401");
        ruleIDs.add("E1120");
        ruleIDs.add("W0104");
        ruleIDs.add("E1102");
        ruleIDs.add("W0707");
        ruleIDs.add("E1121");
        ruleIDs.add("C0201");
        for (String ruleID : ruleIDs) {
            RefactoringRule rule = construction.constructKB(ruleID);
//            System.out.println(rule);
        }
    }

    public RefactoringRule constructKB(String ruleID) throws IOException {
        File targetFile = new File("magic_replicationpackage/src/main/resources/" + KNOWLEDGE_BASE_JSON);
        List<RefactoringRule> list = objectMapper.readValue(targetFile, new TypeReference<>() {
        });
        for (RefactoringRule rule : list)
            if (rule.getId().equals(ruleID)) return rule;
        PylintRule pylintRule = parser.extractRule(ruleID);
        String ruleName = pylintRule.getName();
        String description = pylintRule.getDescription();
        Map<String, String> problematicCode = pylintRule.getProblematicCode();
        String antiPattern = problematicCode.get("bad.py");
        if (antiPattern == null) antiPattern = problematicCode.get("example_1.py");
        if (antiPattern == null) antiPattern = problematicCode.get("empty_dict.py");
        Map<String, String> correctCode = pylintRule.getCorrectCode();
        String bestPractice = correctCode.get("good.py");
        if (bestPractice == null) bestPractice = correctCode.get("example_1.py");
        if (bestPractice == null) bestPractice = correctCode.get("empty_dict.py");
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
