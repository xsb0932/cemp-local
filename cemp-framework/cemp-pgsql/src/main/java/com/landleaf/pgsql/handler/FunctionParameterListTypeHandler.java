package com.landleaf.pgsql.handler;

import com.landleaf.comm.base.bo.FunctionParameter;
import com.landleaf.pgsql.handler.type.ListTypeHandler;
import org.apache.ibatis.type.MappedTypes;

import java.util.List;

@MappedTypes({List.class})
public class FunctionParameterListTypeHandler extends ListTypeHandler<FunctionParameter> {

    @Override
    protected Class<FunctionParameter> specificType() {
        return FunctionParameter.class;
    }

}