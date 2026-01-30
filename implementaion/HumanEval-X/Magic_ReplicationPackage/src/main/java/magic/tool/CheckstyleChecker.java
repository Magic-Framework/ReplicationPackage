package magic.tool;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.*;
import magic.dto.CodeViolation;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class CheckstyleChecker {

    public List<CodeViolation> checkIssues(String javaCode) throws Exception {
        File tempFile = File.createTempFile("checkstyle_temp_", ".java");

        List<CodeViolation> issues;
        try (InputStream configStream = getClass().getResourceAsStream("/checkstyle/sun_checks.xml")) {
            if (configStream == null) {
                throw new IllegalStateException("Checkstyle config not found in resources!");
            }

            Files.writeString(tempFile.toPath(), javaCode, StandardCharsets.UTF_8);

            // 1. 加载Checkstyle配置文件
            InputSource inputSource = new InputSource(configStream);
            Configuration config = ConfigurationLoader.loadConfiguration(
                    inputSource,
                    new PropertiesExpander(System.getProperties()),
                    ConfigurationLoader.IgnoredModulesOptions.OMIT
            );

            // 2. 创建Checker实例并配置
            Checker checker = new Checker();
            checker.setModuleClassLoader(Checker.class.getClassLoader());
            checker.setLocaleLanguage("en");
            checker.setLocaleCountry("US");

            // 3. 设置自定义监听器以收集检查结果
            CustomAuditListener listener = new CustomAuditListener();
            checker.addListener(listener);

            // 4. 配置Checker模块
            checker.configure(config);

            // 5. 运行Checkstyle检查
            checker.process(Collections.singletonList(tempFile));

            // 6. 从监听器中获取错误结果
            issues = listener.getViolations();

            // 7. 清理Checker资源
            checker.destroy();
        } finally {
            // 删除临时文件
            Files.deleteIfExists(tempFile.toPath());
        }
        return issues;
    }

    /**
     * 自定义审计监听器，用于收集Checkstyle检查中的错误
     */
    private static class CustomAuditListener implements AuditListener {

        List<CodeViolation> violations = new ArrayList<>();

        String[] remove = {"UnnecessaryImport", "RedundantImport", "UnusedImports", "JavadocPackage", "NewlineAtEndOfFile",
                "WhitespaceAround", "WhitespaceAfter", "LineLength", "RegexpSingleline", "JavadocVariable", "FinalParameters", "MagicNumber",
                "NeedBraces", "NoWhitespaceBefore", "LocalVariableName", "OperatorWrap", "AvoidStarImport", "InvalidJavadocPosition",
                "NoWhitespaceAfter", "ParenPad", "MethodParamPad", "ParameterName", "LocalFinalVariableName", "ConstantName", "MethodName",
                "StaticVariableName", "MemberName", "JavadocMethod", "MissingJavadocMethod", "JavadocStyle", "DesignForExtension"};

        List<String> removeList = Arrays.asList(remove);

        @Override
        public void auditStarted(AuditEvent event) {
        }

        @Override
        public void auditFinished(AuditEvent event) {
        }

        @Override
        public void fileStarted(AuditEvent event) {
        }

        @Override
        public void fileFinished(AuditEvent event) {
        }

        @Override
        public void addError(AuditEvent event) {
            Violation violation = event.getViolation();
            String sourceName = violation.getSourceName();
            int lastDotIndex = sourceName.lastIndexOf('.');
            String ruleName;
            if (lastDotIndex != -1) {
                ruleName = sourceName.substring(lastDotIndex + 1);
            } else {
                ruleName = sourceName;
            }
            String suffix = "Check";
            ruleName = ruleName.substring(0, ruleName.length() - suffix.length());
            if (removeList.contains(ruleName)) return;
            String description = violation.getViolation();
            int startLine = violation.getLineNo(), endLine = violation.getLineNo();
            SeverityLevel severityLevel = violation.getSeverityLevel();
            int severity = 0;
            switch (severityLevel) {
                case ERROR -> severity = 1;
                case WARNING -> severity = 2;
                case INFO -> severity = 3;
                case IGNORE -> severity = 4;
            }
            CodeViolation codeViolation = new CodeViolation("checkstyle", startLine, endLine, description, ruleName, severity);
            violations.add(codeViolation);
        }

        @Override
        public void addException(AuditEvent event, Throwable throwable) {
        }

        /**
         * 获取收集到的错误列表
         *
         * @return 错误信息列表
         */
        public List<CodeViolation> getViolations() {
            return violations;
        }
    }
}
