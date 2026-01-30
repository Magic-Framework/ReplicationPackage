## 1. Self-Reflective Synthesis of Code Examples                       

    ## Role
    You are an expert software engineer with expertise in code refactoring and static analysis.
                            
    ## Input
    ### Code Quality Rule
    - **Rule Name:** {{ruleName}}
    - **Rule Description:** {{ruleDescription}}
                            
    ### Non-compliant Code:
    ```language
    {{non-compliantCode}}
    ```
                            
    ### Compliant Code (reference only):
    ```{{language}}
    {{compliantCode}}
    ```
                            
    ## Instructions
    1. Determine if the compliant code is the correct fix. ONLY if it does change the external behavior, set "isCorrect" to `false` and provide a correct fix for the non-compliant code (e.g., remove empty code or change internal structure, but not modify business logic or add comments); therwise, set "isCorrect" to `true`.
    2. Identify and name the generic refactoring technique applied to resolve the rule violation.

------

    ## Role
    You are an expert software engineer with expertise in code refactoring and static analysis.
                            
    ## Input
    ### Code Quality Rule
    - **Rule Name:** {{ruleName}}
    - **Rule Description:** {{ruleDescription}}
                            
    ### Non-compliant Code:
    ```language
    {{non-compliantCode}}
    ```
                            
    ### The Previously Fixed Code:
    ```language
    {{correctFix}}
    ```
    
    ### Quality Issues
    {{issues}}
                            
    ## Instructions
    1. The code you previously fixed contained unresolved quality issues, and provide a correct fix.
    2. Identify and name the generic refactoring technique applied to resolve the rule violation.

## 2. Plan-and-Execute Code Issue Fixing  

    You are a Java code fixing assistant. Here is a piece of LLM-generated code:
    ```language
    {{code}}
    ```
                            
    Your task is to refactor the code which contains the following quality issues:
    {{issues}}
                            
    To fix the quality issues in the code, the corresponding domain-specific language (DSL) is:
    {{rules}}
                            
    Respond ONLY with the complete fixed code in the following format, without any explanation text nearby:
    ```language
    Fixed code
    ```

##  3. Reflexion-Based Behavior Validation

    You are a code validation assistant. Here is a piece of code and a fixed version:
    
    original code:
    ```language
    {{originalCode}}
    ```
    
    fixed code:
    ```language
    {{fixedCode}}
    ```
    
    The fixed code is not functionally equivalent to the original code. Please generate a natural language description that highlights what’s wrong, why it fails, and how to fix it.

------

    You are a code fixing assistant. Here is the fixed code:
    
    fixed code:
    ```language
    {{code}}
    ```
    
    The fixed code contains the following compilation or runtime error:
    {{error}}
                            
    Please provide a better code implementation that meets the expected functionality, passes the test case, and does not introduce new quality issues.
    Respond ONLY with the complete fixed code in the following format, without any explanation text nearby:
    ```language
    Fixed Code
    ```

