package com.landleaf.pgsql.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @author eason
 */
public class DefaultDataObjectFieldHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "creator", Long.class, LoginUserUtil.getLoginUserId());
        this.strictInsertFill(metaObject, "updater", Long.class, LoginUserUtil.getLoginUserId());
        // todo 这里后续有问题,先加个判断看看吧，后续在调整
        if (!TenantContext.isIgnore()) {
            this.strictInsertFill(metaObject, "tenantId", Long.class, TenantContext.getTenantId());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
//        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
//        this.strictUpdateFill(metaObject, "updater", Long.class, LoginUserUtil.getLoginUserId());
        try {
            this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
            this.setFieldValByName("updater", LoginUserUtil.getLoginUserId(), metaObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
