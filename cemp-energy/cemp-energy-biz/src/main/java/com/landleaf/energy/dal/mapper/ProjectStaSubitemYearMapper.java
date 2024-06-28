package com.landleaf.energy.dal.mapper;

import java.util.List;

import com.landleaf.energy.domain.entity.ProjectStaSubitemMonthEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.energy.domain.entity.ProjectStaSubitemYearEntity;
import org.apache.ibatis.annotations.Select;

/**
 * ProjectStaSubitemYearEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectStaSubitemYearMapper extends BaseMapper<ProjectStaSubitemYearEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    @Select("SELECT * FROM tb_project_sta_subitem_year t1 where t1.deleted = 0 and t1.biz_project_id = #{bizProjectId} and t1.year = #{year} limit 1")
    ProjectStaSubitemYearEntity getCurrentYear(@Param("bizProjectId") String bizProjectId,
                                               @Param("year") String year);
}
