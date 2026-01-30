package magic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskEntry {

    @JsonProperty("task_id")
    private String taskId;
    private String prompt;
    @JsonProperty("canonical_solution")
    private String canonicalSolution;
    private String test;
    private String text;
    private String declaration;
    @JsonProperty("example_test")
    private String exampleTest;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getCanonicalSolution() {
        return canonicalSolution;
    }

    public void setCanonicalSolution(String canonicalSolution) {
        this.canonicalSolution = canonicalSolution;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDeclaration() {
        return declaration;
    }

    public void setDeclaration(String declaration) {
        this.declaration = declaration;
    }

    public String getExampleTest() {
        return exampleTest;
    }

    public void setExampleTest(String exampleTest) {
        this.exampleTest = exampleTest;
    }
}
