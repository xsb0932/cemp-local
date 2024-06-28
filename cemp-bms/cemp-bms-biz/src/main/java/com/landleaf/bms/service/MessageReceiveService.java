package com.landleaf.bms.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.bms.domain.dto.MessageReceiveAddDTO;
import com.landleaf.bms.domain.dto.MessageReceiveQueryDTO;
import com.landleaf.bms.domain.entity.MessageReceiveEntity;
import com.landleaf.bms.domain.vo.MessageReceiveVO;

/**
 * 消息读取信息表的业务逻辑接口定义 
 *
 * @author hebin
 * @since 2023-11-27
 */
public interface MessageReceiveService extends IService<MessageReceiveEntity> {

	/**
	 * 新增一个对象
	 * 
	 * @param addInfo
	 *            新增对象的数据的封装
	 * @return 新增后的数据对象
	 */
	MessageReceiveAddDTO save(MessageReceiveAddDTO addInfo);

	/**
	 * 修改一个对象
	 * 
	 * @param updateInfo
	 *            修改对象的数据的封装
	 */
	void update(MessageReceiveAddDTO updateInfo);

	/**
	 * 修改数据的is_delete标识
	 * 
	 * @param ids
	 *            要修改的数据的编号
	 * @param isDeleted
	 *            删除标记
	 */
	void updateIsDeleted(String ids, Integer isDeleted);

	/**
	 * 根据id，查询详情
	 * 
	 * @param id
	 *            编号
	 * @return 详情信息
	 */
	MessageReceiveEntity selectById(Long id);

	/**
	 * 根据查询条件，查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合
	 */
	List<MessageReceiveEntity> list(MessageReceiveQueryDTO queryInfo);

	/**
	 * 根据查询条件，分页查询实体的集合
	 * 
	 * @param queryInfo
	 *            查询条件封装
	 * @return 实体的集合的分页信息
	 */
	IPage<MessageReceiveEntity> page(MessageReceiveQueryDTO queryInfo);

	/**
	 * 根据msgid,删除对应的阅读相请
	 * @param msgId
	 */
    void removeByMsgId(Long msgId);

	/**
	 * 根据msgid,将相应的消息，置为已读
	 * @param id
	 */
	boolean readByMsgId(Long id);

	/**
	 * 根据msgId，获取voList
	 * @param msgIds
	 * @return
	 */
    List<MessageReceiveVO> selectListByMsgIds(List<Long> msgIds);

	/**
	 * 根据msgId,查询需要推送的user的邮箱
	 *
	 * @param id
	 * @return
	 */
    List<String> selectEmailListByMsgId(Long id);
}