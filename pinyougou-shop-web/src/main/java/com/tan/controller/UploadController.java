package com.tan.controller;

import com.tan.common.FastDFSClient;
import com.tan.vo.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/upload")
@RestController
public class UploadController {

    @PostMapping
    public Result upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")+1);
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastdfs/tracker.conf");
            String fileName = fastDFSClient.uploadFile(file.getBytes(),originalFilename);
            return Result.ok(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.fail("上传文件失败");
    }
}
