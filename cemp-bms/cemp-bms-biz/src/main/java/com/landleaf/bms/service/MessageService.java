package com.landleaf.bms.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.bms.api.dto.MessageAddRequest;
import com.landleaf.bms.domain.dto.MessageQueryDTO;
import com.landleaf.bms.domain.entity.MessageEntity;
import com.landleaf.bms.domain.vo.MessageVO;

/**
 * 消息信息表的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-11-27
 */
public interface MessageService extends IService<MessageEntity> {

    /**
     * 新增一个对象
     *
     * @param addInfo 新增对象的数据的封装
     * @return 新增后的数据对象
     */
    MessageAddRequest save(MessageAddRequest addInfo);

    /**
     * 修改一个对象
     *
     * @param updateInfo 修改对象的数据的封装
     */
    void update(MessageAddRequest updateInfo);

    /**
     * 修改数据的is_delete标识
     *
     * @param ids       要修改的数据的编号
     * @param isDeleted 删除标记
     */
    void updateIsDeleted(String ids, Integer isDeleted);

    /**
     * 根据id，查询详情
     *
     * @param id 编号
     * @return 详情信息
     */
    MessageEntity selectById(Long id);

    /**
     * 获取当前用户未读的5条
     *
     * @return 实体的集合
     */
    List<MessageVO> listTop5();

    /**
     * 根据查询条件，分页查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合的分页信息
     */
    PageDTO<MessageVO> page(MessageQueryDTO queryInfo);

    /**
     * 根据id，查询相请
     *
     * @param id
     * @return
     */
    MessageVO selectDetailById(Long id);

    /**
     * 根据id，发布某消息
     *
     * @param id
     * @return
     */
    MessageVO publishById(Long id);

    /**
     * 根据id，读取某消息
     *
     * @param id
     * @return
     */
    MessageVO readById(Long id);

    /**
     * 查询未读数量
     * @return
     */
    int selectUnreadCount();
}