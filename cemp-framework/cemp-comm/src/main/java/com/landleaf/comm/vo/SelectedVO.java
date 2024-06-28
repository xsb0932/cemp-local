package com.landleaf.comm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Schema(name = "SelectedVO对象", description = "SelectedVO对象")
@EqualsAndHashCode
public class SelectedVO implements Serializable {


    private static final long serialVersionUID = -7203209756333585234L;
    @Schema(description = "值")
    private String value;

    @Schema(description = "显示的值")
    @EqualsAndHashCode.Exclude
    private String label;

    @Schema(description = "属性")
    private String property;

    public SelectedVO(String label, String value) {
        this.value = value;
        this.label = label;
    }

    public SelectedVO(String label, String value, String property) {
        this.value = value;
        this.label = label;
        this.property = property;
    }

}
