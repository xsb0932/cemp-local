package com.landleaf.comm.base.pojo;

import lombok.Data;

/**
 * UploadResult
 *
 * @author 张力方
 * @since 2022/7/5
 **/
@Data
public class UploadResult {
    private Integer errno;
    private UploadResultData data;
    private String message;
}
