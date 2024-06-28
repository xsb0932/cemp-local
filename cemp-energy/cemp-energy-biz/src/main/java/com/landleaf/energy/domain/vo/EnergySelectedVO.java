package com.landleaf.energy.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "EnergySelectedVO对象", description = "能耗下拉对象")
@EqualsAndHashCode
public class EnergySelectedVO extends SelectedVO {


    @Schema(description = "属性")
    private String unit;

    public EnergySelectedVO(String label, String value, String property,String unit) {
        super(label,value,property);
        this.unit = unit;
    }

}
