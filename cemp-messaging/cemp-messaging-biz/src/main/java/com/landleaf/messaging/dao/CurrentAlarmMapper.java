package com.landleaf.messaging.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.messaging.domain.CurrentAlarmEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * CurrentAlarmMapper
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Mapper
public interface CurrentAlarmMapper extends BaseMapper<CurrentAlarmEntity> {

    @Delete("delete from tb_current_alarm where id = #{id}")
    void delCurrentAlarm(@Param("id") Long id);
}
