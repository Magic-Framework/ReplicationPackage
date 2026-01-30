package magic.tool;

import magic.dto.CodeViolation;
import magic.util.CommandUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Flake8Checker {

    private static final String FLAKE8_PATH = "/usr/local/bin/flake8";

    public List<CodeViolation> checkIssues(String pythonCode) {
        ProcessBuilder processBuilder = new ProcessBuilder(FLAKE8_PATH, "-");
        List<String> rawOutput = CommandUtils.executeCommand(processBuilder, pythonCode);
        return parseFlake8Output(rawOutput);
    }

    private List<CodeViolation> parseFlake8Output(List<String> outputLines) {
        List<CodeViolation> violations = new ArrayList<>();
        Pattern pattern = Pattern.compile("^(.*?):(\\d+):(\\d+): ([A-Z]\\d+) (.*)$");

        String[] remove = {
                "F821", "W292", "E265", "E231", "W293", "E261", "E501", "E262", "W291", "E225", "F401",
                "E227", "E302", "E303", "E305", "E211", "E228", "E275", "E402", "E201", "E203", "F811"
        };

        List<String> removeList = Arrays.asList(remove);

        for (String line : outputLines) {
            if (line.startsWith("ERROR:")) {
                System.err.println("Flake8 报告错误或超时: " + line);
                continue;
            }
            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                try {
                    int startLine = Integer.parseInt(matcher.group(2)), endLine = Integer.parseInt(matcher.group(2));
                    String ruleName = matcher.group(4);
                    if (removeList.contains(ruleName)) continue;
                    String description = matcher.group(5);
                    int severity = CommandUtils.getSeverityFromRule(ruleName);
                    violations.add(new CodeViolation("flake8", startLine, endLine, description, ruleName, severity));
                } catch (NumberFormatException e) {
                    System.err.println("解析 Flake8 输出行时数字格式错误: " + line);
                }
            } else {
                // 如果有无法匹配的行，可能是非错误输出，或者格式不符
                // System.out.println("Flake8: 无法解析的行: " + line);
            }
        }
        return violations;
    }
}