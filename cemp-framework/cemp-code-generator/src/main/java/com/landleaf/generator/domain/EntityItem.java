package com.landleaf.generator.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EntityItem {
	private String columnName;

	private String columnNameNew;

	private String columnType;

	private String columnDesc;

	private String isPriKey;

	private String isAutoIncr;
}
