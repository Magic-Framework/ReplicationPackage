package magic.dto;

import java.util.Map;

public class PylintRule {

    private String id;
    private String name;
    private String description;
    private Map<String, String> problematicCode;
    private Map<String, String> correctCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getProblematicCode() {
        return problematicCode;
    }

    public void setProblematicCode(Map<String, String> problematicCode) {
        this.problematicCode = problematicCode;
    }

    public Map<String, String> getCorrectCode() {
        return correctCode;
    }

    public void setCorrectCode(Map<String, String> correctCode) {
        this.correctCode = correctCode;
    }

    @Override
    public String toString() {
        return "PylintRule{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", problematicCode=" + problematicCode +
                ", correctCode=" + correctCode +
                '}';
    }
}
