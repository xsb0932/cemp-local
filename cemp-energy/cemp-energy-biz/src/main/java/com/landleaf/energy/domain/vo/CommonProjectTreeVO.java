package com.landleaf.energy.domain.vo;

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
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CommonProjectTreeVO对象", description = "项目树对象")
public class CommonProjectTreeVO {

	@Schema(description = "id")
	private String id;
	@Schema(description = "名称")
	private String name;
	@Schema(description = "是否选中")
	private boolean selectable;
	@Schema(description = "子项")
	private List<CommonProjectTreeVO> children;

}
