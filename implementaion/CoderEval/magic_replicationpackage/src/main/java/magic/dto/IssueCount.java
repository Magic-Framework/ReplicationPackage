package magic.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

@HeadRowHeight(25)
public class IssueCount {

    @HeadFontStyle(fontHeightInPoints = 12)
    @ExcelProperty("modelName")
    private String modelName;

    @ExcelProperty("language")
    private String language;

    @ExcelProperty("taskID")
    private String taskId;

    @ExcelProperty("oldIssues")
    private int oldIssues;

    @ExcelProperty("unresolvedIssues")
    private int unresolvedIssues;

    @ExcelProperty("newIssues")
    private int newIssues;

    @ExcelProperty("bugs")
    private int bugs;

    public IssueCount(String modelName, String language, String taskId, int oldIssues, int unresolvedIssues, int newIssues, int bugs) {
        this.modelName = modelName;
        this.language = language;
        this.taskId = taskId;
        this.oldIssues = oldIssues;
        this.unresolvedIssues = unresolvedIssues;
        this.newIssues = newIssues;
        this.bugs = bugs;
    }

    public String getModelName() {
        return modelName;
    }

    public String getLanguage() {
        return language;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getOldIssues() {
        return oldIssues;
    }

    public int getUnresolvedIssues() {
        return unresolvedIssues;
    }

    public int getNewIssues() {
        return newIssues;
    }

    public int getBugs() {
        return bugs;
    }
}
