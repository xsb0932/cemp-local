package com.landleaf.sdl.domain.vo;

import com.landleaf.comm.vo.CommonStaVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 当年负荷使用结构
 *
 * @author xshibai
 * @since 2023/11/30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "当年负荷使用结构", description = "当年负荷使用结构")
public class SDLSubitemYearRatoVO {

	@Schema(description = "饼图数据")
	private List<CommonStaVO> pieChartData;

}
