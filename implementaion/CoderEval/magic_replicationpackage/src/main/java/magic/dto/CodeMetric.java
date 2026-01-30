package magic.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

@HeadRowHeight(25)
public class CodeMetric {

    @HeadFontStyle(fontHeightInPoints = 12)
    @ExcelProperty("modelName")
    private String modelName;

    @ExcelProperty("language")
    private String language;

    @ExcelProperty("codeIssues")
    private int codeIssues;

    @ExcelProperty("loc")
    private int loc;

    @ExcelProperty("cyclomaticComplexity")
    private int cyclomaticComplexity;

    @ExcelProperty("halsteadVolume")
    private double halsteadVolume;

    @ExcelProperty("maintainabilityIndex")
    private double maintainabilityIndex;

    public CodeMetric(String modelName, String language, int codeIssues, int loc, int cyclomaticComplexity, double halsteadVolume, double maintainabilityIndex) {
        this.modelName = modelName;
        this.language = language;
        this.codeIssues = codeIssues;
        this.loc = loc;
        this.cyclomaticComplexity = cyclomaticComplexity;
        this.halsteadVolume = halsteadVolume;
        this.maintainabilityIndex = maintainabilityIndex;
    }

    public String getModelName() {
        return modelName;
    }

    public String getLanguage() {
        return language;
    }

    public int getCodeIssues() {
        return codeIssues;
    }

    public int getLoc() {
        return loc;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public double getHalsteadVolume() {
        return halsteadVolume;
    }

    public double getMaintainabilityIndex() {
        return maintainabilityIndex;
    }
}
