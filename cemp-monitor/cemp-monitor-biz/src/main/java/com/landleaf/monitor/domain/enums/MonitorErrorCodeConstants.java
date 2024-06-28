package com.landleaf.monitor.domain.enums;

import com.landleaf.comm.exception.ErrorCode;

/**
 * 综合监控 错误码枚举类
 * <p>
 * 综合监控 系统，使用 1-004-000-000 段
 * </p>
 */
public interface MonitorErrorCodeConstants {
    // ========== 视图配置 模块 1004000000 ==========
    ErrorCode AVUE_CREATE_ERROR = new ErrorCode("1004000000", "avue创建视图失败");
    ErrorCode VIEW_CREATE_ERROR = new ErrorCode("1004000001", "视图创建失败");
    ErrorCode VIEW_UPDATE_ERROR = new ErrorCode("1004000002", "视图修改失败");
    ErrorCode VIEW_DELETE_ERROR = new ErrorCode("1004000003", "视图删除失败");
    ErrorCode VIEW_PUBLISH_ERROR = new ErrorCode("1004000004", "视图发布状态修改失败");
    ErrorCode VIEW_NOT_EXISTS = new ErrorCode("1004000005", "视图不存在");
}
