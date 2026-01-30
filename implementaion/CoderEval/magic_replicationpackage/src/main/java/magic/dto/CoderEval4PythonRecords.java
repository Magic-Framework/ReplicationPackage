package magic.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoderEval4PythonRecords {

    @JsonProperty("RECORDS")
    private List<CoderEval4Python> records;

    public List<CoderEval4Python> getRecords() {
        return records;
    }

}
