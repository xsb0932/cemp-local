package com.landleaf.jjgj.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DeviceCategoryKpiConfigEntity对象的新增时的参数封装
 *
 * @author hebin
 * @since 2023-08-01
 */
@Data
@Schema(name = "DeviceCategoryKpiConfigAddDTO对象", description = "DeviceCategoryKpiConfigEntity对象的新增时的参数封装")
public class DeviceCategoryKpiConfigAddDTO {

	/**
	 * 指标ID
	 */
		@Schema(description = "指标ID")
				@NotNull(groups = {UpdateGroup.class},message = "指标ID不能为空")
		private Long id;

	/**
	 * 指标代码
	 */
		@Schema(description = "指标代码")
			private String code;

	/**
	 * 指标名称
	 */
		@Schema(description = "指标名称")
			private String name;

	/**
	 * 品类id
	 */
		@Schema(description = "品类id")
			private String bizCategoryId;

	/**
	 * 品类名称
	 */
		@Schema(description = "品类名称")
			private String categoryName;

	/**
	 * 分项大类代码
	 */
		@Schema(description = "分项大类代码")
			private String kpiTypeCode;

	/**
	 * 分项大类 水 气 电 ...
	 */
		@Schema(description = "分项大类 水 气 电 ...")
			private String kpiType;

	/**
	 * 统计间隔-小时-1:是 0 否
	 */
		@Schema(description = "统计间隔-小时-1:是 0 否")
			private Integer staIntervalHour;

	/**
	 * 统计间隔-日月年-1:是 0 否
	 */
		@Schema(description = "统计间隔-日月年-1:是 0 否")
			private Integer staIntervalYmd;

	/**
	 * 单位
	 */
		@Schema(description = "单位")
			private String unit;

	public interface AddGroup {
	}

	public interface UpdateGroup {
	}
}
