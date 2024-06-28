package com.landleaf.bms.handler;

import com.landleaf.bms.api.json.ValueDescription;
import com.landleaf.pgsql.handler.type.ListTypeHandler;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

/**
 * ValueDescription的JSON处理器
 *
 * @author yue lin
 * @since 2023/6/27 9:16
 */
@MappedTypes({List.class})
public class ValueDescriptionListTypeHandler extends ListTypeHandler<ValueDescription> {
    @Override
    protected Class<ValueDescription> specificType() {
        return ValueDescription.class;
    }
}
