package com.landleaf.monitor.domain.response;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AVueUploadResponse {
    private int code;
    private String msg;
    private Map<String, Object> data;

    public static AVueUploadResponse success(String host, String filePath, String filename) {
        AVueUploadResponse result = new AVueUploadResponse();
        result.setCode(200).setMsg("操作成功");
        HashMap<String, Object> data = new HashMap<>(5);
        data.put("attachId", -1);
        data.put("domain", host);
        data.put("link", host + "/" + filePath);
        data.put("name", filePath);
        data.put("originalName", filename);
        result.setData(data);
        return result;
    }
}
