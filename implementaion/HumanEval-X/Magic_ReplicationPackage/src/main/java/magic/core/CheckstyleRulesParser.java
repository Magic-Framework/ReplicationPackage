package magic.core;

import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import magic.dto.CheckstyleRule;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CheckstyleRulesParser {

    private static final String RESOURCES_PATH = "checkstylerules";
    private static final String EXAMPLES_YAML = RESOURCES_PATH + "/examples.yml";
    private static final FlexmarkHtmlConverter MD_CONVERTER = FlexmarkHtmlConverter.builder().build();
    private static final Map<String, String> CHECK_NS = Map.of("ns", "http://maven.apache.org/XDOC/2.0");

    public static void main(String[] args) throws Exception {
        CheckstyleRulesParser parser = new CheckstyleRulesParser();
        CheckstyleRule rule = parser.extractRule("VisibilityModifier");
        System.out.println(rule);
    }

    public CheckstyleRule extractRule(String ruleName) throws Exception {
        URL url = getClassLoader().getResource(RESOURCES_PATH);
        if (url == null) return null;
        Path targetXmlPath = findXmlInDirectory(url, ruleName);
        if (targetXmlPath == null) return null;
        CheckstyleRule rule = parseXml(targetXmlPath);
        if (rule == null) return null;
        fillExamplesFromYaml(ruleName, rule);
        return rule;
    }

    private ClassLoader getClassLoader() {
        return Optional.ofNullable(Thread.currentThread().getContextClassLoader()).orElse(getClass().getClassLoader());
    }

    private CheckstyleRule parseXml(Path xmlPath) {
        try (InputStream is = Files.newInputStream(xmlPath)) {
            SAXReader reader = new SAXReader();
            reader.getDocumentFactory().setXPathNamespaceURIs(CHECK_NS);
            Document document = reader.read(is);
            Node titleNode = document.selectSingleNode("//ns:title");
            String nameInXml = (titleNode != null) ? titleNode.getText().trim() :
                    xmlPath.getFileName().toString().replace(".xml", "");
            String descXPath = "//ns:subsection[@name='Description']/ns:div";
            Node descNode = createXPath(document, descXPath).selectSingleNode(document);
            String desc = (descNode != null) ? convertToCompactMarkdown(descNode.asXML()) : "";
            String codeFromXml = findExampleCode(document);
            CheckstyleRule rule = new CheckstyleRule();
            rule.setName(nameInXml);
            rule.setDescription(desc);
            rule.setCodeBefore(codeFromXml);
            return rule;
        } catch (Exception e) {
            return null;
        }
    }

    private String findExampleCode(Document document) {
        String xpathExpr = "//ns:p[contains(@id, '-code')]";
        List<Node> pNodes = createXPath(document, xpathExpr).selectNodes(document);
        for (Node pNode : pNodes) {
            Node codeNode = pNode.selectSingleNode("following-sibling::ns:div[1]//ns:code");
            if (codeNode != null) {
                String rawCode = codeNode.getText();
                if (rawCode != null && rawCode.contains("// violation")) {
                    return rawCode.trim();
                }
            }
        }
        return null;
    }

    private void fillExamplesFromYaml(String ruleName, CheckstyleRule rule) {
        try (InputStream is = getClassLoader().getResourceAsStream(EXAMPLES_YAML);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            StringBuilder docBuffer = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("---")) {
                    processYamlSection(docBuffer.toString(), ruleName, rule);
                    docBuffer.setLength(0);
                } else
                    docBuffer.append(line).append("\n");
            }
            processYamlSection(docBuffer.toString(), ruleName, rule);
        } catch (IOException ignored) {
        }
    }

    private void processYamlSection(String content, String ruleName, CheckstyleRule rule) {
        if (content.contains("recipeName:") && content.contains("." + ruleName)) {
            String beforeCode = extractYamlBlock(content, "before: |");
            String afterCode = extractYamlBlock(content, "after: |");
            if (beforeCode != null)
                rule.setCodeBefore(beforeCode);
            if (afterCode != null)
                rule.setCodeAfter(afterCode);
        }
    }

    private String extractYamlBlock(String content, String marker) {
        String[] lines = content.split("\n");
        StringBuilder code = new StringBuilder();
        boolean inBlock = false;
        int blockIndent = -1;
        for (String line : lines) {
            if (!inBlock) {
                if (line.contains(marker))
                    inBlock = true;
                continue;
            }
            String trimmedLine = line.trim();
            if (trimmedLine.isEmpty()) {
                code.append("\n");
                continue;
            }
            int currentIndent = 0;
            while (currentIndent < line.length() && line.charAt(currentIndent) == ' ')
                currentIndent++;
            if (blockIndent == -1)
                blockIndent = currentIndent;
            if (currentIndent < blockIndent)
                break;
            if (line.length() >= blockIndent)
                code.append(line.substring(blockIndent)).append("\n");
            else
                code.append(line.trim()).append("\n");
        }
        return !code.isEmpty() ? code.toString().trim() : null;
    }

    private String convertToCompactMarkdown(String xmlFragment) {
        if (xmlFragment == null || xmlFragment.isEmpty()) return "";
        org.jsoup.nodes.Document doc = Jsoup.parseBodyFragment(xmlFragment);
        doc.select("a").unwrap();
        doc.select("div.wrapper").unwrap();
        doc.select("subsection").unwrap();
        String markdown = MD_CONVERTER.convert(doc.body().html());
        return markdown.lines().map(String::stripTrailing).filter(l -> !l.isBlank()).collect(Collectors.joining("\n"));
    }

    private Path findXmlInDirectory(URL url, String ruleName) throws Exception {
        URI uri = url.toURI();
        String target = ruleName + ".xml";
        Path root = Paths.get(uri);
        try (Stream<Path> s = Files.walk(root)) {
            return s.filter(Files::isRegularFile).filter(p -> p.getFileName().toString()
                    .equalsIgnoreCase(target)).findFirst().orElse(null);
        }
    }

    private XPath createXPath(Document doc, String expr) {
        XPath xpath = doc.createXPath(expr);
        xpath.setNamespaceURIs(CHECK_NS);
        return xpath;
    }
}