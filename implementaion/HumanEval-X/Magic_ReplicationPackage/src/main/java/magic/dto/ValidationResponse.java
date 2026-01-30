package magic.dto;

import dev.langchain4j.model.output.structured.Description;

public class ValidationResponse {

    @Description("Description of what's wrong, why it fails, and how to fix it.")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
