package com.example.demo.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import com.baomidou.mybatisplus.annotation.FieldFill;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * MyBatis-Plus 代码生成器
 * 参考：https://baomidou.com/guides/new-code-generator/
 *
 * 直接运行 main 方法即可生成代码。
 */
public class CodeGenerator {

    private static final String URL = "jdbc:mysql://localhost:3306/demo"
            + "?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai"
            + "&remarks=true&useInformationSchema=true&tinyInt1isBit=true";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    private static final String AUTHOR = "demo";
    private static final String PARENT_PACKAGE = "com.example.demo";

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");

        FastAutoGenerator.create(URL, USERNAME, PASSWORD)
                .globalConfig(builder -> builder
                        .author(AUTHOR)
                        .enableSpringdoc() // 生成 OpenAPI 3 注解，配合 springdoc-openapi
                        .commentDate("yyyy-MM-dd")
                        .outputDir(Paths.get(projectPath, "src", "main", "java").toString())
                )
                .packageConfig(builder -> builder
                        .parent(PARENT_PACKAGE)
                        .entity("entity")
                        .mapper("mapper")
                        .service("service")
                        .serviceImpl("service.impl")
                        .pathInfo(Collections.singletonMap(
                                OutputFile.xml,
                                Paths.get(projectPath, "src", "main", "resources", "mapper").toString()
                        ))
                )
                .strategyConfig((scanner, builder) -> builder
                        .addInclude(getTables(scanner.apply("请输入表名，多个英文逗号分隔，输入 all 生成全部表：")))
                        .addTablePrefix("t_", "c_")
                        .entityBuilder()
                        .enableLombok()
                        .addTableFills(
                                new Column("create_time", FieldFill.INSERT),
                                new Column("update_time", FieldFill.INSERT_UPDATE)
                        )
                        .build()
                )
                .templateEngine(new VelocityTemplateEngine())
                .execute();
    }

    /**
     * 处理 all 情况：空列表表示生成全部表
     */
    protected static List<String> getTables(String tables) {
        return "all".equalsIgnoreCase(tables.trim())
                ? Collections.emptyList()
                : Arrays.asList(tables.split(","));
    }
}
