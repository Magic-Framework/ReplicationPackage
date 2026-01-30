package magic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CoderEval4Java {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("all_context")
    private String allContext;

    @JsonProperty("class_name")
    private String className;

    private String code;

    @JsonProperty("docstring")
    private String docString;

    @JsonProperty("end_lineno")
    private String endLineno;

    @JsonProperty("file_content")
    private String fileContent;

    @JsonProperty("file_name")
    private String fileName;

    @JsonProperty("human_label")
    private String humanLabel;

    private String level;

    private String lineno;

    private String name;

    @JsonProperty("oracle_context")
    private String oracleContext;

    @JsonProperty("package")
    private String packageName;

    private String project;

    public String getId() {
        return id;
    }

    public String getAllContext() {
        return allContext;
    }

    public String getClassName() {
        return className;
    }

    public String getCode() {
        return code;
    }

    public String getDocString() {
        return docString;
    }

    public String getEndLineno() {
        return endLineno;
    }

    public String getFileContent() {
        return fileContent;
    }

    public String getFileName() {
        return fileName;
    }

    public String getHumanLabel() {
        return humanLabel;
    }

    public String getLevel() {
        return level;
    }

    public String getLineno() {
        return lineno;
    }

    public String getName() {
        return name;
    }

    public String getOracleContext() {
        return oracleContext;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getProject() {
        return project;
    }
}
