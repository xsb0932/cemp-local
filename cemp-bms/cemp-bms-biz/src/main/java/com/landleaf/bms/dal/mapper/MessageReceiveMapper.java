package com.landleaf.bms.dal.mapper;

import java.util.List;

import com.landleaf.bms.domain.vo.MessageReceiveVO;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.bms.domain.entity.MessageReceiveEntity;

/**
 * 消息读取信息表的数据库操作句柄
 *
 * @author hebin
 * @since 2023-11-27
 */
public interface MessageReceiveMapper extends BaseMapper<MessageReceiveEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    /**
     * 根据msgIds，获取对应的receive信息
     *
     * @param msgIds
     * @return
     */
    List<MessageReceiveVO> selectListByMsgIds(@Param("msgIds") List<Long> msgIds);

    List<String> selectEmailListByMsgId(@Param("msgId") Long msgId);
}