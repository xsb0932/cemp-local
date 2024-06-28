package com.landleaf.jjgj.domain.vo.rjd;

import com.landleaf.comm.vo.CommonStaVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ScreenProjectBasicVO 锦江定制大屏-基本信息VO
 *
 * @author xshibai
 * @since 2023-11-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "锦江定制大屏-基本信息VO", description = "锦江定制大屏-基本信息VO")
public class ScreenProjectBasicVO {

	@Schema(description = "空调数量")
	private String airNum;

	@Schema(description = "电表数量")
	private String eleNum;

	@Schema(description = "水表数量")
	private String waterNum;

	@Schema(description = "气表数量")
	private String gasNum;
}
