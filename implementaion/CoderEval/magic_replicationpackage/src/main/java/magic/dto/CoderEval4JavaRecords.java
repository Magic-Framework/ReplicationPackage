package magic.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoderEval4JavaRecords {

    @JsonProperty("RECORDS")
    private List<CoderEval4Java> records;

    public List<CoderEval4Java> getRecords() {
        return records;
    }
    
}
