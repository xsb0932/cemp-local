package com.landleaf.comm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CommonStaVODetail
 *
 * @author xshibai
 * @since 2023-10-08
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "CommonStaVODetail对象", description = "CommonStaVODetail对象")
public class CommonStaVODetail {

	@Schema(description = "xValue")
	private String xValue;
	@Schema(description = "yValue")
	private String yValue;
	@Schema(description = "order")
	private Integer order;


}
