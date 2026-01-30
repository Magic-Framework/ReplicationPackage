package magic.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatRequestParameters;

import java.time.Duration;
import java.util.Map;

public class LLMConfig {

    public static ChatModel getDeepSeekModel() {
        Boolean enableThinking = false;
        Map<String, Object> customParameters = Map.of("enable_thinking", enableThinking);
        OpenAiChatRequestParameters parameters = OpenAiChatRequestParameters.builder()
                .customParameters(customParameters)
                .build();

        return OpenAiChatModel.builder()
                .baseUrl("https://api.siliconflow.cn/v1")
                .apiKey(ApiKeys.SILICON_API_KEY)
                .modelName("Pro/deepseek-ai/DeepSeek-V3.1-Terminus")
                .logRequests(true)
                .logResponses(true)
                .temperature(0.0)
                .timeout(Duration.ofSeconds(60))
                .maxRetries(3)
                .defaultRequestParameters(parameters)
                .build();
    }


    public static ChatModel getGPT5Model() {
        Map<String, String> reasoning = Map.of(
                "effort", "minimal"
        );
        Map<String, Object> customParameters = Map.of("reasoning", reasoning);
        OpenAiChatRequestParameters parameters = OpenAiChatRequestParameters.builder()
                .customParameters(customParameters)
                .build();

        return OpenAiChatModel.builder()
                .baseUrl("https://api.chatanywhere.tech/v1")
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName("gpt-5")
                .logResponses(true)
                .logResponses(true)
                .temperature(0.0)
                .timeout(Duration.ofSeconds(60))
                .maxRetries(3)
                .defaultRequestParameters(parameters)
                .build();
    }

    public static ChatModel getGPT3_5Model() {
        return OpenAiChatModel.builder()
                .baseUrl("https://api.chatanywhere.tech/v1")
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName("gpt-3.5-turbo")
                .logResponses(true)
                .logResponses(true)
                .temperature(0.0)
                .timeout(Duration.ofSeconds(60))
                .maxRetries(3)
                .build();
    }

    public static ChatModel getQwenModel() {
        return OpenAiChatModel.builder()
                .baseUrl("https://api.siliconflow.cn/v1")
                .apiKey(ApiKeys.SILICON_API_KEY)
                .modelName("Qwen/Qwen3-Coder-480B-A35B-Instruct")
                .logResponses(true)
                .logResponses(true)
                .temperature(0.0)
                .timeout(Duration.ofSeconds(120))
                .maxRetries(3)
                .build();
    }
}