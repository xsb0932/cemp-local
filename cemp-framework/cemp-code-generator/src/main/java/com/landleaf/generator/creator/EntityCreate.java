package com.landleaf.generator.creator;

import com.landleaf.generator.GeneratorApplication;
import com.landleaf.generator.domain.ConditionItem;
import com.landleaf.generator.domain.EntityItem;
import com.landleaf.generator.util.VelocityCreate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.velocity.VelocityContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class EntityCreate {
    public static void createEntity(String entityName, String tableDesc, List<EntityItem> columns) throws IOException {
        // 生成entity
        VelocityContext context = new VelocityContext();
        context.put("package", GeneratorApplication.ENTITY_PACKAGE);
        context.put("exceptionName", GeneratorApplication.EXCEPTION_NAME);
        context.put("basePackage", GeneratorApplication.BASE_PACKAGE_NAME);
        context.put("entityPackage", GeneratorApplication.ENTITY_PACKAGE);
        VelocityContext table = new VelocityContext();
        table.put("comment", tableDesc);
        table.put("name", GeneratorApplication.DB_TABLE_NAME);
        table.put("fields", columns);
        context.put("table", table);
        context.put("author", "hebin");
        context.put("date", DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd"));
        context.put("entity", entityName + "Entity");
        String javaFile = VelocityCreate.fill(context, "Entity.java.vm");
        VelocityCreate.writeFile(GeneratorApplication.OUT_PUT_DIR, GeneratorApplication.ENTITY_PACKAGE, entityName + "Entity.java", javaFile);

        String priKey = "get";
        String priColumn = "";
        for (EntityItem entityItem : columns) {
            if ("PRI" .equals(entityItem.getIsPriKey())) {
                priKey += StringUtils.capitalise(entityItem.getColumnNameNew()) + "()";
                priColumn = entityItem.getColumnName();
            }
        }
        context.put("getPriKey", priKey);

        // 生成add的dto
        context.put("package", GeneratorApplication.DTO_PACKAGE);
        context.put("dtoPackage", GeneratorApplication.DTO_PACKAGE);
        context.put("dto", entityName + "AddDTO");
        context.put("addDto", entityName + "AddDTO");
        javaFile = VelocityCreate.fill(context, "AddDTO.java.vm");
        VelocityCreate.writeFile(GeneratorApplication.OUT_PUT_DIR, GeneratorApplication.DTO_PACKAGE, entityName + "AddDTO.java", javaFile);

//		// 生成update的dto
//		context.put("dto", entityName + "UpdateDTO");
//		context.put("updateDto", entityName + "UpdateDTO");
//		javaFile = VelocityCreate.fill(context, "UpdateDTO.java.vm");
//		VelocityCreate.writeFile(CodeGeneration.DTO_PACKAGE, entityName + "UpdateDTO.java", javaFile);

        // 生成query的dto
        context.put("dto", entityName + "QueryDTO");
        context.put("queryDto", entityName + "QueryDTO");
        javaFile = VelocityCreate.fill(context, "QueryDTO.java.vm");
        VelocityCreate.writeFile(GeneratorApplication.OUT_PUT_DIR, GeneratorApplication.DTO_PACKAGE, entityName + "QueryDTO.java", javaFile);

        // 生成vo
        context.put("vo", entityName + "VO");
        context.put("package", GeneratorApplication.VO_PACKAGE);
        context.put("voPackage", GeneratorApplication.VO_PACKAGE);
        javaFile = VelocityCreate.fill(context, "VO.java.vm");
        VelocityCreate.writeFile(GeneratorApplication.OUT_PUT_DIR, GeneratorApplication.VO_PACKAGE, entityName + "VO.java", javaFile);

        // 生成wrapper
        context.put("wrapper", entityName + "Wrapper");
        context.put("package", GeneratorApplication.WRAPPER_PACKAGE);
        context.put("wrapperPackage", GeneratorApplication.WRAPPER_PACKAGE);
        javaFile = VelocityCreate.fill(context, "Wrapper.java.vm");
        VelocityCreate.writeFile(GeneratorApplication.OUT_PUT_DIR, GeneratorApplication.WRAPPER_PACKAGE, entityName + "Wrapper.java", javaFile);

        // 生成reposotory
        String keyType = "Integer";
        for (EntityItem entityItem : columns) {
            if (!StringUtils.isEmpty(entityItem.getIsPriKey()) && entityItem.getIsPriKey().equalsIgnoreCase("PRI")) {
                keyType = entityItem.getColumnType();
            }
        }
        context.put("repository", entityName + "Mapper");
        context.put("keyType", keyType);
        context.put("package", GeneratorApplication.REPOSITORY_PACKAGE);
        context.put("daoPackage", GeneratorApplication.REPOSITORY_PACKAGE);
        javaFile = VelocityCreate.fill(context, "Repository.java.vm");
        VelocityCreate.writeFile(GeneratorApplication.OUT_PUT_DIR, GeneratorApplication.REPOSITORY_PACKAGE, entityName + "Mapper.java", javaFile);
        String daoName = entityName + "Mapper";
        daoName = StringUtils.uncapitalize(daoName);
        context.put("daoName", daoName);

        // 生成reposotory的xml
        StringBuilder baseSqlColumn = new StringBuilder();
        for (EntityItem entityItem : columns) {
            baseSqlColumn.append(entityItem.getColumnName()).append(",");
        }
        context.put("baseSqlColumn", baseSqlColumn.substring(0, baseSqlColumn.length() - 1));
        context.put("priColumn", priColumn);
        javaFile = VelocityCreate.fill(context, "Repository.xml.vm");
        VelocityCreate.writeFile(GeneratorApplication.OUT_PUT_DIR, GeneratorApplication.REPOSITORY_PACKAGE, entityName + "Mapper.xml", javaFile);

        // 生成service
        context.put("service", entityName + "Service");
        context.put("package", GeneratorApplication.SERVICE_PACKAGE);
        context.put("servicePackage", GeneratorApplication.SERVICE_PACKAGE);
        javaFile = VelocityCreate.fill(context, "Service.java.vm");
        VelocityCreate.writeFile(GeneratorApplication.OUT_PUT_DIR, GeneratorApplication.SERVICE_PACKAGE, entityName + "Service.java", javaFile);

        // 生成serviceImpl

        context.put("serviceImpl", entityName + "ServiceImpl");
        context.put("serviceImplName", StringUtils.uncapitalize(entityName + "ServiceImpl"));
        context.put("package", GeneratorApplication.SERVICE_IMPL_PACKAGE);
        context.put("serviceImplPackage", GeneratorApplication.SERVICE_IMPL_PACKAGE);
        // 加入查询条件
        List<ConditionItem> conditionList = new ArrayList<ConditionItem>();
        for (EntityItem entityItem : columns) {
            String desc = entityItem.getColumnDesc();
            if (desc.contains(":")) {
                desc = desc.substring(0, desc.indexOf(":"));
            }
            String queryType = "equal";
            String mybatisQueryType = "eq";
            String condition = "null != queryInfo.get" + StringUtils.capitalize(entityItem.getColumnNameNew()) + "()";
            String value = null;
            if ("String" .equals(entityItem.getColumnType())) {
                queryType = "like";
                mybatisQueryType = "like";
                condition = "!StringUtils.hasText(queryInfo.get" + StringUtils.capitalize(entityItem.getColumnNameNew())
                        + "())";
                value = "\"%\" + queryInfo.get" + StringUtils.capitalize(entityItem.getColumnNameNew()) + "() + \"%\"";
            } else {
                if ("BigDecimal" .equals(entityItem.getColumnType())) {
                    value = "queryInfo.get" + StringUtils.capitalize(entityItem.getColumnNameNew())
                            + "().doubleValue()";
                } else {
                    value = "queryInfo.get" + StringUtils.capitalize(entityItem.getColumnNameNew()) + "()";
                }
            }
            conditionList.add(ConditionItem.builder().key(entityItem.getColumnNameNew())
                    .mybatisKey(StringUtils.capitalize(entityItem.getColumnNameNew())).desc(desc).value(value)
                    .condition(condition).queryType(queryType).mybatisType(mybatisQueryType).build());
        }
        context.put("conditionList", conditionList);
        javaFile = VelocityCreate.fill(context, "ServiceImpl.java.vm");
        VelocityCreate.writeFile(GeneratorApplication.OUT_PUT_DIR, GeneratorApplication.SERVICE_IMPL_PACKAGE, entityName + "ServiceImpl.java", javaFile);

        // 生成controller
        String urlPrefix = GeneratorApplication.DB_TABLE_NAME;
        if (urlPrefix.startsWith(GeneratorApplication.DB_TABLE_NAME_PREFIX)) {
            // 消除前缀
            urlPrefix = urlPrefix.substring(GeneratorApplication.DB_TABLE_NAME_PREFIX.length());
        }
        urlPrefix = urlPrefix.replaceAll("_", "-");

        context.put("controller", entityName + "Controller");
        context.put("urlPrefix", urlPrefix);
        context.put("package", GeneratorApplication.CONTROLLER_PACKAGE);
        javaFile = VelocityCreate.fill(context, "Controller.java.vm");
        VelocityCreate.writeFile(GeneratorApplication.OUT_PUT_DIR, GeneratorApplication.CONTROLLER_PACKAGE, entityName + "Controller.java", javaFile);
    }
}
