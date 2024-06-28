package com.landleaf.energy.domain.enums;

import com.landleaf.comm.exception.ErrorCode;

/**
 * energy 错误码枚举类
 * <p>
 * energy 系统，使用 1-005-000-000 段
 * @author tycoon
 */
public interface ErrorCodeConstants {

    // ========== 计划用电 模块 1005000000 ==========
    ErrorCode PROJECT_NOT_EXIST = new ErrorCode("1005000000", "目标项目不存在");
    ErrorCode YEAR_PLANNED_ELECTRICITY_EXISTED = new ErrorCode("1005000001", "目标年份计划用电信息已存在");
    ErrorCode PLANNED_ELECTRICITY_NOT_EXIST = new ErrorCode("1005000002", "目标计划用电信息不存在");

    // ========== 计划用水 模块 1006000000 ==========
    ErrorCode YEAR_PLANNED_WATER_EXISTED = new ErrorCode("1006000001", "目标年份计划用水信息已存在");
    ErrorCode PLANNED_WATER_NOT_EXIST = new ErrorCode("1006000002", "目标计划用水信息不存在");

    // ========== 计划用气 模块 1007000000 ==========
    ErrorCode YEAR_PLANNED_GAS_EXISTED = new ErrorCode("1007000001", "目标年份计划用气信息已存在");
    ErrorCode PLANNED_GAS_NOT_EXIST = new ErrorCode("1007000002", "目标计划用气信息不存在");

    // ========== 抄表配置 模块 1004001000 ==========
    ErrorCode RECORD_ALREADY_EXISTS = new ErrorCode("1004001000", "抄表记录已存在");
    ErrorCode RECORD_NOT_EXIST = new ErrorCode("1004001001", "抄表记录不存在");
    ErrorCode OPERATE_TYPE_NOT_EXIST = new ErrorCode("1004001002", "抄表操作类型不存在");
    ErrorCode PARAMETER_EXCEPTIONS = new ErrorCode("1004001003", "参数异常");


}
