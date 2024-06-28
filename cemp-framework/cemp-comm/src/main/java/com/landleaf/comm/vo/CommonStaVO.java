package com.landleaf.comm.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CommonStaVO 折线图通用对象
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@NoArgsConstructor
@Schema(name = "CommonStaVO对象", description = "CommonStaVO对象")
public class CommonStaVO {

	@Schema(description = "描述")
	private String desc;
	@Schema(description = "y轴数据")
	private List<String> xlist;
	@Schema(description = "x轴数据")
	private List<String> ylist;
	@Schema(description = "x轴数据")
	private List<String> zlist;

	public CommonStaVO(String desc, List<String> xlist, List<String> ylist) {
		this.desc = desc;
		this.xlist = xlist;
		this.ylist = ylist;
	}

	public CommonStaVO(String desc, List<String> xlist, List<String> ylist, List<String> zlist) {
		this.desc = desc;
		this.xlist = xlist;
		this.ylist = ylist;
		this.zlist = zlist;
	}
}
