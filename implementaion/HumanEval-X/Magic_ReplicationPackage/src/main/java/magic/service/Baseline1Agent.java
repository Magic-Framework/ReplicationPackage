package magic.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import magic.config.LLMConfig;
import magic.dto.CodeViolation;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Baseline1Agent {

    interface Baseline1Assistant {
        @UserMessage("{{userMessage}}")
        String chat(@V("userMessage") String userMessage);
    }

    private final Baseline1Assistant assistant;

    public Baseline1Agent() {
        ChatModel model = LLMConfig.getGPT3_5Model();
        this.assistant = AiServices.builder(Baseline1Assistant.class)
                .chatModel(model)
                .build();
    }

    public String fixJavaIssue(String code, List<CodeViolation> codeViolations) {
        PromptTemplate template = PromptTemplate.from(
                """
                        You are an assistant. Here is a piece of code:
                        ```java
                        {{code}}
                        ```
                        
                        The generated code contains the following quality issues:
                        {{issues}}
                        
                        Please provide a better code implementation as expected by the task description.
                        Respond ONLY with the fixed code in the following format, without any explanation text nearby:
                        ```java
                        Fixed code
                        ```
                        """
        );
        StringBuilder sb = new StringBuilder();
        for (CodeViolation codeViolation : codeViolations) {
            sb.append("Solution.java:").append(codeViolation.getStartLine()).append(": ");
            sb.append(codeViolation.getRuleName()).append(": ");
            sb.append(codeViolation.getDescription()).append("\n");
        }

        Map<String, Object> variables = Map.of("code", code, "issues", sb.toString());
        Prompt prompt = template.apply(variables);
        String response = assistant.chat(prompt.text());
        String regex = "```java(.*?)```";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    public String fixPythonIssue(String code, List<CodeViolation> codeViolations) {
        PromptTemplate template = PromptTemplate.from(
                """
                        You are an assistant. Here is a piece of code:
                        ```python
                        {{code}}
                        ```
                        
                        The generated code contains the following quality issues:
                        {{issues}}
                        
                        Please provide a better code implementation as expected by the task description.
                        Respond ONLY with the fixed code in the following format, without any explanation text nearby:
                        ```python
                        Fixed code
                        ```
                        """
        );
        StringBuilder sb = new StringBuilder();
        for (CodeViolation codeViolation : codeViolations) {
            sb.append("Solution.py:").append(codeViolation.getStartLine()).append(": ");
            sb.append(codeViolation.getRuleName()).append(": ");
            sb.append(codeViolation.getDescription()).append("\n");
        }
        Map<String, Object> variables = Map.of("code", code, "issues", sb.toString());
        Prompt prompt = template.apply(variables);
        String response = assistant.chat(prompt.text());
        String regex = "```python(.*?)```";
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }
}

