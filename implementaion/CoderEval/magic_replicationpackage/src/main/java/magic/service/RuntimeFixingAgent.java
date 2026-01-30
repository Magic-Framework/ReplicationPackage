package magic.service;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
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

public class RuntimeFixingAgent {

    interface FixingAssistant {
        @UserMessage("{{userMessage}}")
        String chat(@V("userMessage") String userMessage);
    }

    private final FixingAssistant assistant;
    private final ChatMemory chatMemory;

    public RuntimeFixingAgent() {
        ChatModel model = LLMConfig.getDeepSeekModel();
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        this.assistant = AiServices.builder(FixingAssistant.class)
                .chatModel(model)
                .chatMemory(chatMemory)
                .build();
    }

    public void clearMemory() {
        chatMemory.clear();
    }

    public String fixJavaCode(String code, String error) {
        PromptTemplate template = PromptTemplate.from(
                """
                        You are a code fixing assistant. Here is the fixed code:
                        fixed code:
                        ```java
                        {{code}}
                        ```

                        The fixed code contains the following compilation or runtime error:
                        {{error}}
                        
                        Please provide a better code implementation that meets the expected functionality, passes the
                        test case, and does not introduce new quality issues.
                        
                        Respond ONLY with the complete fixed code in the following format, without any explanation text nearby:
                        ```java
                        Fixed Code
                        ```
                        """
        );
        Map<String, Object> variables = Map.of("code", code, "error", error);
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

    public String fixPythonCode(String code, String error) {
        PromptTemplate template = PromptTemplate.from(
                """
                        You are a code fixing assistant. Here is the fixed code and test case:
                        fixed code:
                        ```python
                        {{code}}
                        ```
                        
                        The fixed code contains the following syntax or runtime error:
                        {{error}}
                        
                        Please provide a better code implementation that meets the expected functionality, passes the
                        above test case, and does not introduce new quality issues.
                        
                        Respond ONLY with the complete fixed code in the following format, without any explanation text nearby:
                        ```python
                        Fixed Code
                        ```
                        """
        );
        Map<String, Object> variables = Map.of("code", code, "error", error);
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
