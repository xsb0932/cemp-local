package com.landleaf.bms.domain.wrapper;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.landleaf.pgsql.base.wrapper.BaseWrapper;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.landleaf.bms.domain.entity.MessageReceiveEntity;
import com.landleaf.bms.domain.vo.MessageReceiveVO;

/**
 * 消息读取信息表的展示类型转化工具
 *
 * @author hebin
 * @since 2023-11-27
 */
public class MessageReceiveWrapper extends BaseWrapper<MessageReceiveVO, MessageReceiveEntity> {
	/**
	 * 构造
	 */
	public static MessageReceiveWrapper builder() {
		return new MessageReceiveWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MessageReceiveVO entity2VO(MessageReceiveEntity e) {
		if (null == e) {
			return null;
		}
		MessageReceiveVO vo = new MessageReceiveVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MessageReceiveVO> listEntity2VO(List<MessageReceiveEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, MessageReceiveVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<MessageReceiveVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<MessageReceiveVO> pageEntity2VO(IPage<MessageReceiveEntity> page) {
		PageDTO<MessageReceiveVO> pageVO = new PageDTO<MessageReceiveVO>();
		if (null == page) {
			pageVO.setCurrent(0);
			pageVO.setTotal(0);
			pageVO.setPages(1);
			pageVO.setRecords(new ArrayList<MessageReceiveVO>());
			return pageVO;
		}
		pageVO.setCurrent(page.getCurrent());
		pageVO.setTotal(page.getTotal());
		pageVO.setPages(page.getPages());
		pageVO.setRecords(listEntity2VO(page.getRecords()));
		return pageVO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<MessageReceiveVO> pageEntity2VO(IPage<MessageReceiveEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}