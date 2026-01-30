package magic.service;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import magic.config.LLMConfig;
import magic.dto.CodeChange;
import magic.dto.CodeViolation;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class KBAgent {

    private final Map<Object, ChatMemory> activeMemories = new ConcurrentHashMap<>();

    interface ChangeAssistant {
        @UserMessage("{{userMessage}}")
        CodeChange chat(@MemoryId String sessionId, @V("userMessage") String userMessage);
    }

    private final ChangeAssistant changeAssistant;

    public KBAgent() {
        ChatModel model = LLMConfig.getDeepSeekModel();
        this.changeAssistant = AiServices.builder(ChangeAssistant.class)
                .chatModel(model)
                .chatMemoryProvider(memoryId -> activeMemories.computeIfAbsent(memoryId, k ->
                        MessageWindowChatMemory.withMaxMessages(5)))
                .build();
    }

    public Map<String, Object> fixIssues(String language, String ruleName, String ruleDescription, String problematicCode,
                                         String correctCode) {
        String sessionId = UUID.randomUUID().toString();
        PromptTemplate template = PromptTemplate.from(
                """
                        ## Role
                        You are an expert software engineer with expertise in code refactoring and static analysis.
                        
                        ## Input
                        ### Code Quality Rule
                        - **Rule Name:** {{ruleName}}
                        - **Rule Description:** {{ruleDescription}}
                        
                        ### Non-compliant Code:
                        ```{{language}}
                        {{non-compliantCode}}
                        ```
                        
                        ### Compliant Code (reference only):
                        ```{{language}}
                        {{compliantCode}}
                        ```
                        
                        ## Instructions
                        1. Determine if the compliant code is the correct fix. ONLY if it does change the external behavior,
                        set "isCorrect" to `false` and provide a correct fix for the non-compliant code (e.g.,
                        remove empty code or change internal structure, but not modify business logic or add comments);
                        Otherwise, set "isCorrect" to `true`.
                        2. Identify and name the generic refactoring technique applied to resolve the rule violation.
                        """
        );
        Map<String, Object> variables = Map.of("language", language, "ruleName", ruleName, "ruleDescription", ruleDescription,
                "non-compliantCode", problematicCode, "compliantCode", correctCode);
        Prompt prompt = template.apply(variables);
        CodeChange output = changeAssistant.chat(sessionId, prompt.text());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sessionId", sessionId);
        response.put("isCorrect", output.isCorrect());
        String correctFix = extractCode(output.getCorrectFix());
        response.put("correctFix", correctFix);
        response.put("refactoringType", output.getRefactoringType());
        return response;
    }

    public static String extractCode(String text) {
        return text.replaceAll("(?s)^```[a-zA-Z]*\\n?(.*?)\\n?```$", "$1").trim();
    }

    public Map<String, Object> refineSolution(String sessionId, String language, String ruleName, String ruleDescription,
                                              String problematicCode, String correctCode, List<CodeViolation> codeViolations) {
        PromptTemplate template = PromptTemplate.from(
                """
                        ## Role
                        You are an expert software engineer with expertise in code refactoring and static analysis.
                        
                        ## Input
                        ### Code Quality Rule
                        - **Rule Name:** {{ruleName}}
                        - **Rule Description:** {{ruleDescription}}
                        
                        ### Non-compliant Code:
                        ```{{language}}
                        {{non-compliantCode}}
                        ```
                        
                        ### The Previously Fixed Code:
                        ```{{language}}
                        {{correctFix}}
                        ```
                        
                        ### Quality Issues
                        {{issues}}
                        
                        ## Instructions
                        1. The code you previously fixed contained unresolved quality issues, and provide a correct fix.
                        2. Identify and name the generic refactoring technique applied to resolve the rule violation.
                        """
        );
        StringBuilder sb = new StringBuilder();
        if (!codeViolations.isEmpty()) {
            sb.append("The fixed code fails to resolve the following quality issues:\n");
            for (CodeViolation codeViolation : codeViolations) {
                sb.append("- Line ").append(codeViolation.getStartLine()).append(": ");
                sb.append(codeViolation.getRuleName()).append(": ");
                sb.append(codeViolation.getDescription()).append("\n");
            }
        }
        Map<String, Object> variables = Map.of("language", language, "ruleName", ruleName, "ruleDescription", ruleDescription,
                "non-compliantCode", problematicCode, "correctFix", correctCode, "issues", sb.toString());
        Prompt prompt = template.apply(variables);
        CodeChange output = changeAssistant.chat(sessionId, prompt.text());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sessionId", sessionId);
        response.put("isCorrect", output.isCorrect());
        String correctFix = extractCode(output.getCorrectFix());
        response.put("correctFix", correctFix);
        response.put("refactoringType", output.getRefactoringType());
        return response;
    }

    public void clearMemory(String sessionId) {
        ChatMemory memory = activeMemories.remove(sessionId);
        if (memory != null) {
            memory.clear();
        }
    }
}
