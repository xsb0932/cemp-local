package com.landleaf.bms.handler;

import com.landleaf.bms.api.json.FunctionParameter;
import com.landleaf.pgsql.handler.type.ListTypeHandler;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

/**
 * FunctionParameter类型JSON处理
 *
 * @author yue lin
 * @since 2023/6/27 11:06
 */
@MappedTypes({List.class})
public class FunctionParameterListTypeHandler extends ListTypeHandler<FunctionParameter> {

    @Override
    protected Class<FunctionParameter> specificType() {
        return FunctionParameter.class;
    }

}
