package magic.kb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import magic.core.PMDRulesParser;
import magic.dto.CodeViolation;
import magic.dto.PMDRule;
import magic.dto.RefactoringRule;
import magic.service.KBAgent;
import magic.tool.PMDChecker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PMDKBConstructor {

    private static final String KNOWLEDGE_BASE_JSON = "knowledgebase/dsl-rules-java.json";
    private static final PMDRulesParser parser = new PMDRulesParser();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final PMDChecker checker = new PMDChecker();
    private static final String language = "java";
    private static final KBAgent kbAgent = new KBAgent();

    public static void main(String[] args) throws Exception {
        PMDKBConstructor construction = new PMDKBConstructor();
        List<String> ruleNames = new ArrayList<>();
        ruleNames.add("PrematureDeclaration");
        ruleNames.add("ConfusingTernary");
        ruleNames.add("AppendCharacterWithChar");
        ruleNames.add("UseLocaleWithCaseConversions");
        ruleNames.add("LiteralsFirstInComparisons");
        ruleNames.add("UseConcurrentHashMap");
        ruleNames.add("AvoidReassigningParameters");
        ruleNames.add("UselessParentheses");
        ruleNames.add("UnnecessaryLocalBeforeReturn");
        ruleNames.add("UnnecessaryCast");
        ruleNames.add("ForLoopCanBeForeach");
        ruleNames.add("UseDiamondOperator");
        ruleNames.add("LooseCoupling");
        ruleNames.add("UnnecessaryCaseChange");
        ruleNames.add("SwitchStmtsShouldHaveDefault");
        ruleNames.add("SimplifyBooleanReturns");
        ruleNames.add("UnusedLocalVariable");
        ruleNames.add("CollapsibleIfStatements");
        ruleNames.add("UnnecessaryBoxing");
        ruleNames.add("UseStringBufferForStringAppends");
        ruleNames.add("InefficientEmptyStringCheck");
        ruleNames.add("UnusedAssignment");
        ruleNames.add("ReturnEmptyCollectionRatherThanNull");
        ruleNames.add("LogicInversion");
        ruleNames.add("EmptyControlStatement");
        ruleNames.add("SimplifiedTernary");
        ruleNames.add("GuardLogStatement");
        ruleNames.add("AvoidThrowingNullPointerException");
        ruleNames.add("AvoidCatchingGenericException");
        ruleNames.add("NullAssignment");
        ruleNames.add("AvoidArrayLoops");
        ruleNames.add("CompareObjectsWithEquals");
        ruleNames.add("ConsecutiveAppendsShouldReuse");
        ruleNames.add("EmptyCatchBlock");
        ruleNames.add("UseProperClassLoader");
        ruleNames.add("PreserveStackTrace");
        ruleNames.add("AvoidDuplicateLiterals");
        ruleNames.add("UnnecessaryFullyQualifiedName");
        ruleNames.add("AvoidThrowingRawExceptionTypes");
        ruleNames.add("OptimizableToArrayCall");
        ruleNames.add("UseCollectionIsEmpty");
        ruleNames.add("ConsecutiveLiteralAppends");
        ruleNames.add("AvoidPrintStackTrace");
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
        PMDRule pmdRule = parser.extractRule(ruleName);
        String description = pmdRule.getDescription();
        String antiPattern = pmdRule.getCodeBefore();
        String bestPractice = pmdRule.getCodeAfter();
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

    private static List<CodeViolation> isResolved(String name, String fixedCode) throws Exception {
        List<CodeViolation> newIssues = checker.checkIssues(fixedCode);
        return newIssues.stream().filter(issue -> issue.getRuleName().equals(name)).collect(Collectors.toList());
    }
}
