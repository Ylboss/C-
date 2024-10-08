package com.example.springbootgraduation.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.springbootgraduation.common.Result;
import com.example.springbootgraduation.entity.Files;
import com.example.springbootgraduation.mapper.FilesMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件上传相关接口
 */
@RestController
@RequestMapping("/file")
public class FilesController {
    @Value("${files.upload.path}")
    private String fileUploadPath;
    @Resource
    FilesMapper filesMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public static final String FILES_KEY = "FILES_FRONT_ALL";

    /**
     * 文件上传接口
     *
     * @param file 前端传递过来的文件
     * @return 返回文件url
     * @throws IOException 异常处理
     */
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file,
                         @RequestParam (required = false)String uploaderName,
                         @RequestParam (required = false)String purpose,
                         @RequestParam(required = false)Integer courseId) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();
        // 定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        String fileUUID = uuid + StrUtil.DOT + type;
        File uploadFile = new File(fileUploadPath + fileUUID);
        // 判断配置的文件目录是否存在，若不存在则创建一个新的文件目录
        File parentFile = uploadFile.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        String url;
        // 获取文件的md5，通过对比md5避免重复上传相同内容的文件
        String md5 = SecureUtil.md5(file.getInputStream());
        // 从数据库查询是否存在相同的记录
        Files dbFiles = getFileByMd5(md5);
        if (dbFiles != null) { // 文件已存在
            url = dbFiles.getUrl();
        } else {
            // 上传文件到磁盘
            file.transferTo(uploadFile);
            // 数据库若不存在重复文件，则不删除刚才上传的文件
            url = "http://localhost:9090/file/" + fileUUID;
        }
        // 存储数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size / 1024);
        saveFile.setUrl(url);
        saveFile.setMd5(md5);
        saveFile.setUploaderName(uploaderName); // 设置上传者的名字
        saveFile.setPurpose(purpose);
        if (courseId != null) {
            saveFile.setCourseId(courseId);
        }
        filesMapper.insert(saveFile);
        flushRedis(FILES_KEY);
        return url;
    }


    /**
     * 文件下载接口
     *
     * @param fileUUID 文件唯一标识符
     * @param response 请求响应
     * @throws IOException 异常处理
     */
    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        // 根据文件的唯一标识码获取文件
        File uploadFile = new File(fileUploadPath + fileUUID);
        // 设置输出流的格式
        ServletOutputStream os = response.getOutputStream();
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");
        // 读取文件的字节流
        os.write(FileUtil.readBytes(uploadFile));
        os.flush();
        os.close();
    }

    /**
     * 通过文件的md5查询文件
     *
     * @param md5
     * @return 返回文件的md5
     */
    private Files getFileByMd5(String md5) {
        // 查询文件的md5是否存在
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = filesMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0);
    }

    /**
     * 分页查询接口
     *
     * @param pageNum  分多少页
     * @param pageSize 每页多少个数据
     * @param name     根据文件名查询记录
     * @return 返回分页查询结果
     */
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name,
                           @RequestParam(defaultValue = "") String type,
                           @RequestParam(defaultValue = "") String uploaderName) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        // 查询未删除的记录
        queryWrapper.eq("is_delete", false);
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        if (!"".equals(uploaderName)) {
            queryWrapper.like("uploader_name", uploaderName);
        }
        if (!"".equals(type)) {
            queryWrapper.like("type",type);
        }
        return Result.success(filesMapper.selectPage(new Page<>(pageNum, pageSize), queryWrapper));
    }
    @GetMapping("/byCourseId")
    // 根据课程ID获取文件列表的方法
    public Result getFilesByCourseId(@RequestParam Integer courseId) {
        // 创建查询条件对象
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        // 设置查询条件为课程ID等于传入的courseId
        queryWrapper.eq("course_id", courseId);
        // 查询数据库，获取符合条件的文件列表
        List<Files> filesList = filesMapper.selectList(queryWrapper);
        // 返回包含文件列表的成功结果
        return Result.success(filesList);
    }
    /**
     * 更新文件信息
     *
     * @param files 待更新的文件
     * @return 返回更新结果
     */
    @PostMapping("/update")
    public Result update(@RequestBody Files files) {
        filesMapper.updateById(files);
        flushRedis(FILES_KEY);
        return Result.success();
    }

    /**
     * 按照文件id删除
     *
     * @param id 待删除的文件id
     * @return 返回删除结果
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        Files files = filesMapper.selectById(id);
        files.setIsDelete(true);
        filesMapper.updateById(files);
        // 刷新 Redis 缓存中的 FILES_KEY 键对应的数值
        flushRedis(FILES_KEY);
        return Result.success();
    }

    /**
     * 按照文件id查询
     *
     * @param id 待查询的文件id
     * @return 返回查询结果
     */
    @GetMapping("/detail/{id}")
    public Result getById(@PathVariable Integer id) {
        return Result.success(filesMapper.selectById(id));
    }

    /**
     * 按照id批量删除文件
     *
     * @param ids 待删除的文件id即可
     * @return 返回删除结果
     */
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", ids);
        List<Files> files = filesMapper.selectList(queryWrapper);
        for (Files file : files) {
            file.setIsDelete(true);
            filesMapper.updateById(file);
        }
        return Result.success();
    }

    /**
     * 删除缓存
     *
     * @param key 要删除的缓存key
     */
    private void flushRedis(String key) {
        stringRedisTemplate.delete(key);
    }

}