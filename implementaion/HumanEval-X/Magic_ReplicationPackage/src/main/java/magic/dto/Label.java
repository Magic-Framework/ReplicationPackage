package magic.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadFontStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;

@HeadRowHeight(25)
public class Label {

    @HeadFontStyle(fontHeightInPoints = 12)
    @ExcelProperty("equals")
    private int equals;

    @ExcelProperty("buggy")
    private int buggy;

    @ExcelProperty("developer1")
    private int developer1;

    @ExcelProperty("developer2")
    private int developer2;

    @ExcelProperty("developer3")
    private int developer3;

    public Label(int equals, int buggy, int developer1, int developer2, int developer3) {
        this.equals = equals;
        this.buggy = buggy;
        this.developer1 = developer1;
        this.developer2 = developer2;
        this.developer3 = developer3;
    }

    public int getEquals() {
        return equals;
    }

    public int getBuggy() {
        return buggy;
    }

    public int getDeveloper1() {
        return developer1;
    }

    public int getDeveloper2() {
        return developer2;
    }

    public int getDeveloper3() {
        return developer3;
    }
}
