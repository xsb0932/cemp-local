package com.landleaf.pgsql.handler.type;

import org.apache.ibatis.type.MappedTypes;

import java.util.List;

/**
 * @author Yang
 */
@MappedTypes({List.class})
public class StringListTypeHandler extends ListTypeHandler<String> {

    @Override
    protected Class<String> specificType() {
        return String.class;
    }

}
