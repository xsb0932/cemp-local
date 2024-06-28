package com.landleaf.energy.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.energy.domain.entity.ProjectCnfTimePeriodEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ProjectCnfTimePeriodEntity对象的数据库操作句柄
 *
 * @author hebin
 * @since 2023-06-24
 */
@Mapper
public interface ProjectCnfTimePeriodMapper extends BaseMapper<ProjectCnfTimePeriodEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    /**
     * 获取项目某月的电费配置
     *
     * @param projectBizId  项目业务ID
     * @param year  年份
     * @param month 月份
     * @return  结果
     */
    default List<ProjectCnfTimePeriodEntity> searchByProjectYearMonth(String projectBizId, int year, int month) {
        return selectList(Wrappers.<ProjectCnfTimePeriodEntity>lambdaQuery()
                .eq(ProjectCnfTimePeriodEntity::getProjectId, projectBizId)
                .eq(ProjectCnfTimePeriodEntity::getPeriodYear, String.valueOf(year))
                .eq(ProjectCnfTimePeriodEntity::getPeriodMonth, String.valueOf(month))
        );
    }
}
