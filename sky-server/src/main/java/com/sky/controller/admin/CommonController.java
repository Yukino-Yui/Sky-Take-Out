package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@Slf4j
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation("文件上传")
    //前端提交过来的类型是file，所以要指定用MultipartFile file来接收，且参数名必须要和前端提交的保持一致，都为file
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传：{}", file);
        //上传文件有可能文件名相同导致覆盖，通过uuid，来对上传的文件重命名
        try {
            //获取原始文件名
            String originalFilename = file.getOriginalFilename();
            //动态截取原始文件名后缀(从最后一个.开始截取)
            String extention = originalFilename.substring(originalFilename.lastIndexOf("."));
            //构造新文件名称
            String objectName = UUID.randomUUID().toString() + extention;

            //调用aliossUtil upload方法返回文件的请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("文件上传失败" + e);
        }
        //要习惯这种开发思想，用设置的常量，而不是固定文字和固定值
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}

