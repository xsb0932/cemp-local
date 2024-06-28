package com.landleaf.api.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("test")
public class Test {

    private Long id;

    private String name;

    private Integer age;
}
