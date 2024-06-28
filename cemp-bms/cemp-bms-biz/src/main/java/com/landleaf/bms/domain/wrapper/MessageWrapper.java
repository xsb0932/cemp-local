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

import com.landleaf.bms.domain.entity.MessageEntity;
import com.landleaf.bms.domain.vo.MessageVO;

/**
 * 消息信息表的展示类型转化工具
 *
 * @author hebin
 * @since 2023-11-27
 */
public class MessageWrapper extends BaseWrapper<MessageVO, MessageEntity> {
	/**
	 * 构造
	 */
	public static MessageWrapper builder() {
		return new MessageWrapper();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MessageVO entity2VO(MessageEntity e) {
		if (null == e) {
			return null;
		}
		MessageVO vo = new MessageVO();
		BeanUtil.copyProperties(e, vo);
		return vo;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<MessageVO> listEntity2VO(List<MessageEntity> eList) {
		if (!CollectionUtils.isEmpty(eList)) {
			return eList.stream().map(i -> BeanUtil.copyProperties(i, MessageVO.class))
					.collect(Collectors.toList());
		}
		return new ArrayList<MessageVO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<MessageVO> pageEntity2VO(IPage<MessageEntity> page) {
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
		pageVO.setRecords(listEntity2VO(page.getRecords()));
		return pageVO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PageDTO<MessageVO> pageEntity2VO(IPage<MessageEntity> page, Map<?, ?> map) {
		return pageEntity2VO(page);
	}
}