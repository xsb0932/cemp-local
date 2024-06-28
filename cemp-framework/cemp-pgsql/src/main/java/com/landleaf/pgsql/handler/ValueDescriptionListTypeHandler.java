package com.landleaf.pgsql.handler;

import com.landleaf.comm.base.bo.ValueDescription;
import com.landleaf.pgsql.handler.type.ListTypeHandler;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

@MappedTypes({List.class})
public class ValueDescriptionListTypeHandler extends ListTypeHandler<ValueDescription> {
    @Override
    protected Class<ValueDescription> specificType() {
        return ValueDescription.class;
    }
}
