package com.landleaf.monitor;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class AvueTest {
    public static void main(String[] args) {
        String body = "{\"visual\":{\"password\":\"\",\"category\":\"5\",\"title\":\"test1355\"},\"config\":{\"detail\":\"{\\\"width\\\":\\\"1920\\\",\\\"height\\\":\\\"1080\\\",\\\"name\\\":\\\"test1355\\\"}\"}}";
        HttpResponse response = HttpUtil.createPost("http://139.196.110.170:4001/blade-visual/visual/save")
                .body(body)
                .execute();
        if (response.isOk()) {
            JSONObject jsonObject = JSONUtil.parseObj(response.body());
            if (jsonObject.getBool("success")) {
                String id = jsonObject.getJSONObject("data").getStr("id");
                System.out.println("http://139.196.110.170:4002/build/" + id);
                System.out.println("http://139.196.110.170:4002/view/" + id);
            }
        }
    }
}
