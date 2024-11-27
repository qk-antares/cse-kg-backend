package com.antares.kg.utils;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class MinioUtil {
    @Resource
    private MinioClient minioClient;

    /**
     * 将字符串以txt文件形式上传到指定桶和目录
     *
     * @param bucketName  桶名称
     * @param directory   目录名称（如 test/）
     * @param fileName    文件名称（如 hello.txt）
     * @param content     要写入的字符串
     * @return            上传结果
     */
    public Boolean uploadStringAsTxt(String bucketName, String directory, String fileName, String content) {
        try {
            // 拼接对象名称（目录 + 文件名）
            String objectName = directory + fileName;

            // 将字符串转为字节流
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(contentBytes);

            // 上传文件到桶
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, contentBytes.length, -1)
                            .contentType("text/plain") // 设置文件类型
                            .build()
            );

            return true;
        } catch (Exception e) {
            log.error("上传 {} 至 {}/{} 失败: {}", bucketName, directory, fileName, e.getMessage());
            return false;
        }
    }
}
