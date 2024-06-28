package com.landleaf.bms.handler;

import com.landleaf.bms.domain.bo.Topic;
import com.landleaf.pgsql.handler.type.ListTypeHandler;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

/**
 * @author Yang
 */
@MappedTypes({List.class})
public class TopicListTypeHandler extends ListTypeHandler<Topic> {
    @Override
    protected Class<Topic> specificType() {
        return Topic.class;
    }
}
