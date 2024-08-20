package com.example.springbootgraduation.service.impl;

import com.example.springbootgraduation.entity.Files;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.springbootgraduation.mapper.FilesMapper;
import com.example.springbootgraduation.service.IFilesService;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yl
 * @since 2024-04-17
 */
@Service
public class FilesServiceImpl extends ServiceImpl<FilesMapper, Files> implements IFilesService {

}
