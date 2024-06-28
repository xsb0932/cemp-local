package com.landleaf.generator.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConditionItem {
	/**
	 * 注释的描述
	 */
	private String desc;

	/**
	 * 执行条件
	 */
	private String condition;
	
	/**
	 * 查询类型
	 */
	private String queryType;
	
	/**
	 * 查询的键
	 */
	private String key;

	/**
	 * 查询的值
	 */
	private String value;
	
	private String mybatisKey;
	
	private String mybatisType;
}
