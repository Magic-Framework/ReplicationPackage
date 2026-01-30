package magic.dto;

public class ImprovedCode {

    private String taskId;
    private String test;
    private String generatedCode;
    private String improvedCode;
    private String iteration;
    private String nanoTime;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getGeneratedCode() {
        return generatedCode;
    }

    public void setGeneratedCode(String generatedCode) {
        this.generatedCode = generatedCode;
    }

    public String getImprovedCode() {
        return improvedCode;
    }

    public void setImprovedCode(String improvedCode) {
        this.improvedCode = improvedCode;
    }

    public String getIteration() {
        return iteration;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getNanoTime() {
        return nanoTime;
    }

    public void setNanoTime(String nanoTime) {
        this.nanoTime = nanoTime;
    }
}
