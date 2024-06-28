package com.landleaf.file.service;

import com.landleaf.file.domain.enums.StoreFileType;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * 文件上传服务
 *
 * @author 张力方
 * @since 2023/6/12
 **/
public interface FileService {
    /**
     * 存储文件
     *
     * @param file     文件
     * @param fileType 文件类型
     * @return 文件 url
     */
    String storeFile(MultipartFile file, StoreFileType fileType) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**
     * 存储文件
     *
     * @param file       文件
     * @param bucketName 桶
     * @param fileType   文件类型
     * @return 文件 url
     */
    String storeFile(MultipartFile file, String bucketName, StoreFileType fileType) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**
     * 存储文件
     *
     * @param stream     文件流
     * @param fileName   文件名
     * @param bucketName 桶
     * @param fileType   文件类型
     * @return 文件 url
     */
    String storeFile(InputStream stream, String bucketName, String fileName, StoreFileType fileType) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    /**
     * 文件下载
     *
     * @param bucketName 桶名
     * @param objectPath 对象路径
     * @return 文件
     */
    byte[] download(String bucketName, String objectPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    String storeAVueFile(MultipartFile file);
}
