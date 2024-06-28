package com.landleaf.pgsql.core;

import com.landleaf.pgsql.enums.BizSequenceEnum;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Component;

@Component
@MapperScan("com.landleaf.pgsql.core")
@RequiredArgsConstructor
public class BizSequenceService {
    private final BizSequenceMapper mapper;

    public String next(BizSequenceEnum sequenceEnum) {
        if (null == sequenceEnum) {
            throw new IllegalArgumentException("不支持的序列类型");
        }
        return sequenceEnum.getPrefix() + String.format(sequenceEnum.getReg(), mapper.next(sequenceEnum.getCode()));
    }
}
