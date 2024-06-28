package com.landleaf.bms.dal.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.dto.MessageQueryDTO;
import com.landleaf.bms.domain.vo.MessageVO;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.landleaf.bms.domain.entity.MessageEntity;

/**
 * 消息信息表的数据库操作句柄
 *
 * @author hebin
 * @since 2023-11-27
 */
public interface MessageMapper extends BaseMapper<MessageEntity> {
    /**
     * 根据id的列表，修改对应信息的is_deleted字段
     *
     * @param ids       id的列表
     * @param isDeleted 修改后的值
     */
    void updateIsDeleted(@Param("ids") List<Long> ids, @Param("isDeleted") Integer isDeleted);

    /**
     * 将阅读数+1
     *
     * @param id
     */
    void increaseReadNum(@Param("id") Long id);

    /**
     * 读取未读的5条消息
     *
     * @param userId
     * @return
     */
    List<MessageVO> selectUnreadTop5Msg(@Param("userId") Long userId);

    IPage<MessageVO> selectPageByUserInfo(@Param("page") IPage<MessageVO> page, @Param("targetTenantId") Long targetTenantId,
                                          @Param("targetUserId") Long targetUserId, @Param("userId") Long userId, @Param("queryInfo") MessageQueryDTO queryInfo);

    /**
     * 查询未读的消息数
     *
     * @param userId
     * @return
     */
    int selectUnreadCount(@Param("userId") Long userId);
}