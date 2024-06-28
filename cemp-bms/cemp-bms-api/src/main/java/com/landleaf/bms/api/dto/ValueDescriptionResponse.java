package com.landleaf.bms.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValueDescriptionResponse {

    /**
     * key值
     */
    private String key;

    /**
     * value值
     */
    private String value;
}
