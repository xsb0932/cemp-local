package com.landleaf.influx.enums;

import com.landleaf.influx.condition.ISqlSegment;
import lombok.AllArgsConstructor;

/**
 * @author lokiy
 * @date 2021/12/13
 * @description sql关键字枚举类
 */
@AllArgsConstructor
public enum SqlKeyword implements ISqlSegment {
    /**
     * sql关键字
     */
    AND("AND"),
    OR("OR"),
    IN("IN"),
    NOT("NOT"),
    LIKE("LIKE"),
    EQ("="),
    NE("<>"),
    GT(">"),
    GE(">="),
    LT("<"),
    LE("<="),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL"),
    GROUP_BY("GROUP BY"),
    HAVING("HAVING"),
    ORDER_BY("ORDER BY"),
    EXISTS("EXISTS"),
    BETWEEN("BETWEEN"),
    ASC("ASC"),
    DESC("DESC");

    private final String keyword;

    @Override
    public String getSqlSegment() {
        return this.keyword;
    }
}
