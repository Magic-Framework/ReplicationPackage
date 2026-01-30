package magic.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RefactoringRule {

    @JsonProperty("ID")
    private String id;
    @JsonProperty("RuleName")
    private String ruleName;
    @JsonProperty("Description")
    private String description;
    @JsonProperty("RefactoringType")
    private String refactoringType;
    @JsonProperty("CodeBefore")
    private String codeBefore;
    @JsonProperty("CodeAfter")
    private String codeAfter;
    @JsonProperty("IsCorrect")
    private boolean correct;
    @JsonProperty("IsResolved")
    private boolean resolved;

    public RefactoringRule(String id, String ruleName, String description, String refactoringType, String codeBefore,
                           String codeAfter, boolean correct, boolean resolved) {
        this.id = id;
        this.ruleName = ruleName;
        this.description = description;
        this.refactoringType = refactoringType;
        this.codeBefore = codeBefore;
        this.codeAfter = codeAfter;
        this.correct = correct;
        this.resolved = resolved;
    }

    public RefactoringRule() {
    }

    public String getId() {
        return id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public String getDescription() {
        return description;
    }

    public String getRefactoringType() {
        return refactoringType;
    }

    public String getCodeBefore() {
        return codeBefore;
    }

    public String getCodeAfter() {
        return codeAfter;
    }

    public boolean isCorrect() {
        return correct;
    }

    public boolean isResolved() {
        return resolved;
    }

    @Override
    public String toString() {
        return "Rule{" +
                "id='" + id + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", description='" + description + '\'' +
                ", refactoringType='" + refactoringType + '\'' +
                ", codeBefore='" + codeBefore + '\'' +
                ", codeAfter='" + codeAfter + '\'' +
                ", isCorrect='" + correct + '\'' +
                ", isResolved='" + resolved + '\'' +
                '}';
    }
}
