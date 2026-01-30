package magic.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import magic.config.LLMConfig;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Baseline2Agent {
    interface Baseline2Assistant {
        @UserMessage("{{userMessage}}")
        String chat(@V("userMessage") String userMessage);
    }

    private final Baseline2Assistant assistant;

    public Baseline2Agent() {
        ChatModel model = LLMConfig.getGPT3_5Model();
        this.assistant = AiServices.builder(Baseline2Assistant.class)
                .chatModel(model)
                .systemMessageProvider(memory -> "Output ONLY the complete method. Do NOT include any import statements, package declarations, and class wrappers. Do NOT change the method signature.")
                .build();
    }

    public String fixJavaIssue(String code) {
        PromptTemplate template = PromptTemplate.from(
                """
                        With no explanation refactor the Java code to improve its quality and
                        [performance, complexity, coupling, cohesion, design size, readability, reusability, and understandability].
                        Respond ONLY with the fixed code in the following format, without any explanation text nearby:
                        ```java
                        Fixed Code
                        ```
                        
                        Java code:
                        ```java
                        {{code}}
                        ```
                        """
        );
        Map<String, Object> variables = Map.of("code", code);
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

    public String fixPythonIssue(String code) {
        PromptTemplate template = PromptTemplate.from(
                """
                        With no explanation refactor the Python code to improve its quality and
                        [performance, complexity, coupling, cohesion, design size, readability, reusability, and understandability].
                        Respond ONLY with the fixed code in the following format, without any explanation text nearby:
                        ```python
                        Fixed Code
                        ```
                        
                        Python code:
                        ```python
                        {{code}}
                        ```
                        """
        );
        Map<String, Object> variables = Map.of("code", code);
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

