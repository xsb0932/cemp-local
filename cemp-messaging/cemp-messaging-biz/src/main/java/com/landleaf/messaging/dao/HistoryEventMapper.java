package com.landleaf.messaging.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.messaging.domain.HistoryEventEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * HistoryEventMapper
 *
 * @author 张力方
 * @since 2023/8/14
 **/
@Mapper
public interface HistoryEventMapper extends BaseMapper<HistoryEventEntity> {
}
