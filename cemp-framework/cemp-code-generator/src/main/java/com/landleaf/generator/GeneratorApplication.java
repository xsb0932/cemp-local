package com.landleaf.generator;

import com.landleaf.generator.creator.EntityCreate;
import com.landleaf.generator.db.DbOperator;
import com.landleaf.generator.db.pgsql.PgsqlOperator;
import com.landleaf.generator.domain.EntityItem;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class GeneratorApplication {

    public static String DB_URL = "jdbc:postgresql://172.20.1.91:15432/cemp?useUnicode=true&characterEncoding=utf8&serverTimeZone=CTT&stringtype=unspecified";

    public static String DB_USER = "landleaf";

    public static String DB_PASSWORD = "landleaf@123.com";

    // 逗号分割
    public static String DB_TABLE_NAME =
            "tb_alarm_push_condition,tb_alarm_push_status";

    public static String DB_TABLE_NAME_PREFIX = "tb_";

    public static String DB_NAME = "cemp";

    public static String BASE_PACKAGE_NAME = "com.landleaf.bms";

    public static String EXCEPTION_NAME = "BusinessException";

    public static String ENTITY_PACKAGE = BASE_PACKAGE_NAME + ".domain.entity";

    public static String DTO_PACKAGE = BASE_PACKAGE_NAME + ".domain.dto";

    public static String VO_PACKAGE = BASE_PACKAGE_NAME + ".domain.vo";

    public static String WRAPPER_PACKAGE = BASE_PACKAGE_NAME + ".domain.wrapper";

    public static String REPOSITORY_PACKAGE = BASE_PACKAGE_NAME + ".dal.mapper";

    public static String SERVICE_PACKAGE = BASE_PACKAGE_NAME + ".service";

    public static String SERVICE_IMPL_PACKAGE = BASE_PACKAGE_NAME + ".service.impl";

    public static String CONTROLLER_PACKAGE = BASE_PACKAGE_NAME + ".controller";

    public static String OUT_PUT_DIR = "code_generation/";

    private static DbOperator operator = null;

    public static void main(String[] args) throws IOException, SQLException {
        operator = new PgsqlOperator(DB_URL, DB_USER, DB_PASSWORD);
        String tables = DB_TABLE_NAME;
        String[] tableArray = tables.split(",");
        for (String string : tableArray) {
            DB_TABLE_NAME = string;
            File f = new File(OUT_PUT_DIR);
            if (!f.exists()) {
                f.mkdirs();
            }
            String tableComment;
            tableComment = operator.getTableComment(DB_TABLE_NAME);
            String entityName = DB_TABLE_NAME;
            if (entityName.startsWith(DB_TABLE_NAME_PREFIX)) {
                // 消除前缀
                entityName = entityName.substring(DB_TABLE_NAME_PREFIX.length());
            }
            entityName = StringUtils.capitalize(entityName.toLowerCase());
            entityName = formatName(entityName);

            List<EntityItem> columns;
            columns = operator.getColumns(DB_NAME, DB_TABLE_NAME);
            System.out.println("Entity 名称:" + entityName);
            EntityCreate.createEntity(entityName, tableComment, columns);
        }
    }

    /**
     * 将包含下划线的名称，去掉下划线，大写
     *
     * @param entityName
     * @return
     */
    public static String formatName(String entityName) {
        if (entityName.contains("_")) {
            // 取消下划线，并且，字母大写
            String[] names = entityName.split("_");
            StringBuilder nameBuilder = new StringBuilder(names[0]);
            for (int i = 1; i < names.length; i++) {
                nameBuilder.append(StringUtils.capitalize(names[i]));
            }
            entityName = nameBuilder.toString();
            return entityName;
        }
        return entityName;
    }
}
