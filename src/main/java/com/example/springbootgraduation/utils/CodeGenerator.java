package com.example.springbootgraduation.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;


import java.util.Collections;



//mp代码生成器
public class CodeGenerator {
    private static String url = "jdbc:mysql://localhost:3306/test?serverTimezone=GMT%2B8";

    private static String username = "root";

    private static String password = "LXY12345";

    private static void generator() {
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("yl") // 设置作者
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("C:\\Users\\yl\\Desktop\\bs\\springbootGraduation\\src\\main\\java\\"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("com.example.springbootgraduation") // 设置父包名
                            .moduleName(null) // 设置父包模块名 无

                            .pathInfo(Collections.singletonMap(OutputFile.mapperXml, "C:\\Users\\yl\\Desktop\\bs\\springbootGraduation\\src\\main\\resources\\mapper\\")); // 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok();
                    builder.mapperBuilder().enableMapperAnnotation().build();
                    builder.controllerBuilder().enableHyphenStyle()  // 开启驼峰转连字符
                            .enableRestStyle(); // 开启生成@RestController 控制器
                    builder.addInclude("file_course") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                })
//                .templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .execute();

    }

    public static void main(String[] args) {
        generator();
    }
}

