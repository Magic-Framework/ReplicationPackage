package magic.experiment;

import magic.data.DataProvider;
import magic.dto.TaskEntry;
import magic.service.CodingAgent;
import magic.util.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Coding {

    private static final CodingAgent codingAgent = new CodingAgent();
    private static final String outputFile = "E:/gpt-5-coding-python.jsonl";

    public static void main(String[] args) throws Exception {
        List<TaskEntry> taskEntries = DataProvider.readJSONL();
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile, true))) {
            for (TaskEntry taskEntry : taskEntries) {
                String taskId = taskEntry.getTaskId();
                System.out.println("starting... " + taskId);
                String prompt = taskEntry.getPrompt();
                String test = taskEntry.getTest();
                Map<String, String> json = new LinkedHashMap<>();
                json.put("taskId", taskId);
                json.put("prompt", prompt);
                json.put("test", test);
                boolean isOk = false;
                for (int i = 0; i < 5; i++) {
                    String code = codingAgent.generatePythonCode(prompt);
                    json.put("generatedCode", code);
                    boolean flag = TestUtils.executePythonCode(code, test);
                    if (flag) {
                        isOk = true;
                        break;
                    }
                }
                if (isOk) {
                    writer.write(mapper.writeValueAsString(json));
                    writer.newLine();
                    writer.flush();
                } else {
                    System.out.println("failed... " + taskId);
                }
            }
        }
    }
}
