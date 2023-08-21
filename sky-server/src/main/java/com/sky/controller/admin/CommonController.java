package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.properties.MinioProperties;
import com.sky.result.Result;
import com.sky.utils.MinioUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {
    @Autowired
    private MinioUtil minioUtil;
    @Autowired
    private MinioProperties minioProperties;

    @PostMapping("/upload")//文件上传
    public Result<String> upload(MultipartFile file){
        log.info("文件上传：{}",file);
        List<String> upload = minioUtil.upload(new MultipartFile[]{file});
        if(!upload.isEmpty()){
            String path=minioProperties.getEndpoint()+"/"
                    +minioProperties.getBucketName()+"/"+upload.get(0);
            log.info("返回的路径:{}",path);
            return Result.success(path);
        }
        else return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
