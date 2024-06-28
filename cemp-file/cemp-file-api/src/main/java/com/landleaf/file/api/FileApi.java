package com.landleaf.file.api;

import com.landleaf.comm.base.pojo.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import static com.landleaf.file.enums.ApiConstants.NAME;
import static com.landleaf.file.enums.ApiConstants.PREFIX;


@FeignClient(name = NAME)
@Tag(name = "RPC 服务 - 天气")
public interface FileApi {

    @PostMapping(value = PREFIX + "/avue/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "AVue资源文件上传")
    Response<String> aVueUpload(@RequestPart("file") MultipartFile file);

    @GetMapping(value = PREFIX + "/download")
    @Operation(summary = "文件下载")
    ResponseEntity<byte[]> downloadFile(@RequestParam("bucketName") String bucketName, @RequestParam("objectPath") String objectPath);
}
