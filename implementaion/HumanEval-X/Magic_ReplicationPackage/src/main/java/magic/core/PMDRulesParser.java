package magic.core;

import magic.dto.PMDRule;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class PMDRulesParser {

    private static final String RESOURCES_PATH = "pmdrules/java";
    private static final String TEST_DATA_PATH = "pmdrules/rule";

    private static final Map<String, String> RULESET_NS = Map.of("pmd", "http://pmd.sourceforge.net/ruleset/2.0.0");
    private static final Map<String, String> TESTDATA_NS = Map.of("td", "http://pmd.sourceforge.net/rule-tests");

    public static void main(String[] args) throws Exception {
        PMDRulesParser parser = new PMDRulesParser();
        PMDRule rule = parser.extractRule("UnnecessaryCast");
        System.out.println(rule);
    }

    /**
     * 根据手动输入的 ruleName 提取规则
     */
    public PMDRule extractRule(String ruleName) throws Exception {
        PMDRule rule = findRuleDefinition(ruleName);
        if (rule == null) return null;
        fillExamplesFromTestXml(ruleName, rule);
        return rule;
    }

    private PMDRule findRuleDefinition(String ruleName) throws Exception {
        URL url = getClassLoader().getResource(RESOURCES_PATH);
        if (url == null) return null;
        URI uri = url.toURI();
        try (Stream<Path> paths = Files.walk(Paths.get(uri))) {
            return paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".xml"))
                    .map(path -> parseSingleRuleFromXml(path, ruleName))
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
    }

    private ClassLoader getClassLoader() {
        return Optional.ofNullable(Thread.currentThread().getContextClassLoader()).orElse(getClass().getClassLoader());
    }

    private PMDRule parseSingleRuleFromXml(Path xmlPath, String ruleName) {
        try (InputStream is = Files.newInputStream(xmlPath)) {
            SAXReader reader = new SAXReader();
            Document document = reader.read(is);
            XPath ruleSelector = document.createXPath("//pmd:rule[@name='" + ruleName + "']");
            ruleSelector.setNamespaceURIs(RULESET_NS);
            Node node = ruleSelector.selectSingleNode(document);
            if (node instanceof Element element) {
                PMDRule rule = new PMDRule();
                rule.setName(ruleName);
                XPath descPath = element.createXPath("pmd:description");
                descPath.setNamespaceURIs(RULESET_NS);
                Node descNode = descPath.selectSingleNode(element);
                rule.setDescription(descNode != null ? cleanDescription(descNode.getText()) : "");
                return rule;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    private void fillExamplesFromTestXml(String ruleName, PMDRule rule) throws Exception {
        URL url = getClassLoader().getResource(TEST_DATA_PATH);
        if (url == null) return;
        String targetFileName = ruleName + ".xml";
        URI uri = url.toURI();
        try (Stream<Path> paths = Files.walk(Paths.get(uri))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.getFileName().toString().equalsIgnoreCase(targetFileName))
                    .findFirst().ifPresent(testXmlPath -> extractCodeFromTestXml(testXmlPath, rule));
        }
    }

    private void extractCodeFromTestXml(Path xmlPath, PMDRule rule) {
        try (InputStream is = Files.newInputStream(xmlPath)) {
            SAXReader reader = new SAXReader();
            Document document = reader.read(is);
            XPath selector = document.createXPath("//td:test-code");
            selector.setNamespaceURIs(TESTDATA_NS);

            List<Node> testCodes = selector.selectNodes(document);

            String badCode = null;
            String goodCode = null;
            int badCodeLOC = Integer.MAX_VALUE;
            int goodCodeLOC = Integer.MAX_VALUE;

            // 新增：记录遇到的最小问题数，初始化为一个极大值
            int minProblemsFound = Integer.MAX_VALUE;

            for (Node node : testCodes) {
                if (node instanceof Element element) {
                    String problemsStr = element.elementText("expected-problems");
                    String rawCode = element.elementText("code");
                    if (problemsStr == null || rawCode == null) continue;

                    try {
                        int problems = Integer.parseInt(problemsStr.trim());
                        String cleanedCode = cleanCode(rawCode);
                        int lineCount = cleanedCode.split("\n").length;

                        if (problems == 0) {
                            // 逻辑不变：找行数最少的好代码
                            if (lineCount < goodCodeLOC) {
                                goodCodeLOC = lineCount;
                                goodCode = cleanedCode;
                            }
                        } else if (problems > 0) {
                            // 核心优化：寻找 problems 最小的样本
                            // 如果当前问题数更少，或者问题数相同但行数更短
                            if (problems < minProblemsFound || (problems == minProblemsFound && lineCount < badCodeLOC)) {
                                minProblemsFound = problems;
                                badCodeLOC = lineCount;
                                badCode = cleanedCode;
                            }
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
            rule.setCodeBefore(badCode);
            rule.setCodeAfter(goodCode);
        } catch (Exception e) {
            System.err.println("解析测试文件失败: " + xmlPath + " 原因: " + e.getMessage());
        }
    }

    private String cleanDescription(String raw) {
        if (raw == null) return "";
        String text = raw.trim();
        String[] paragraphs = text.split("\\n\\s*\\n", 2);
        String firstParagraph = paragraphs[0];
        firstParagraph = firstParagraph.replaceAll("\\[([^]]*)]\\([^)]*\\)", "$1");
        return firstParagraph.replaceAll("\\s+", " ").trim();
    }

    private String cleanCode(String raw) {
        if (raw == null) return "";
        return raw.stripIndent().trim();
    }
}