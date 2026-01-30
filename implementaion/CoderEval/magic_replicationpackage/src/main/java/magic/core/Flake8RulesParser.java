package magic.core;

import com.vladsch.flexmark.ast.FencedCodeBlock;
import com.vladsch.flexmark.ast.Heading;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ext.yaml.front.matter.AbstractYamlFrontMatterVisitor;
import com.vladsch.flexmark.ext.yaml.front.matter.YamlFrontMatterExtension;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeVisitor;
import com.vladsch.flexmark.util.ast.TextCollectingVisitor;
import com.vladsch.flexmark.util.ast.VisitHandler;
import com.vladsch.flexmark.util.data.MutableDataSet;
import magic.dto.Flake8Rule;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Flake8RulesParser {

    private static final String RESOURCE_PATH = "flake8rules";
    private static final Parser PARSER;

    static {
        MutableDataSet options = new MutableDataSet();
        options.set(Parser.EXTENSIONS, List.of(YamlFrontMatterExtension.create()));
        PARSER = Parser.builder(options).build();
    }

    public static void main(String[] args) {
        Flake8RulesParser parser = new Flake8RulesParser();
        Flake8Rule rule = parser.extractRule("F841");
        System.out.println(rule);
    }

    public Flake8Rule extractRule(String ruleID) {
        String filePath = RESOURCE_PATH + "/" + ruleID + ".md";
        try (InputStream is = getClassLoader().getResourceAsStream(filePath)) {
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            return parseMarkdown(content);
        } catch (IOException e) {
            return null;
        }
    }

    private Flake8Rule parseMarkdown(String markdown) {
        Node document = PARSER.parse(markdown);
        Flake8Rule rule = new Flake8Rule();
        AbstractYamlFrontMatterVisitor yamlVisitor = new AbstractYamlFrontMatterVisitor();
        yamlVisitor.visit(document);
        Map<String, List<String>> data = yamlVisitor.getData();
        rule.setId((data.containsKey("code") && !data.get("code").isEmpty()) ? data.get("code").get(0) : null);
        String msg = (data.containsKey("message") && !data.get("message").isEmpty()) ? data.get("message").get(0) : null;
        rule.setName(msg != null ? msg.replaceAll("^['\"]|['\"]$", "") : null);
        new RuleContentVisitor(rule).collect(document);
        return rule;
    }

    private ClassLoader getClassLoader() {
        return Optional.ofNullable(Thread.currentThread().getContextClassLoader()).orElse(getClass().getClassLoader());
    }

    private static class RuleContentVisitor {
        private final Flake8Rule rule;
        private final TextCollectingVisitor textCollector = new TextCollectingVisitor();
        private final StringBuilder descBuilder = new StringBuilder();
        private String currentSection = "DESCRIPTION";

        private final NodeVisitor visitor = new NodeVisitor(
                new VisitHandler<>(Heading.class, this::visitHeading),
                new VisitHandler<>(FencedCodeBlock.class, this::visitCode),
                new VisitHandler<>(Paragraph.class, this::visitParagraph)
        );

        RuleContentVisitor(Flake8Rule rule) {
            this.rule = rule;
        }

        void collect(Node document) {
            visitor.visit(document);
            rule.setDescription(descBuilder.toString().trim());
        }

        private void visitHeading(Heading node) {
            String title = textCollector.collectAndGetText(node).trim().toLowerCase();
            if (title.contains("anti-pattern") && rule.getAntiPattern() == null) currentSection = "ANTI";
            else if (title.contains("best practice") && rule.getBestPractice() == null) currentSection = "BEST";
            else currentSection = "OTHER";
        }

        private void visitCode(FencedCodeBlock node) {
            String code = node.getContentChars().toString().trim();
            switch (currentSection) {
                case "ANTI" -> {
                    rule.setAntiPattern(code);
                    currentSection = "OTHER";
                }
                case "BEST" -> {
                    rule.setBestPractice(code);
                    currentSection = "OTHER";
                }
            }
        }

        private void visitParagraph(Paragraph node) {
            if ("DESCRIPTION".equals(currentSection)) {
                String text = textCollector.collectAndGetText(node).trim();
                if (!text.isEmpty()) {
                    if (!descBuilder.isEmpty()) descBuilder.append("\n");
                    descBuilder.append(text);
                }
            }
        }
    }
}