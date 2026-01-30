package magic.data;

import magic.dto.GenerateCode;
import magic.dto.ImprovedCode;
import magic.dto.TaskEntry;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class DataProvider {

    public static List<TaskEntry> readJSONL() {
        List<TaskEntry> entries = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = DataProvider.class.getResourceAsStream("/java/humaneval.jsonl");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                TaskEntry entry = mapper.readValue(line, TaskEntry.class);
                entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static List<GenerateCode> readGenerateCode(String fileName) {
        List<GenerateCode> entries = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = DataProvider.class.getResourceAsStream("/" + fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                GenerateCode entry = mapper.readValue(line, GenerateCode.class);
                entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static List<ImprovedCode> readImprovedCode(String fileName) {
        List<ImprovedCode> entries = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = DataProvider.class.getResourceAsStream("/" + fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                ImprovedCode entry = mapper.readValue(line, ImprovedCode.class);
                entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }
}
