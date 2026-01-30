package magic.dto;

public class PMDRule {

    private String name;
    private String description;
    private String codeBefore;
    private String codeAfter;

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

    public String getCodeBefore() {
        return codeBefore;
    }

    public void setCodeBefore(String codeBefore) {
        this.codeBefore = codeBefore;
    }

    public String getCodeAfter() {
        return codeAfter;
    }

    public void setCodeAfter(String codeAfter) {
        this.codeAfter = codeAfter;
    }

    @Override
    public String toString() {
        return "PMDRule{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", codeBefore='" + codeBefore + '\'' +
                ", codeAfter='" + codeAfter + '\'' +
                '}';
    }
}
