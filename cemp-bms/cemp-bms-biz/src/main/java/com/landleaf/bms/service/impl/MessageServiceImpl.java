package com.landleaf.bms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.api.dto.MessageAddRequest;
import com.landleaf.bms.api.dto.MsgNoticeUserDTO;
import com.landleaf.bms.api.enums.MsgStatusEnum;
import com.landleaf.bms.dal.mapper.MessageMapper;
import com.landleaf.bms.domain.dto.MessageQueryDTO;
import com.landleaf.bms.domain.entity.MessageEntity;
import com.landleaf.bms.domain.entity.MessageReceiveEntity;
import com.landleaf.bms.domain.vo.MessageReceiveVO;
import com.landleaf.bms.domain.vo.MessageVO;
import com.landleaf.bms.service.MessageReceiveService;
import com.landleaf.bms.service.MessageService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.constance.CommonConstant;
import com.landleaf.comm.constance.ErrorCodeEnumConst;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.enums.GlobalErrorCodeConstants;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.mail.domain.param.MsgPushMail;
import com.landleaf.mail.service.MailService;
import com.landleaf.oauth.api.UserRoleApi;
import com.landleaf.pgsql.core.BizSequenceService;
import com.landleaf.pgsql.enums.BizSequenceEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消息信息表的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-11-27
 */
@Service
@AllArgsConstructor
@Slf4j
public class MessageServiceImpl extends ServiceImpl<MessageMapper, MessageEntity> implements MessageService {

    /**
     * 数据库操作句柄
     */
    private final MessageMapper messageMapper;

    private final MessageReceiveService messageReceiveServiceImpl;

    private final BizSequenceService bizSequenceService;

    private final UserRoleApi userRoleApi;

    private final MailService mailService;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public MessageAddRequest save(MessageAddRequest addInfo) {
        TenantContext.setIgnore(true);
        MessageEntity entity = new MessageEntity();
        BeanUtil.copyProperties(addInfo, entity);
        if (null == entity.getDeleted()) {
            entity.setDeleted(CommonConstant.DELETED_FLAG_NOT_DELETE);
        }
        if (null == entity.getCreateTime()) {
            entity.setCreateTime(LocalDateTime.now());
        }
        String bizMsgId = bizSequenceService.next(BizSequenceEnum.MSG);
        entity.setBizMsgId(bizMsgId);
        // 处理发布途径
        entity.setNoticeType(Long.valueOf(addInfo.getMailFlag() + (addInfo.getEmailFlag() << 1)));
        entity.setPublisher(LoginUserUtil.getLoginUserId());
        entity.setPublishTime(LocalDateTime.now());
        if (null == entity.getTenantId()) {
            entity.setTenantId(TenantContext.getTenantId());
        }
        int effectNum = messageMapper.insert(entity);
        if (0 == effectNum) {
            // 插入失败
            throw new BusinessException(ErrorCodeEnumConst.DATA_INSERT_ERROR.getCode(), ErrorCodeEnumConst.DATA_INSERT_ERROR.getMessage());
        }

        BeanUtil.copyProperties(entity, addInfo);
        // 添加detail
        List<MessageReceiveEntity> receiveEntityList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(addInfo.getNoticeUserInfo())) {
            addInfo.getNoticeUserInfo().forEach(i -> {
                MessageReceiveEntity temp = new MessageReceiveEntity();
                temp.setTenantId(i.getTenantId());
                temp.setCreateTime(LocalDateTime.now());
                temp.setBizMsgId(entity.getBizMsgId());
                temp.setMsgId(entity.getId());
                temp.setReadFlag(0L);
                temp.setReceiveType(0L);
                temp.setTargetUserId(i.getUserId());
                temp.setTargetTenantId(i.getTenantId());
                receiveEntityList.add(temp);
            });
        }
        messageReceiveServiceImpl.saveBatch(receiveEntityList);

        // 如果状态为发布，则需要发送对应的邮件
        new Thread(() -> {
            sendEmailByMsg(entity);
        }).start();
        return addInfo;
    }

    private void sendEmailByMsg(MessageEntity entity) {
        TenantContext.setIgnore(true);
        // 如果状态不是发布，则返回
        if (!MsgStatusEnum.PUBLISHED.getType().equals(entity.getMsgStatus())) {
            return;
        }
        if (0 >= (entity.getNoticeType() & 2)) {
            // 不需要发邮件，返回
            return;
        }
        // 根据msgId,查询对应的receive
        List<String> emailList = messageReceiveServiceImpl.selectEmailListByMsgId(entity.getId());
        if (!CollectionUtils.isEmpty(emailList)) {
            for (String email : emailList) {
                mailService.sendMailAsync(MsgPushMail.mail(email, entity.getMsgTitle(), entity.getMsgContent()));
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MessageAddRequest updateInfo) {
        TenantContext.setIgnore(true);
        MessageEntity entity = messageMapper.selectById(updateInfo.getId());
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        // 仅支持修改类型为草稿的
        if (!MsgStatusEnum.DRAFT.getType().equals(entity.getMsgStatus())) {
            throw new BusinessException(GlobalErrorCodeConstants.ILLEGAL_MESSAGE_STATUS);
        }
        // 处理发布途径
        entity.setNoticeType(Long.valueOf(updateInfo.getMailFlag() + (updateInfo.getEmailFlag() << 1)));
        boolean sendFalg = false;
        if (MsgStatusEnum.PUBLISHED.getType().equals(updateInfo.getMsgStatus()) && !MsgStatusEnum.PUBLISHED.getType().equals(entity.getMsgStatus())) {
            entity.setPublisher(LoginUserUtil.getLoginUserId());
            entity.setPublishTime(LocalDateTime.now());
            sendFalg = true;
        }
        BeanUtil.copyProperties(updateInfo, entity, CopyOptions.create().setIgnoreNullValue(true).setIgnoreError(true));
        messageMapper.updateById(entity);
        // 添加detail，删除旧的，添加新的
        messageReceiveServiceImpl.removeByMsgId(updateInfo.getId());
        List<MessageReceiveEntity> receiveEntityList = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(updateInfo.getNoticeUserInfo())) {
            updateInfo.getNoticeUserInfo().forEach(i -> {
                MessageReceiveEntity temp = new MessageReceiveEntity();
                temp.setTenantId(i.getTenantId());
                temp.setCreateTime(LocalDateTime.now());
                temp.setBizMsgId(entity.getBizMsgId());
                temp.setMsgId(entity.getId());
                temp.setReadFlag(0L);
                temp.setReceiveType(0L);
                temp.setTargetUserId(i.getUserId());
                temp.setTargetTenantId(i.getTenantId());
                receiveEntityList.add(temp);
            });
        }
        messageReceiveServiceImpl.saveBatch(receiveEntityList);
        if (sendFalg) {
            new Thread(() -> {
                sendEmailByMsg(entity);
            }).start();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateIsDeleted(String ids, Integer isDeleted) {
        TenantContext.setIgnore(true);
        String[] idArray = ids.split(",");
        List<Long> idList = new ArrayList<Long>();
        for (String id : idArray) {
            idList.add(Long.valueOf(id));
        }
        messageMapper.updateIsDeleted(idList, isDeleted);
        for (Long id : idList) {
            messageReceiveServiceImpl.removeByMsgId(id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessageEntity selectById(Long id) {
        MessageEntity entity = messageMapper.selectById(id);
        if (null == entity) {
            return null;
        }
        return CommonConstant.DELETED_FLAG_NOT_DELETE == entity.getDeleted().intValue() ? entity : null;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MessageVO> listTop5() {
        TenantContext.setIgnore(true);
        Long userId = LoginUserUtil.getLoginUserId();
        return messageMapper.selectUnreadTop5Msg(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageDTO<MessageVO> page(MessageQueryDTO queryInfo) {
        TenantContext.setIgnore(true);
        // 如果是平台管理员，则查询所有
        TenantContext.setIgnore(true);
        Long userId = LoginUserUtil.getLoginUserId();
        Response<Boolean> roleResp = userRoleApi.isPlatformAdmin(userId);
        Long targetTenantId = null;
        Long targetUserId = null;
        if (!roleResp.isSuccess()) {
            // 查询用户信息失败，返回错误
            throw new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
        } else {
            if (!roleResp.getResult()) {
                // 如果是租户管理员，则查看租户所有
                roleResp = userRoleApi.isTenantAdmin(userId);
                targetTenantId = TenantContext.getTenantId();
                if (!roleResp.isSuccess()) {
                    // 查询用户信息失败，返回错误
                    throw new BusinessException(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR);
                } else {
                    if (!roleResp.getResult()) {
                        targetUserId = userId;
                    }
                }
            }
        }

        // 如果普通租户，查看给自己的
        IPage<MessageVO> page = new Page<>(queryInfo.getPageNo(), queryInfo.getPageSize());
        page = messageMapper.selectPageByUserInfo(page, targetTenantId, targetUserId, userId, queryInfo);
        // 将target信息放入内部
        List<Long> msgIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(page.getRecords())) {
            page.getRecords().forEach(i -> {
                msgIds.add(i.getId());
                i.setNoticeUserInfo(new ArrayList<>());
            });
            // 查询detail
            List<MessageReceiveVO> receiveList = messageReceiveServiceImpl.selectListByMsgIds(msgIds);
            if (!CollectionUtils.isEmpty(receiveList)) {
                Map<Long, List<MessageReceiveVO>> map = receiveList.stream().collect(Collectors.groupingBy(MessageReceiveVO::getMsgId));
                page.getRecords().forEach(i -> {
                    if (map.containsKey(i.getId())) {
                        List<MessageReceiveVO> temp = map.get(i.getId());
                        temp.forEach(j -> {
                            i.getNoticeUserInfo().add(MsgNoticeUserDTO.builder().tenantId(j.getTargetTenantId()).tenantName(j.getTargetTenantName()).userId(j.getTargetUserId()).userName(j.getTargetUserName()).build());
                        });
                    }
                });
            }
        }

        PageDTO<MessageVO> pageVO = new PageDTO<MessageVO>();
        if (null == page) {
            pageVO.setCurrent(0);
            pageVO.setTotal(0);
            pageVO.setPages(1);
            pageVO.setRecords(new ArrayList<MessageVO>());
            return pageVO;
        }
        pageVO.setCurrent(page.getCurrent());
        pageVO.setTotal(page.getTotal());
        pageVO.setPages(page.getPages());
        pageVO.setRecords(page.getRecords());
        return pageVO;

    }

    @Override
    public MessageVO selectDetailById(Long id) {
        // 如果普通租户，查看给自己的
        TenantContext.setIgnore(true);
        IPage<MessageVO> page = new Page<>(0, 1);
        MessageQueryDTO queryInfo = new MessageQueryDTO();
        queryInfo.setId(id);
        page = messageMapper.selectPageByUserInfo(page, null, null, LoginUserUtil.getLoginUserId(), queryInfo);
        if (!CollectionUtils.isEmpty(page.getRecords())) {
            MessageVO vo = page.getRecords().get(0);
            // 查询detail
            List<MessageReceiveVO> receiveList = messageReceiveServiceImpl.selectListByMsgIds(List.of(id));
            if (!CollectionUtils.isEmpty(receiveList)) {
                vo.setNoticeUserInfo(new ArrayList<>());
                receiveList.forEach(j -> {
                    vo.getNoticeUserInfo().add(MsgNoticeUserDTO.builder().tenantId(j.getTargetTenantId()).tenantName(j.getTargetTenantName()).userId(j.getTargetUserId()).userName(j.getTargetUserName()).build());
                });
            }
            return vo;
        }
        return null;
    }

    @Override
    public MessageVO publishById(Long id) {
        TenantContext.setIgnore(true);
        MessageEntity entity = messageMapper.selectById(id);
        if (null == entity) {
            // 修改失败
            throw new BusinessException(ErrorCodeEnumConst.NULL_VALUE_ERROR.getCode(), ErrorCodeEnumConst.NULL_VALUE_ERROR.getMessage());
        }
        if (!MsgStatusEnum.PUBLISHED.getType().equals(entity.getMsgStatus())) {
            entity.setMsgStatus(MsgStatusEnum.PUBLISHED.getType());
            entity.setPublisher(LoginUserUtil.getLoginUserId());
            entity.setPublishTime(LocalDateTime.now());
            updateById(entity);
            new Thread(() -> {
                sendEmailByMsg(entity);
            }).start();
        }

        return selectDetailById(id);
    }

    @Override
    public MessageVO readById(Long id) {
        TenantContext.setIgnore(true);
        // 将消息置为已读
        boolean readFlag = messageReceiveServiceImpl.readByMsgId(id);
        if (readFlag) {
            // 将当前阅读数+1
            messageMapper.increaseReadNum(id);
        }
        return selectDetailById(id);
    }

    @Override
    public int selectUnreadCount() {
        TenantContext.setIgnore(true);
        Long userId = LoginUserUtil.getLoginUserId();
        return messageMapper.selectUnreadCount(userId);
    }
}