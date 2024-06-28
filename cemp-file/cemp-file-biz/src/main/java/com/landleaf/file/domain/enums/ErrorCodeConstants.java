package com.landleaf.file.domain.enums;


import com.landleaf.comm.exception.ErrorCode;

/**
 * file 错误码枚举类
 * <p>
 * file 错误码区间 [1-004-000-000 ~ 1-005-000-000)
 */
public interface ErrorCodeConstants {

    // ========== 文件下载 模块 1003002000 ==========
    ErrorCode FILE_DOWNLOAD_FAIL = new ErrorCode("1004000000", "文件下载失败");
    ErrorCode FILE_UPLOAD_FAIL = new ErrorCode("1004000001", "文件上传失败");
    ErrorCode NOT_PERMISSION = new ErrorCode("1004000002", "没有文件权限");

}
