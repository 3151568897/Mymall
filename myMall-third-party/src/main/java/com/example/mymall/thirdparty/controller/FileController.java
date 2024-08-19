package com.example.mymall.thirdparty.controller;

import com.example.common.utils.R;
import com.example.mymall.thirdparty.utils.MinioUtil;
import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("thirdParty/file")
public class FileController {

    @Autowired
    private MinioUtil minioUtil;

    private final String bucketName = "mymall";

    @PostMapping("/upload")
    public R upload(MultipartFile file) throws Exception {
        String format = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
        String objectName = format + "/" + UUID.randomUUID().toString()+"."+file.getOriginalFilename().split("\\.")[1];
        minioUtil.uploadFile(bucketName, objectName, file);
        return R.ok().put("data",minioUtil.getObjectURL(bucketName, objectName));
    }
}
