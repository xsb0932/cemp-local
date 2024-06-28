package com.landleaf.lh.domain.enums;

import cn.hutool.core.util.StrUtil;
import com.landleaf.lh.domain.response.MaintenanceTypeListResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum MaintenanceTypeEnum {
    ONE("01", "温度"),
    TWO("02", "湿度"),
    THREE("03", "温湿度"),
    FOUR("04", "新风相关"),
    FIVE("05", "热水相关"),
    SIX("06", "异味"),
    SEVEN("07", "噪音"),
    EIGHT("08", "结露发霉"),
    NINE("09", "漏水"),
    TEN("10", "测灯位、毛细"),
    ELEVEN("11", "开关系统、热水"),
    TWELVE("12", "安装、调整、维修"),
    THIRTEEN("13", "大屏、控制面板故障"),
    FOURTEEN("14", "保养"),
    FIFTEEN("15", "恢复（户式）"),
    SIXTEEN("16", "系统解释"),
    SEVENTEEN("17", "换滤网（自由方舟）"),
    ;

    private final String code;
    private final String name;

    public static String codeToName(String code) {
        for (MaintenanceTypeEnum value : values()) {
            if (StrUtil.equals(code, value.getCode())) {
                return value.name;
            }
        }
        return null;
    }

    public static String nameToCode(String name) {
        for (MaintenanceTypeEnum value : values()) {
            if (StrUtil.equals(name, value.getName())) {
                return value.code;
            }
        }
        return null;
    }

    public static List<MaintenanceTypeListResponse> toTypeList() {
        return Arrays.stream(values()).map(o -> new MaintenanceTypeListResponse(o.getCode(), o.getName())).toList();
    }

    public static boolean typeExists(String code) {
        return Arrays.stream(values()).anyMatch(o -> StrUtil.equals(o.getCode(), code));
    }
}
