package com.landleaf.sdl.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.sdl.domain.entity.ProjectStaSubareaDayEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * ProjectStaSubareaDayEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaSubareaDayMapper extends BaseMapper<ProjectStaSubareaDayEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("select t1.subarea_name,sum(sta_value) as sta_value from tb_project_sta_subarea_day t1 where t1.deleted = 0 and t1.kpi_code = 'area.electricity.energyUsage.total' and t1.sta_time > #{beginTime} and t1.sta_time < #{endTime} and t1.biz_project_id = #{projectBizId} and t1.subarea_code in (select id||'' from tb_project_cnf_subarea where project_id = #{projectBizId} and parent_id is not null )GROUP BY t1.subarea_name order by sta_value desc")
    List<ProjectStaSubareaDayEntity> getSubareaOrder(@Param("projectBizId") String projectBizId,
                                                     @Param("beginTime") String beginTime,
                                                     @Param("endTime") String endTime);
}
