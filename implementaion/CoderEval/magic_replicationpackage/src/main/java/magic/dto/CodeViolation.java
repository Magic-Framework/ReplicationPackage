package magic.dto;

import java.util.Objects;

public class CodeViolation implements Comparable<CodeViolation> {

    private final String toolName;
    private final int startLine;     // 开始行号
    private final int endLine;     // 结束行号
    private final String description; // 问题描述
    private final String ruleName;    // 违反的规则名称
    private int severity; // 严重性

    public CodeViolation(String toolName, int startLine, int endLine, String description, String ruleName, int severity) {
        this.toolName = toolName;
        this.startLine = startLine;
        this.endLine = endLine;
        this.description = description;
        this.ruleName = ruleName;
        this.severity = severity;
    }

    public String getToolName() {
        return toolName;
    }

    public int getStartLine() {
        return startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public String getDescription() {
        return description;
    }

    public String getRuleName() {
        return ruleName;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CodeViolation that = (CodeViolation) o;
        return Objects.equals(ruleName, that.ruleName) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ruleName, description);
    }

    @Override
    public int compareTo(CodeViolation other) {
        return Integer.compare(this.severity, other.severity);
    }

    @Override
    public String toString() {
        return "CodeViolation{" +
                "ruleName='" + ruleName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
