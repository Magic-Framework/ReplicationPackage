package magic.core;

import magic.dto.PylintRule;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PylintRulesParser {

    private static final String RESOURCE_BASE = "pylintrules";
    private static final String INDEX_FILE_NAME = "pylint_rules.txt";
    private static final String SEPARATOR = "::";

    public static void main(String[] args) {
        PylintRulesParser parser = new PylintRulesParser();
        PylintRule pylintRule = parser.extractRule("C0325");
        System.out.println(pylintRule);
    }

    public PylintRule extractRule(String ruleID) {
        String indexPath = RESOURCE_BASE + "/" + INDEX_FILE_NAME;

        try (InputStream is = getClassLoader().getResourceAsStream(indexPath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return reader.lines()
                    .map(line -> line.split(SEPARATOR, 3))
                    .filter(parts -> parts.length >= 3 && parts[0].trim().equals(ruleID))
                    .findFirst()
                    .map(parts -> buildRule(parts[0].trim(), parts[1].trim(), parts[2].trim()))
                    .orElse(null);
        } catch (IOException e) {
            return null;
        }
    }

    private PylintRule buildRule(String id, String name, String desc) {
        String firstLetter = name.isEmpty() ? "_" : name.substring(0, 1).toLowerCase();
        String ruleBasePath = String.format("%s/messages/%s/%s", RESOURCE_BASE, firstLetter, name);
        PylintRule rule = new PylintRule();
        rule.setId(id);
        rule.setName(name);
        rule.setDescription(desc);
        rule.setProblematicCode(loadCodeContent(ruleBasePath, "bad"));
        rule.setCorrectCode(loadCodeContent(ruleBasePath, "good"));
        return rule;
    }

    private Map<String, String> loadCodeContent(String basePath, String type) {
        String fileName = type + ".py";
        byte[] data = readRawResource(basePath + "/" + fileName);
        if (data != null)
            return Collections.singletonMap(fileName, new String(data, StandardCharsets.UTF_8));
        URL dirUrl = getClassLoader().getResource(basePath + "/" + type);
        if (dirUrl == null) return Collections.emptyMap();
        try {
            URI uri = dirUrl.toURI();
            return walkAndRead(Paths.get(uri));
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }

    private Map<String, String> walkAndRead(Path rootPath) throws IOException {
        try (Stream<Path> walk = Files.walk(rootPath)) {
            return walk.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".py"))
                    .sorted(Comparator.comparing(p -> rootPath.relativize(p).toString().replace("\\", "/")))
                    .collect(Collectors.toMap(
                            p -> rootPath.relativize(p).toString().replace("\\", "/"),
                            p -> {
                                try {
                                    return Files.readString(p, StandardCharsets.UTF_8);
                                } catch (IOException e) {
                                    throw new UncheckedIOException(e);
                                }
                            },
                            (u, v) -> u,
                            LinkedHashMap::new
                    ));
        }
    }

    private byte[] readRawResource(String path) {
        try (InputStream is = getClassLoader().getResourceAsStream(path)) {
            return (is == null) ? null : is.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    private ClassLoader getClassLoader() {
        return Optional.ofNullable(Thread.currentThread().getContextClassLoader()).orElse(getClass().getClassLoader());
    }
}