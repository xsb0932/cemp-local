package com.landleaf.bms.service;

/**
 * 功能管理service
 *
 * @author yue lin
 * @since 2023/6/26 9:42
 */
public interface FeatureManagementService {

    /**
     * 校验标识符是否唯一
     * @param identifier    标识符
     * @param id    id（不包含），用于更新时校验
     * @return  唯一为true，不唯一为false
     */
    boolean checkIdentifierUnique(String identifier, Long id);


    /**
     * 校验标识符是否唯一
     * @param identifier    标识符
     * @return  唯一为true，不唯一为false
     */
    boolean checkIdentifierUnique(String identifier);

}
