package magic.data;

import magic.dto.CoderEval4Java;
import magic.dto.CoderEval4JavaRecords;
import magic.dto.CoderEval4Python;
import magic.dto.CoderEval4PythonRecords;
import magic.dto.GenerateCode;
import magic.dto.GenerateCode4CoderEval;
import magic.dto.ImprovedCode;
import magic.dto.ImprovedCode4CoderEval;
import magic.dto.TaskEntry;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public static List<CoderEval4Java> readCoderEval4Java() {
        ObjectMapper mapper = new ObjectMapper();
        String relativePath = "travis/builds/java/CoderEval4Java.json";
        Path path = Paths.get(relativePath);
        try {
            // 1. 读取整个包装类
            File jsonFile = path.toFile();
            CoderEval4JavaRecords response = mapper.readValue(jsonFile, CoderEval4JavaRecords.class);
            // 2. 获取 List
            List<CoderEval4Java> list = response.getRecords();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static List<CoderEval4Python> readCoderEval4Python() {
        ObjectMapper mapper = new ObjectMapper();
        String relativePath = "travis/builds/CoderEval4Python.json";
        Path path = Paths.get(relativePath);
        try {
            // 1. 读取整个包装类
            File jsonFile = path.toFile();
            CoderEval4PythonRecords response = mapper.readValue(jsonFile, CoderEval4PythonRecords.class);
            // 2. 获取 List
            List<CoderEval4Python> list = response.getRecords();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static List<GenerateCode4CoderEval> readGenerateCode4CoderEval(String fileName) {
        List<GenerateCode4CoderEval> entries = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = DataProvider.class.getResourceAsStream("/" + fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                GenerateCode4CoderEval entry = mapper.readValue(line, GenerateCode4CoderEval.class);
                entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    public static List<ImprovedCode4CoderEval> readImprovedCode4CoderEval(String fileName) {
        List<ImprovedCode4CoderEval> entries = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream inputStream = DataProvider.class.getResourceAsStream("/" + fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            String line;
            while ((line = reader.readLine()) != null) {
                ImprovedCode4CoderEval entry = mapper.readValue(line, ImprovedCode4CoderEval.class);
                entries.add(entry);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }
}
