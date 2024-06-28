package com.landleaf.energy.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.sql.Timestamp;

import java.util.Date;

/**
 * 统计表-设备指标-电表-统计年的新增时的参数封装
 *
 * @author hebin
 * @since 2023-06-25
 */
@Data
@Schema(name = "ProjectStaDeviceElectricityYearAddDTO对象", description = "统计表-设备指标-电表-统计年的新增时的参数封装")
public class ProjectStaDeviceElectricityYearAddDTO {

	/**
	 * id
	 */
		@Schema(description = "id")
				@NotNull(groups = {UpdateGroup.class},message = "id不能为空")
		private Long id;

	/**
	 * 设备ID
	 */
		@Schema(description = "设备ID")
			private String bizDeviceId;

	/**
	 * 产品ID
	 */
		@Schema(description = "产品ID")
			private String bizProductId;

	/**
	 * 品类ID
	 */
		@Schema(description = "品类ID")
			private String bizCategoryId;

	/**
	 * 项目ID
	 */
		@Schema(description = "项目ID")
			private String bizProjectId;

	/**
	 * 项目代码
	 */
		@Schema(description = "项目代码")
			private String projectCode;

	/**
	 * 租户ID
	 */
		@Schema(description = "租户ID")
			private Long tenantId;

	/**
	 * 租户代码
	 */
		@Schema(description = "租户代码")
			private String tenantCode;

	/**
	 * 统计-年
	 */
		@Schema(description = "统计-年")
			private String year;

	/**
	 * 有功用电量
	 */
		@Schema(description = "有功用电量")
			private BigDecimal energymeterEpimportTotal;

	/**
	 * 统计时间
	 */
		@Schema(description = "统计时间")
			private Timestamp staTime;

	public interface AddGroup {
	}

	public interface UpdateGroup {
	}
}
