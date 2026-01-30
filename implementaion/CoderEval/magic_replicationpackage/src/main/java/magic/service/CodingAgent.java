package magic.service;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodingAgent {

    interface CodingAssistant {
        @UserMessage("{{userMessage}}")
        String chat(@V("userMessage") String userMessage);
    }

    private final CodingAssistant assistant;

    public CodingAgent(ChatModel model) {
        this.assistant = AiServices.builder(CodingAssistant.class)
                .chatModel(model)
                .build();
    }

    public String generateJavaCode(String requirement, String context) {
        PromptTemplate template = PromptTemplate.from(
                """
                        Your task is to complete the following code.
                        You should first write a rough problem-solving process using three programming structures
                        (i.e., sequential, branch, and loop structures) and then output the final code.
                        
                        Here are some demonstration examples:
                        /**
                         * Write a java function to find sum of prime numbers between 1 to n.
                         */
                        public int sumOfPrimes(int n) {
                            // Let's think step by step
                            // Input: n, an integer
                            // Output: sum, an integer
                            // 1. Initialize a list "prime" with True values.
                            // 2. Initialize a variable "p" with 2.
                            // 3. While p * p is less than or equal to n:
                            // 4.   If prime[p] is True:
                            // 5.     Set all the multiples of p to False.
                            // 6.   Increment the variable "p" by 1.
                            // 7. Compute the sum of the prime numbers.
                            // 8. Return the sum.
                            // Write your code here
                            boolean[] prime = new boolean[n + 1];
                            for (int i = 0; i <= n; i++) {
                                prime[i] = true;
                            }
                            int p = 2;
                            while (p * p <= n) {
                                if (prime[p]) {
                                    for (int i = p * p; i <= n; i += p) {
                                        prime[i] = false;
                                    }
                                }
                                p += 1;
                            }
                            int sum = 0;
                            for (int i = 2; i <= n; i++) {
                                if (prime[i]) {
                                    sum += i;
                                }
                            }
                            return sum;
                        }

                        context:
                        {{context}}

                        Input code:
                        {{requirement}}
                            // Let's think step by step
                        
                        Respond ONLY with the complete input code in the following format, without any explanation text nearby:
                        ```java
                        Input code
                        ```
                        """
        );
        Map<String, Object> variables = Map.of("requirement", requirement.trim(), "context", context.trim());
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

    public String generatePythonCode(String requirement, String context) {
        PromptTemplate template = PromptTemplate.from(
                """
                        Your task is to complete the following code.
                        You should first write a rough problem-solving process using three programming structures
                        (i.e., sequential, branch, and loop structures) and then output the final code.
                        
                        Here are some demonstration examples:
                        ```python
                        def sum_Of_Primes(n):
                            ""\" Write a python function to find sum of prime numbers between 1 to n. ""\"
                            # Let's think step by step
                            # Input: n, an integer
                            # Output: sum, an integer
                            # 1. Initialize a list "prime" with True values.
                            # 2. Initialize a variable "p" with 2.
                            # 3. While p * p is less than or equal to n:
                            # 4.   If prime[p] is True:
                            # 5.     Set all the multiples of p to False.
                            # 6.   Increment the variable "p" by 1.
                            # 7. Compute the sum of the prime numbers.
                            # 8. Return the sum.
                            # Write your code here
                            prime = [True] * (n + 1)
                            p = 2
                            while p * p <= n:
                                if prime[p]:
                                    for i in range(p * p, n + 1, p):
                                        prime[i] = False
                                p += 1
                            sum = 0
                            for i in range(2, n + 1):
                                if prime[i]:
                                    sum += i
                            return sum
                        ```
                        
                        context:
                        {{context}}

                        Input code:
                        {{requirement}}
                            # Let's think step by step
                        
                        Respond ONLY with the complete input code in the following format, without any explanation text nearby:
                        ```python
                        Input code
                        ```
                        """
        );
        Map<String, Object> variables = Map.of("requirement", requirement.trim(), "context", context.trim());
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
