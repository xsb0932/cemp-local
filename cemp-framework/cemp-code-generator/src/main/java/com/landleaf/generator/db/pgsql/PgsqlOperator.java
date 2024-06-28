package com.landleaf.generator.db.pgsql;

import com.landleaf.generator.db.DbOperator;
import com.landleaf.generator.db.DbUtil;
import com.landleaf.generator.domain.EntityItem;

import java.sql.SQLException;
import java.util.List;

public class PgsqlOperator extends DbOperator {

    private static DbUtil dbUtil = null;

    public PgsqlOperator(String url, String username, String password) throws SQLException {
        super();
        DbUtil.url = url;
        DbUtil.username = username;
        DbUtil.password = password;
        DbUtil.typeConstantMap = TypeConstance.map;
        DbUtil.init();
    }

    @Override
    public String getTableComment(String tableName) throws SQLException {
        String sql = "select obj_description(relfilenode, 'pg_class') as TABLE_COMMENT from pg_class where relname='"+ tableName +"'";
        return DbUtil.getTableComment(sql);
    }

    @Override
    public List<EntityItem> getColumns(String dbName, String dbTableName) throws SQLException {

        String sql = "select  a.attname as COLUMN_NAME,t.typname as DATA_TYPE,d.description as COLUMN_COMMENT,\n" +
                "case when pc.contype='p' then 'PRI' else '' end COLUMN_KEY, case when col.is_identity ='YES' then 'auto_increment' else '' end as EXTRA from pg_class c\n" +
                "left join pg_attribute a on a.attnum>0 and  a.attrelid =  c.oid\n" +
                "left join  pg_type t on a.atttypid  = t.oid\n" +
                "left join pg_description d on d.objoid =a.attrelid and d.objsubid=a.attnum\n" +
                "left join pg_constraint pc on a.attnum = pc.conkey[1] and pc .conrelid=c.oid\n" +
                "left join information_schema.columns col on col.table_name= c.relname and col.\"column_name\" = a.attname\n" +
                "where c.relname =  '"+dbTableName+"' and a.attnum>0 and col.table_catalog='"+dbName+"'";
        return DbUtil.getColumns(sql);
    }
}
