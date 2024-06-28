package com.landleaf.influx.condition;

import com.landleaf.influx.enums.SqlKeyword;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lokiy
 * @date 2021/12/13
 * @description where条件记录对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhereCondition {

    /**
     * 查询字段
     */
    private String field;

    /**
     * 比较关键字
     */
    private SqlKeyword compareKeyWord;

    /**
     * 比较值
     */
    private String value;
}
