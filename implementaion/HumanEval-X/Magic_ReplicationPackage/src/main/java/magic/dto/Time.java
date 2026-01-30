package magic.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

@HeadRowHeight(25)
public class Time {

    @HeadFontStyle(fontHeightInPoints = 12)
    @ExcelProperty("iteration")
    private int iteration;

    @ExcelProperty("nanoTime")
    private long nanoTime;

    @ExcelProperty("inputTokens")
    private long inputTokens;

    @ExcelProperty("outputTokens")
    private long outputTokens;

    @ExcelProperty("totalTokens")
    private long totalTokens;

    public Time(int iteration, long nanoTime, long inputTokens, long outputTokens, long totalTokens) {
        this.iteration = iteration;
        this.nanoTime = nanoTime;
        this.inputTokens = inputTokens;
        this.outputTokens = outputTokens;
        this.totalTokens = totalTokens;
    }

    public int getIteration() {
        return iteration;
    }

    public long getNanoTime() {
        return nanoTime;
    }

    public long getInputTokens() {
        return inputTokens;
    }

    public long getOutputTokens() {
        return outputTokens;
    }

    public long getTotalTokens() {
        return totalTokens;
    }
}
