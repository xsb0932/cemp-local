package com.landleaf.comm.constance;

public interface CommonConstant {
    /**
     * 未删除标志
     */
    short DELETED_FLAG_NOT_DELETE = 0;

    /**
     * 删除标志
     */
    Integer DELETED_FLAG_DELETED = 1;

    /**
     * 郎绿租户code
     */
    String TENANT_ADMIN_CODE = "LANDLEAF";

    /**
     * 任务执行成功
     */
    int JOB_EXEC_SUCCESS = 0;

    /**
     * 任务执行异常
     */
    int JOB_EXEC_ERROR = 1;
}
