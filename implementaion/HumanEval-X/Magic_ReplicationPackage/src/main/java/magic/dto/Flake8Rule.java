package magic.dto;

public class Flake8Rule {

    private String id;
    private String name;
    private String description;
    private String antiPattern;
    private String bestPractice;

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

    public String getAntiPattern() {
        return antiPattern;
    }

    public void setAntiPattern(String antiPattern) {
        this.antiPattern = antiPattern;
    }

    public String getBestPractice() {
        return bestPractice;
    }

    public void setBestPractice(String bestPractice) {
        this.bestPractice = bestPractice;
    }

    @Override
    public String toString() {
        return "Flake8Rule{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", antiPattern='" + antiPattern + '\'' +
                ", bestPractice='" + bestPractice + '\'' +
                '}';
    }
}
