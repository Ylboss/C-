package com.example.springbootgraduation.controller;

import com.example.springbootgraduation.entity.CompileRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;

@RestController
@RequestMapping("/api")
public class CompileController {

    @PostMapping("/compile")
    public String compileCode(@RequestBody CompileRequest request) throws IOException, InterruptedException {
        String code = request.getCode(); // 获取请求中的代码

        System.out.println("===开始写入临时文件==="); // 打印提示信息，表示开始写入临时文件

        // 写入临时文件
        File tempFile = new File("temp.c"); // 创建一个临时文件对象
        Files.write(tempFile.toPath(), code.getBytes()); // 将代码写入临时文件

        // 使用GCC编译
        ProcessBuilder pb = new ProcessBuilder("gcc", "-o", "temp", "temp.c"); // 创建一个进程构建器，用于执行GCC编译命令
        Process process = pb.start(); // 启动编译进程
        process.waitFor(); // 等待编译进程执行完毕

        // 执行编译后的程序并获取输出
        pb = new ProcessBuilder("./temp"); // 创建一个进程构建器，用于执行编译后的程序
        Process execProcess = pb.start(); // 启动执行进程
        BufferedReader reader = new BufferedReader(new InputStreamReader(execProcess.getInputStream())); // 创建一个缓冲读取器，用于读取执行进程的输出流
        StringBuilder output = new StringBuilder(); // 创建一个字符串构建器，用于存储执行结果
        String line;
        while ((line = reader.readLine()) != null) { // 逐行读取执行结果
            output.append(line).append("\n"); // 将每行结果添加到字符串构建器中
        }
        execProcess.waitFor(); // 等待执行进程执行完毕

        // 清理临时文件
        tempFile.delete(); // 删除临时文件
        new File("temp").delete(); // 删除编译后的程序文件
        System.out.println("===清理临时文件完毕==="); // 打印提示信息，表示清理临时文件完毕


        // 处理输出结果，确保字符之间没有其他转义符或特殊字符
        String cleanOutput = output.toString().trim(); // 去除两端空格
        cleanOutput = cleanOutput.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", ""); // 去除控制字符，保留换行符和制表符
        cleanOutput = cleanOutput.replaceAll("[^\\x20-\\x7E]", ""); // 去除非ASCII可见字符

        return cleanOutput; // 返回处理后的干净字符串
    }
}