package magic.dto;

import dev.langchain4j.model.output.structured.Description;

public class CodeChange {

    @Description("Reasoning about whether the external behavior changes between non-compliant code and compliant code.")
    private String reasoning;

    @Description("Set to false ONLY IF the compliant code does change the external behavior; Otherwise, set to true.")
    private boolean isCorrect;

    @Description("The correct fix for the non-compliant code. If isCorrect is true, return the complete compliant code.")
    private String correctFix;

    @Description("Type of refactoring applied to resolve the rule violation.")
    private String refactoringType;

    public String getReasoning() {
        return reasoning;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public String getCorrectFix() {
        return correctFix;
    }

    public String getRefactoringType() {
        return refactoringType;
    }
}
