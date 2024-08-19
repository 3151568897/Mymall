package com.example.mymall.thirdparty.utils;

import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;


    /**
     * 从给定输入流中传输对象并放入bucket
     * */
    public ObjectWriteResponse putObject(String bucketName, String objectName, InputStream stream, long objectSize, String contentType) throws Exception {
        if (StringUtils.isEmpty(bucketName)) {
            throw new RuntimeException("保存的bucketName为空");
        }
        //long objSize = -1;
        long partSize = -1; //objectSize已知，partSize设为-1意为自动设置
        PutObjectArgs putArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(stream, stream.available(), -1)
                .contentType(contentType)
                .build();
        ObjectWriteResponse response = minioClient.putObject(putArgs);

        return response;
    }

    /**
     * 从bucket获取指定对象的输入流，后续可使用输入流读取对象
     * getObject与minio server连接默认保持5分钟，
     * 每隔15s由minio server向客户端发送keep-alive check，5分钟后由客户端主动发起关闭连接
     * */
    public InputStream getObject(String bucketName, String objectName) throws Exception{
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        return minioClient.getObject(args);
    }

    /**
     * 获取对象的临时访问url，有效期5分钟
     * */
    public String getObjectURL(String bucketName, String objectName) throws Exception{
        return minioClient.getObjectUrl(bucketName, objectName);
    }

    /**
     * 删除对象
     * */
    public void removeObject(String bucketName, String objectName) throws Exception {
        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build();
        minioClient.removeObject(args);
        log.info("bucket:{}文件{}已删除", bucketName , objectName);
    }

    /**
     * 上传MultipartFile
     * @param bucketName 文件存放的bucket
     * @param filePath 文件在bucket里的全目录
     * */
    public ObjectWriteResponse uploadFile(String bucketName, String filePath, MultipartFile file) throws Exception{
        return putObject(bucketName, filePath, file.getInputStream(), file.getSize(), file.getContentType());
    }

    /**
     * 从minio下载文件，直接通过response传输
     * */
    public void downloadFile(String bucketName, String filePath, HttpServletResponse response) throws Exception {

        try (InputStream is = getObject(bucketName, filePath);
             BufferedInputStream bis = new BufferedInputStream(is);
             OutputStream os = response.getOutputStream()) {
            //try {
			/*InputStream is = getObject(bucketName, filePath);
			BufferedInputStream bis = new BufferedInputStream(is);
			OutputStream os = response.getOutputStream();*/
            response.setContentType("application/force-download;charset=utf-8");// 设置强制下载而不是直接打开
            response.addHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(filePath, "UTF-8"));// 设置文件名


            byte[] buffer = new byte[1024*1024*1024]; //buffer 1M
            int offset = bis.read(buffer);
            while (offset != -1) {
                os.write(buffer, 0, offset);
                offset = bis.read(buffer);
            }
            os.flush();
        } catch (Exception e) {
            log.error("下载文件失败"+e.getMessage(), e);
            throw new RuntimeException("下载文件失败" , e);
        }
    }
}
