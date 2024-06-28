package com.landleaf.generator.db;

import com.landleaf.generator.domain.EntityItem;

import java.sql.SQLException;
import java.util.List;

public abstract class DbOperator {
    /**
     * 根据表名，获取表描述
     *
     * @return
     */
    public abstract String getTableComment(String tableName) throws SQLException;

    /**
     * 根据库名，表明，查询字段
     *
     * @param dbName
     * @param dbTableName
     * @return
     */
    public abstract List<EntityItem> getColumns(String dbName, String dbTableName) throws SQLException;
}
