package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * ClassName: CommonController
 * Package: com.sky.controller.admin
 * Description:
 *
 * @Author Abola
 * @Create 2025/4/16 11:07
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/common")
@Api("通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) throws IOException {
        log.info("文件上传：{}",file);
        try {
            String originalFilename = file.getOriginalFilename();
            int index = originalFilename.lastIndexOf(".");
            String suffix = originalFilename.substring(index);
            String objectName = UUID.randomUUID().toString() + suffix;

            // 调用阿里云OSS上传方法
            String accessUrl = aliOssUtil.upload(file.getBytes(), objectName);

            return Result.success(accessUrl);
        } catch (IOException e) {
            log.error("文件上传失败：{}", e.getMessage());
            return Result.error(MessageConstant.UPLOAD_FAILED);
        }
    }
}
