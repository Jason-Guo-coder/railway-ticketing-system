package com.gjq.train.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.TemplateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class CodeGenerator {

    private static final String JDBC_URL = System.getenv().getOrDefault(
            "DB_URL",
            "jdbc:mysql://124.223.55.166:3306/railway-ticketing-system"
                    + "?useSSL=false&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=Asia/Shanghai&characterEncoding=UTF-8"
    );
    private static final String JDBC_USERNAME =
            System.getenv().getOrDefault("DB_USERNAME", "oceana_dev");
    private static final String[] TABLES = {"member"};

    private CodeGenerator() {
    }

    public static void main(String[] args) {
        Path projectRoot = resolveProjectRoot();
        Path javaOutput = projectRoot.resolve("member/src/main/java");
        Path xmlOutput = projectRoot.resolve("member/src/main/resources/mapper");

        DataSourceConfig.Builder dataSource = new DataSourceConfig.Builder(
                JDBC_URL,
                JDBC_USERNAME,
                requireEnvironmentVariable("DB_PASSWORD")
        ).keyWordsHandler(new MySqlKeyWordsHandler());

        FastAutoGenerator.create(dataSource)
                .globalConfig(builder -> builder
                        .author("郭建泉")
                        .disableOpenDir()
                        .outputDir(javaOutput.toString()))
                .packageConfig(builder -> builder
                        .parent("com.gjq.train.member")
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .pathInfo(Collections.singletonMap(
                                OutputFile.xml,
                                xmlOutput.toString()
                        )))
                .strategyConfig(builder -> builder
                        .addInclude(TABLES)
                        .entityBuilder()
                        .disableSerialVersionUID()
                        .enableLombok()
                        .enableTableFieldAnnotation()
                        .enableFileOverride()
                        .mapperBuilder()
                        .enableBaseResultMap()
                        .enableBaseColumnList()
                        .enableFileOverride()
                        .serviceBuilder()
                        .formatServiceFileName("%sService")
                        .formatServiceImplFileName("%sServiceImpl")
                        .enableFileOverride())
                .templateConfig(builder -> builder.disable(
                        TemplateType.CONTROLLER
                ))
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    private static String requireEnvironmentVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "请先配置环境变量 " + name + "，再运行代码生成器"
            );
        }
        return value;
    }

    private static Path resolveProjectRoot() {
        String mavenRoot = System.getProperty("maven.multiModuleProjectDirectory");
        Path current = Path.of(
                mavenRoot == null ? System.getProperty("user.dir") : mavenRoot
        ).toAbsolutePath().normalize();

        if (Files.isRegularFile(current.resolve("member/pom.xml"))) {
            return current;
        }

        Path parent = current.getParent();
        if (parent != null
                && Files.isRegularFile(parent.resolve("member/pom.xml"))) {
            return parent;
        }

        throw new IllegalStateException("无法定位项目根目录");
    }
}
