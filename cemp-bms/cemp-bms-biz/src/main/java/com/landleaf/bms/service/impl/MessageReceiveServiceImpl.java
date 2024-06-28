package com.landleaf.bms.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

import com.landleaf.bms.domain.vo.MessageReceiveVO;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.landleaf.comm.exception.BusinessException;
import com.landleaf.bms.dal.mapper.MessageReceiveMapper;
import com.landleaf.bms.domain.dto.MessageReceiveAddDTO;
import com.landleaf.bms.domain.dto.MessageReceiveQueryDTO;
import com.landleaf.bms.domain.entity.MessageReceiveEntity;
import com.landleaf.bms.service.MessageReceiveService;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息读取信息表的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-11-27
 */
@Service
@AllArgsConstructor
@Slf4j
public class MessageReceiveServiceImpl extends ServiceImpl<MessageReceiveMapper, MessageReceiveEntity> implements MessageReceiveService {

    /**
     * 数据库操作句柄
     */
    private final MessageReceiveMapper messageReceiveMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageReceiveAddDTO save(MessageReceiveAddDTO addInfo) {
        MessageReceiveEntity entity = new MessageReceiveEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        int effectNum = messageReceiveMapper.insert(entity);
        if (0 == effectNum) {
            // 插入失败
            throw new BusinessException(ErrorCodeEnumConst.DATA_INSERT_ERROR.getCode(), ErrorCodeEnumConst.DATA_INSERT_ERROR.getMessage());
        }
        BeanUtil.copyProperties(entity, addInfo);
        return addInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MessageReceiveAddDTO updateInfo) {
        MessageReceiveEntity entity = messageReceiveMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));

        messageReceiveMapper.updateById(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIsDeleted(String ids, Integer isDeleted) {
        String[] idArray = ids.split(",");
        List<Long> idList = new ArrayList<Long>();
        for (String id : idArray) {
            idList.add(Long.valueOf(id));
        }
        messageReceiveMapper.updateIsDeleted(idList, isDeleted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageReceiveEntity selectById(Long id) {
        MessageReceiveEntity entity = messageReceiveMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MessageReceiveEntity> list(MessageReceiveQueryDTO queryInfo) {
        return messageReceiveMapper.selectList(getCondition(queryInfo));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IPage<MessageReceiveEntity> page(MessageReceiveQueryDTO queryInfo) {
        IPage<MessageReceiveEntity> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = messageReceiveMapper.selectPage(page, getCondition(queryInfo));
        return page;
    }

    @Override
    public void removeByMsgId(Long msgId) {
        messageReceiveMapper.delete(new QueryWrapper<MessageReceiveEntity>().lambda().eq(MessageReceiveEntity::getMsgId, msgId));
    }

    @Override
    public boolean readByMsgId(Long msgId) {
        Long userId = LoginUserUtil.getLoginUserId();
        List<MessageReceiveEntity> list = messageReceiveMapper.selectList(new QueryWrapper<MessageReceiveEntity>().lambda().eq(MessageReceiveEntity::getMsgId, msgId).eq(MessageReceiveEntity::getTargetUserId, userId));
        if (CollectionUtils.isEmpty(list)) {
            return false;
        }
        MessageReceiveEntity current = list.get(0);
        if (1 == current.getReadFlag().intValue()) {
            // 已读， 直接返回
            return false;
        }
        current.setReceiveType(1L);
        current.setReadFlag(1L);
        updateById(current);
        return true;
    }

    @Override
    public List<MessageReceiveVO> selectListByMsgIds(List<Long> msgIds) {
        return messageReceiveMapper.selectListByMsgIds(msgIds);
    }

    @Override
    public List<String> selectEmailListByMsgId(Long msgId) {
        return messageReceiveMapper.selectEmailListByMsgId(msgId);
    }

    /**
     * 封装查询的请求参数
     *
     * @param queryInfo 请求参数
     * @return sql查询参数封装
     */
    private LambdaQueryWrapper<MessageReceiveEntity> getCondition(MessageReceiveQueryDTO queryInfo) {
        LambdaQueryWrapper<MessageReceiveEntity> wrapper = new QueryWrapper<MessageReceiveEntity>().lambda().eq(MessageReceiveEntity::getDeleted, CommonConstant.DELETED_FLAG_NOT_DELETE);

        // 开始时间
        if (!StringUtils.isEmpty(queryInfo.getStartTime())) {
            long startTimeMillion = 0L;
            try {
                startTimeMillion = DateUtils
                        .parseDate(queryInfo.getStartTime() + " 00:00:00")
                        .getTime();
            } catch (Exception e) {
                log.error("查询参数错误，startTime不符合格式{}", queryInfo.getStartTime());
                throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR.getCode(), ErrorCodeEnumConst.DATE_FORMAT_ERROR.getMessage());
            }
            wrapper.le(MessageReceiveEntity::getCreateTime, new Timestamp(startTimeMillion));
        }

        // 结束时间
        if (!StringUtils.isEmpty(queryInfo.getEndTime())) {
            long endTimeMillion = 0L;
            try {
                endTimeMillion = DateUtils
                        .parseDate(queryInfo.getEndTime() + " 23:59:59")
                        .getTime();
            } catch (Exception e) {
                log.error("查询参数错误，endTime不符合格式{}", queryInfo.getEndTime());
                throw new BusinessException(ErrorCodeEnumConst.DATE_FORMAT_ERROR.getCode(), ErrorCodeEnumConst.DATE_FORMAT_ERROR.getMessage());
            }
            wrapper.ge(MessageReceiveEntity::getCreateTime, new Timestamp(endTimeMillion));
        }
        // id
        if (null != queryInfo.getId()) {
            wrapper.eq(MessageReceiveEntity::getId, queryInfo.getId());
        }
        // 消息的Id
        if (null != queryInfo.getMsgId()) {
            wrapper.eq(MessageReceiveEntity::getMsgId, queryInfo.getMsgId());
        }
        // 消息的bizId
        if (!StringUtils.hasText(queryInfo.getBizMsgId())) {
            wrapper.like(MessageReceiveEntity::getBizMsgId, "%" + queryInfo.getBizMsgId() + "%");
        }
        // 发送给对应的tenantId
        if (null != queryInfo.getTargetTenantId()) {
            wrapper.eq(MessageReceiveEntity::getTargetTenantId, queryInfo.getTargetTenantId());
        }
        // 消发送给对应userId
        if (null != queryInfo.getTargetUserId()) {
            wrapper.eq(MessageReceiveEntity::getTargetUserId, queryInfo.getTargetUserId());
        }
        // 是否已读：0未读；1已读
        if (null != queryInfo.getReadFlag()) {
            wrapper.eq(MessageReceiveEntity::getReadFlag, queryInfo.getReadFlag());
        }
        // 接收方式：未接收为0，否则为对应的接收方式。
        if (null != queryInfo.getReceiveType()) {
            wrapper.eq(MessageReceiveEntity::getReceiveType, queryInfo.getReceiveType());
        }
        // 租户id
        if (null != queryInfo.getTenantId()) {
            wrapper.eq(MessageReceiveEntity::getTenantId, queryInfo.getTenantId());
        }
        wrapper.orderByDesc(MessageReceiveEntity::getUpdateTime);
        return wrapper;
    }
}