package com.landleaf.file.service.impl;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.net.URLDecoder;
import cn.hutool.core.util.IdUtil;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.file.domain.enums.StoreFileType;
import com.landleaf.file.minio.properties.MinioProperties;
import com.landleaf.file.service.FileService;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.dto.TenantInfoResponse;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * FileServiceImpl
 *
 * @author 张力方
 * @since 2023/6/12
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class FileServiceImpl implements FileService {

    private final TenantApi tenantApi;
    private final MinioClient minioClient;
    private final MinioProperties minioProperties;

    /**
     * 存储文件
     *
     * @param file     文件
     * @param fileType 文件类型
     * @return 文件 url
     */
    @Override
    public String storeFile(MultipartFile file, StoreFileType fileType) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Long loginTenantId = LoginUserUtil.getLoginTenantId();
        Response<TenantInfoResponse> tenantInfo = tenantApi.getTenantInfo(loginTenantId);
        TenantInfoResponse result = tenantInfo.getResult();
        String bizTenantId = result.getBizTenantId();
        // 企业业务id作为桶名称
        // 存储桶不存在则创建
        // 存储桶名称长度必须至少为3且不超过63个字符。
        // 存储桶名称不得包含大写字符或下划线。
        // 存储桶名称必须以小写字母或数字开头。
        String bucketName = bizTenantId.toLowerCase();
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            log.info("{},存储桶创建成功", bucketName);
        } else {
            log.info("{},存储桶已存在", bucketName);
        }
        return storeFile(file, bucketName, fileType);
    }

    /**
     * 存储文件
     *
     * @param file       文件
     * @param bucketName 桶
     * @param fileType   文件类型
     * @return 文件 url
     */
    @Override
    public String storeFile(MultipartFile file, String bucketName, StoreFileType fileType) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String fileName = file.getOriginalFilename();
        InputStream stream = file.getInputStream();
        return storeFile(stream, bucketName, fileName, fileType);

    }

    /**
     * 存储文件
     *
     * @param stream     文件流
     * @param fileName   文件名
     * @param bucketName 桶
     * @param fileType   文件类型
     * @return 文件 url
     */
    @Override
    public String storeFile(InputStream stream, String bucketName, String fileName, StoreFileType fileType) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (Objects.isNull(stream)) {
            return null;
        }

        String path = fileType.getPath();

        String[] split = fileName.split("\\.");
        String fileSuffix = split.length > 1 ? "." + split[split.length - 1] : "";

        String finalPathWithFileName = path + "/" + split[0] + "-" + UUID.randomUUID().toString().split("-")[0] + fileSuffix;

        // 开始上传
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(finalPathWithFileName)
                .stream(stream, stream.available(), -1)
                .build());

        return minioProperties.getDownloadUrl() + "/" + bucketName + "?objectPath=" + finalPathWithFileName;

    }

    /**
     * 文件下载
     *
     * @param bucketName 桶名
     * @param objectPath 对象路径
     * @return 文件
     */
    @Override
    public byte[] download(String bucketName, String objectPath) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectResponse object = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectPath)
                .build());
        return IoUtil.readBytes(object);
    }

    @SneakyThrows
    @Override
    public String storeAVueFile(MultipartFile file) {
        String fileName = URLDecoder.decode(file.getOriginalFilename(), UTF_8);
        String fileSuffix;
        if (null != fileName && fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            fileSuffix = "." + fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            fileSuffix = "";
        }
        InputStream ins = file.getInputStream();
        String filename = IdUtil.fastSimpleUUID() + fileSuffix;
        minioClient.putObject(PutObjectArgs.builder()
                .bucket("avue")
                .object(filename)
                .stream(ins, ins.available(), -1)
                .build());
        return "avue/" + filename;
    }
}
