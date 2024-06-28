package com.landleaf.pgsql.handler.type;

import org.apache.ibatis.type.MappedTypes;

import java.util.List;

/**
 * @author Yang
 */
@MappedTypes({List.class})
public class LongListTypeHandler extends ListTypeHandler<Long> {

    @Override
    protected Class<Long> specificType() {
        return Long.class;
    }
}
