package com.landleaf.comm.constance;

/**
 * 查询历史时的周期类型
 */
public enum PeriodTypeConst {
    /*
     *  原数据
     */
    DEFAULT_PERIOD(0, 1),
    /*
     *5分钟
     */
    FIVE_MINUTES(1, 5),
    /*
     *10分钟
     */
    TEN_MINUTES(2, 10),
    /*
     *30分钟
     */
    THIRTY_MINUTES(3, 30),
    /*
     *1小时
     */
    ONE_HOUR(4, 1),
    /*
     *8小时
     */
    EIGHT_HOURS(5, 8),
    /*
     *1天
     */
    ONE_DAY(6, 1),
    ;

    /**
     * 编码
     */
    private final int type;

    /**
     * 错误信息
     */
    private final int period;

    PeriodTypeConst(int type, int period) {
        this.type = type;
        this.period = period;
    }

    public int getType() {
        return type;
    }

    public int getPeriod() {
        return period;
    }
}
