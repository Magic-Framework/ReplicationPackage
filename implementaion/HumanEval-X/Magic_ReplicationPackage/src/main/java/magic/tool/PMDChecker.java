package magic.tool;

import magic.dto.CodeViolation;
import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.PmdAnalysis;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.lang.rule.RulePriority;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

import java.util.*;

public class PMDChecker {

    public List<CodeViolation> checkIssues(String javaCode) {
        List<CodeViolation> issues = new ArrayList<>();

        // 配置 PMD
        PMDConfiguration config = new PMDConfiguration();
        config.addRuleSet("rulesets-pmd/java/all.xml");
        config.setMinimumPriority(RulePriority.LOW); // 设置最低优先级

        String[] remove = {
                "UnnecessaryImport", "CommentRequired", "NoPackage", "AtLeastOneConstructor", "LocalVariableCouldBeFinal",
                "UseVarargs", "MethodArgumentCouldBeFinal", "ShortVariable", "OnlyOneReturn", "LawOfDemeter",
                "ShortMethodName", "MethodNamingConventions", "ControlStatementBraces", "CommentDefaultAccessModifier",
                "CommentSize", "AvoidLiteralsInIfCondition", "LinguisticNaming", "ImmutableField", "CognitiveComplexity",
                "CyclomaticComplexity", "FormalParameterNamingConventions", "UseUnderscoresInNumericLiterals",
                "AvoidReassigningLoopVariables", "ShortClassName", "AvoidInstantiatingObjectsInLoops", "OneDeclarationPerLine",
                "SystemPrintln", "LongVariable", "LocalVariableNamingConventions", "NcssCount"};

        List<String> removeList = Arrays.asList(remove);
        
        try (PmdAnalysis pmd = PmdAnalysis.create(config)) {
            pmd.files().addSourceFile(FileId.fromPathLikeString("/Solution.java"), javaCode);
            Report report = pmd.performAnalysisAndCollectReport();
            List<RuleViolation> violations = report.getViolations();
            for (RuleViolation violation : violations) {
                int violationBeginLine = violation.getBeginLine();
                int violationEndLine = violation.getEndLine();
                String description = violation.getDescription();
                Rule rule = violation.getRule();
                String ruleName = rule.getName();
                if (removeList.contains(ruleName)) continue;
                int priority = rule.getPriority().getPriority();
                issues.add(new CodeViolation("pmd", violationBeginLine, violationEndLine, description, ruleName, priority));
            }
        }
        return issues;
    }
}
