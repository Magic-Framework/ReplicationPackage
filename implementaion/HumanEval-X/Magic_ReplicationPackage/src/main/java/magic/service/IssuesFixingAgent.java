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
import magic.data.DSLRuleProvider;
import magic.dto.CodeViolation;
import magic.dto.RefactoringRule;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IssuesFixingAgent {

    interface FixingAssistant {
        @UserMessage("{{userMessage}}")
        String chat(@V("userMessage") String userMessage);
    }

    private final FixingAssistant assistant;
    private final ChatMemory chatMemory;

    public IssuesFixingAgent() {
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

    public String fixJavaIssue(String code, List<CodeViolation> codeViolations) throws Exception {
        PromptTemplate template = PromptTemplate.from(
                """
                        You are a Java code fixing assistant. Here is a piece of LLM-generated code:
                        ```java
                        {{code}}
                        ```
                        
                        Your task is to refactor the code which contains the following quality issues:
                        {{issues}}
                        
                        To fix the quality issues in the code, the corresponding domain-specific language (DSL) is:
                        {{rules}}
                        
                        Respond ONLY with the complete fixed code in the following format, without any explanation text nearby:
                        ```java
                        Fixed code
                        ```
                        """
        );
        StringBuilder sb = new StringBuilder();
        StringBuilder rules = new StringBuilder();
        Map<String, RefactoringRule> refactoringRules = DSLRuleProvider.readJavaRefactoringRules(codeViolations);
        for (CodeViolation codeViolation : codeViolations) {
            sb.append("Solution.java:").append(codeViolation.getStartLine()).append(": ");
            sb.append(codeViolation.getRuleName()).append(": ");
            sb.append(codeViolation.getDescription()).append("\n");
            RefactoringRule rule = refactoringRules.get(codeViolation.getRuleName());
            rules.append("RuleName:").append(rule.getRuleName()).append("\n");
            rules.append("Description:").append(rule.getDescription()).append("\n");
            rules.append("RefactoringType:").append(rule.getRefactoringType()).append("\n");
            rules.append("RefactoringExamples:").append("\n");
            rules.append("CodeBefore:").append("\n").append("```java\n").append(rule.getCodeBefore()).append("\n```").append("\n");
            rules.append("CodeAfter:").append("\n").append("```java\n").append(rule.getCodeAfter()).append("\n```").append("\n");
        }
        Map<String, Object> variables = Map.of("code", code, "issues", sb.toString(), "rules", rules.toString());
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

    public String fixPythonIssue(String code, List<CodeViolation> codeViolations) throws IOException {
        PromptTemplate template = PromptTemplate.from(
                """
                        You are a Python code fixing assistant. Here is a piece of LLM-generated code:
                        ```python
                        {{code}}
                        ```
                        
                        Your task is to refactor the code which contains the following quality issues:
                        {{issues}}
                        
                        To fix the quality issues in the code, the corresponding domain-specific language (DSL) is:
                        {{rules}}
                        
                        Respond ONLY with the complete fixed code in the following format, without any explanation text nearby:
                        ```python
                        Fixed code
                        ```
                        """
        );
        StringBuilder sb = new StringBuilder();
        StringBuilder rules = new StringBuilder();
        Map<String, RefactoringRule> refactoringRules = DSLRuleProvider.readPythonRefactoringRules(codeViolations);
        for (CodeViolation codeViolation : codeViolations) {
            sb.append("solution.py:").append(codeViolation.getStartLine()).append(": ");
            sb.append(codeViolation.getRuleName()).append(": ");
            sb.append(codeViolation.getDescription()).append("\n");
            RefactoringRule rule = refactoringRules.get(codeViolation.getRuleName());
            rules.append("RuleName:").append(rule.getRuleName()).append("\n");
            rules.append("Description:").append(rule.getDescription()).append("\n");
            rules.append("RefactoringType:").append(rule.getRefactoringType()).append("\n");
            rules.append("RefactoringExamples:").append("\n");
            rules.append("CodeBefore:").append("\n").append("```python\n").append(rule.getCodeBefore()).append("\n```").append("\n");
            rules.append("CodeAfter:").append("\n").append("```python\n").append(rule.getCodeAfter()).append("\n```").append("\n");
        }
        Map<String, Object> variables = Map.of("code", code, "issues", sb.toString(), "rules", rules.toString());
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
