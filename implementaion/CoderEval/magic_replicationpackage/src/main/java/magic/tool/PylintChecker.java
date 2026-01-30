package magic.tool;

import magic.dto.CodeViolation;
import magic.util.CommandUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PylintChecker {

    private static final String PYLINT_PATH = "/usr/local/bin/pylint";

    public List<CodeViolation> checkIssues(String pythonCode) {
        ProcessBuilder processBuilder = new ProcessBuilder(PYLINT_PATH, "--from-stdin", "stdin_module.py");
        List<String> rawOutput = CommandUtils.executeCommand(processBuilder, pythonCode);
        return parsePylintOutput(rawOutput);
    }

    private List<CodeViolation> parsePylintOutput(List<String> outputLines) {
        List<CodeViolation> violations = new ArrayList<>();
        Pattern pattern = Pattern.compile("^(.*?):(\\d+):(\\d+): ([CRWEFI]\\d+): (.*?) \\((.*?)\\)$");

        String[] remove = {
                "C0301", "C0305", "C0303", "C0304", "C0103", "C0112", "C0114", "C0115", "C0116",
                "C0410", "C0411", "C0412", "C0413", "C0414", "C0415", "R0903", "W0611", "W0401",
                "E0602", "W0621", "W0212", "W0404", "E1101", "W0201", "E1133"
        };

        List<String> removeList = Arrays.asList(remove);

        for (String line : outputLines) {
            if (line.startsWith("*************") || line.startsWith("-------------") ||
                    line.startsWith("Your code has been rated") || line.startsWith("ERROR:")) {
                if (line.startsWith("ERROR:")) {
                    System.err.println("Pylint 报告错误或超时: " + line);
                }
                continue;
            }

            Matcher matcher = pattern.matcher(line);
            if (matcher.matches()) {
                try {
                    int startLine = Integer.parseInt(matcher.group(2)), endLine = Integer.parseInt(matcher.group(2));
                    String ruleName = matcher.group(4);
                    if (removeList.contains(ruleName)) continue;
                    String description = matcher.group(5).trim();
                    int severity = CommandUtils.getSeverityFromRule(ruleName);
                    violations.add(new CodeViolation("pylint", startLine, endLine, description, ruleName, severity));
                } catch (NumberFormatException e) {
                    System.err.println("解析 Pylint 输出行时数字格式错误: " + line);
                }
            } else {
                System.out.println("Pylint: 无法解析的行: " + line);
            }
        }
        return violations;
    }
}