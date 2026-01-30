package magic.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import magic.config.LLMConfig;
import magic.dto.ValidationResponse;

import java.util.Map;

public class ValidationAgent {

    interface ValidationAssistant {
        @UserMessage("{{userMessage}}")
        ValidationResponse chat(@V("userMessage") String userMessage);
    }

    private final ValidationAssistant assistant;

    public ValidationAgent() {
        ChatModel model = LLMConfig.getDeepSeekModel();
        this.assistant = AiServices.builder(ValidationAssistant.class)
                .chatModel(model)
                .build();
    }

    public String validateJavaCode(String originalCode, String fixedCode) {
        PromptTemplate template = PromptTemplate.from(
                """
                        You are a code validation assistant. Here is a piece of code and a fixed version:
                        original code:
                        ```java
                        {{originalCode}}
                        ```
                        fixed code:
                        ```java
                        {{fixedCode}}
                        ```
                        The fixed code is not functionally equivalent to the original code.
                        Please generate a natural language description that highlights what’s wrong, why it fails, and
                        how to fix it.
                        """
        );
        Map<String, Object> variables = Map.of("originalCode", originalCode, "fixedCode", fixedCode);
        Prompt prompt = template.apply(variables);
        ValidationResponse response = assistant.chat(prompt.text());
        return response.getDescription();
    }

    public String validatePythonCode(String originalCode, String fixedCode) {
        PromptTemplate template = PromptTemplate.from(
                """
                        You are a code validation assistant. Here is a piece of code and a fixed version:
                        original code:
                        ```python
                        {{originalCode}}
                        ```
                        fixed code:
                        ```python
                        {{fixedCode}}
                        ```
                        The fixed code is not functionally equivalent to the original code.
                        Please generate a natural language description that highlights what’s wrong, why it fails, and
                        how to fix it.
                        """
        );
        Map<String, Object> variables = Map.of("originalCode", originalCode, "fixedCode", fixedCode);
        Prompt prompt = template.apply(variables);
        ValidationResponse response = assistant.chat(prompt.text());
        return response.getDescription();
    }
}
