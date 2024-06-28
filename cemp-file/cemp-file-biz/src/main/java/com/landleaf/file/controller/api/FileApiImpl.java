package com.landleaf.file.controller.api;

import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.file.api.FileApi;
import com.landleaf.file.domain.enums.ErrorCodeConstants;
import com.landleaf.file.domain.enums.StoreFileType;
import com.landleaf.file.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class FileApiImpl implements FileApi {
    private final FileService fileService;

    @Override
    public Response<String> aVueUpload(MultipartFile file) {
        try {
            String filePath = fileService.storeAVueFile(file);
            return Response.success(filePath);
        } catch (Exception e) {
            log.error("AVue file store error", e);
            throw new ServiceException(ErrorCodeConstants.FILE_UPLOAD_FAIL);
        }
    }

    @Override
    public ResponseEntity<byte[]> downloadFile(String bucketName, String objectPath) {
        try {
            // 读取内容
            byte[] content = fileService.download(bucketName, objectPath);
            HttpHeaders headers = new HttpHeaders();
            if (objectPath.contains(StoreFileType.IMAGE.getPath())) {
                headers.setContentType(MediaType.IMAGE_JPEG);
            }
            if (content == null) {
                log.warn("[下载文件][path({}) 文件不存在]", objectPath);
                return new ResponseEntity<>(null, headers, HttpStatus.NOT_FOUND);
            }
            headers.setContentLength(content.length);
            return new ResponseEntity<>(content, headers, HttpStatus.OK);
        } catch (Exception e) {
            throw new ServiceException(ErrorCodeConstants.FILE_DOWNLOAD_FAIL);
        }
    }
}
