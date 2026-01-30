package magic.dto;

import dev.langchain4j.model.output.structured.Description;

public class Score {

    @Description("The quality score of the refined code, range from 1 to 4.")
    private int score;

    public int getScore() {
        return score;
    }
}
