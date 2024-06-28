package com.landleaf.bms.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AddressResponse {
    @Schema(description = "id")
    private Long id;

    @Schema(description = "编码")
    private String addressCode;

    @Schema(description = "名称")
    private String addressName;

    @Schema(description = "类型:1->省;2->市;3->区;")
    private Integer addressType;

    @Schema(description = "父编码")
    private String parentCode;

    @Schema(description = "对应气象局中相应区域code")
    private String weatherCode;

    @Schema(description = "对应气象局中相应区域name")
    private String weatherName;

    @Schema(description = "地址信息的子集")
    private List<AddressResponse> children;

    public void addChildren(AddressResponse address) {
        if (null == children) {
            children = new ArrayList<AddressResponse>();
        }
        children.add(address);
    }
}
