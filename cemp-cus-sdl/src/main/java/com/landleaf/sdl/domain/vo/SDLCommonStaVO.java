package com.landleaf.sdl.domain.vo;

import com.landleaf.comm.vo.CommonStaVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 绥德路项目 - 通用图数据结构
 *
 * @author xusihbai
 * @since 2023/11/29
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SDLCommonStaVO {
    @Schema(description = "柱状图")
    private List<CommonStaVO> barChartData;
}
