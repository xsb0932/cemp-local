package com.landleaf.jjgj.domain.vo;

import com.landleaf.comm.vo.SelectedVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
