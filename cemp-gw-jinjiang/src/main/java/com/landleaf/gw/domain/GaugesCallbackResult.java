package com.landleaf.gw.domain;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GaugesCallbackResult {
    /**
     * 识别结果，0成功，其他见对应文档
     */
    private String outputState;

    /**
     * 识别的小数部分，仅在表计为非指针型单区域表计时有效
     */
    private String outputStrDec;

    /**
     * 在表计为非指针型单区域表计时,识别的为整数部分， 在表计为非指针型多区域表计时,各个区域的值，从上到下解析，逗号分割
     */
    private String outputStrInt;

    /**
     * 表计值，仅表计为指针型时有效
     */
    private BigDecimal outputReadingValue;
}
