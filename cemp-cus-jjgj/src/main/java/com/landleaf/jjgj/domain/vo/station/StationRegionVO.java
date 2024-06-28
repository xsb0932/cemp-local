package com.landleaf.jjgj.domain.vo.station;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * StationRegionVO 站点系统图-区域VO
 *
 * @author xshibai
 * @since 2023-06-20
 */
@Data
@AllArgsConstructor
@Schema(name = "StationRegionVO对象", description = "StationRegionVO对象")
public class StationRegionVO {

	@Schema(description = "项目id")
	private String bizProjectId;
	@Schema(description = "项目名称")
	private String projectName;
	@Schema(description = "区域类型 1:房间 2:区域")
	private String regionType;
	@Schema(description = "区域名称")
	private String regionName;
	@Schema(description = "区域代码")
	private String regionCode;
	@Schema(description = "设备列表")
	private List<String> devices;



}
