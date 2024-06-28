package com.landleaf.bms.api;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.api.dto.DictDataResponse;
import com.landleaf.bms.api.dto.DictUsedRecordEditRequest;
import com.landleaf.bms.api.dto.DictUsedRecordRequest;
import com.landleaf.bms.dal.mapper.DictDataMapper;
import com.landleaf.bms.dal.mapper.DictTypeMapper;
import com.landleaf.bms.dal.redis.DictRedisDAO;
import com.landleaf.bms.domain.entity.DictDataEntity;
import com.landleaf.bms.domain.entity.DictTypeEntity;
import com.landleaf.bms.domain.enums.DictTypeEnum;
import com.landleaf.bms.domain.enums.TenantConstants;
import com.landleaf.bms.domain.response.DictDataSelectiveResponse;
import com.landleaf.bms.service.DictService;
import com.landleaf.bms.service.DictUsedRecordService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Feign 服务 - 数据字典
 *
 * @author 张力方
 * @since 2023/6/16
 **/
@RestController
@RequiredArgsConstructor
public class DictApiImpl implements DictApi {
    private final DictUsedRecordService dictUsedRecordService;
    private final DictTypeMapper dictTypeMapper;
    private final DictDataMapper dictDataMapper;
    private final DictService dictService;
    private final DictRedisDAO dictRedisDAO;

    /**
     * 新增数据字典使用记录
     *
     * @param request 数据字典使用记录
     */
    @Override
    public Response<Void> addDictDataUsedRecord(DictUsedRecordRequest request) {
        dictUsedRecordService.addUsedRecord(request);
        return Response.success();
    }

    /**
     * 编辑数据字典使用记录
     *
     * @param request 数据字典使用记录
     */
    @Override
    public Response<Void> editDictDataUsedRecord(DictUsedRecordEditRequest request) {
        dictUsedRecordService.editUsedRecord(request);
        return Response.success();
    }

    /**
     * 删除数据字典使用记录
     *
     * @param request 数据字典使用记录
     */
    @Override
    public Response<Void> deleteDictDataUsedRecord(DictUsedRecordRequest request) {
        dictUsedRecordService.deleteUsedRecord(request);
        return Response.success();
    }

    /**
     * 初始化租户数据字典
     *
     * @param tenantId 新租户id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> initTenantDictData(Long tenantId) {
        TenantContext.setIgnore(true);
        List<DictTypeEntity> dictTypeEntities = dictTypeMapper.selectList(Wrappers.<DictTypeEntity>lambdaQuery()
                .eq(TenantBaseEntity::getTenantId, TenantConstants.PLATFORM_ID)
                .eq(DictTypeEntity::getType, DictTypeEnum.TENANT.getType())
        );
        if (CollectionUtils.isEmpty(dictTypeEntities)) {
            return Response.success();
        }
        List<Long> dictIds = dictTypeEntities.stream().map(DictTypeEntity::getId).toList();
        List<DictDataEntity> dictDataEntities = dictDataMapper.selectList(Wrappers.<DictDataEntity>lambdaQuery()
                .in(DictDataEntity::getDictId, dictIds)
                .eq(TenantBaseEntity::getTenantId, TenantConstants.PLATFORM_ID));
        for (DictTypeEntity dictTypeEntity : dictTypeEntities) {
            DictTypeEntity newDictTypeEntity = new DictTypeEntity();
            newDictTypeEntity.setCode(dictTypeEntity.getCode());
            newDictTypeEntity.setName(dictTypeEntity.getName());
            newDictTypeEntity.setType(dictTypeEntity.getType());
            newDictTypeEntity.setDescription(dictTypeEntity.getDescription());
            newDictTypeEntity.setTenantId(tenantId);
            dictTypeMapper.insert(newDictTypeEntity);
            Long dictId = newDictTypeEntity.getId();
            String dictCode = newDictTypeEntity.getCode();
            dictDataEntities.stream().filter(item -> item.getDictId().equals(dictTypeEntity.getId()))
                    .forEach(item -> {
                        DictDataEntity dictDataEntity = new DictDataEntity();
                        dictDataEntity.setTenantId(tenantId);
                        dictDataEntity.setDictId(dictId);
                        dictDataEntity.setDictCode(dictCode);
                        dictDataEntity.setSort(item.getSort());
                        dictDataEntity.setLabel(item.getLabel());
                        dictDataEntity.setValue(item.getValue());
                        dictDataEntity.setStatus(item.getStatus());
                        dictDataEntity.setIsDefault(item.getIsDefault());
                        dictDataMapper.insert(dictDataEntity);
                    });
        }
        dictRedisDAO.reloadDictRedisCache();
        return Response.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Response<Void> deleteTenantDictData(Long tenantId) {
        TenantContext.setIgnore(true);
        dictTypeMapper.delete(Wrappers.<DictTypeEntity>lambdaQuery().eq(TenantBaseEntity::getTenantId, tenantId));
        dictDataMapper.delete(Wrappers.<DictDataEntity>lambdaQuery().eq(TenantBaseEntity::getTenantId, tenantId));
        return Response.success();
    }

    @Override
    public Response<List<DictDataResponse>> getDictDataList(String code) {
        List<DictDataSelectiveResponse> dictDataSelectiveResponses = dictService.selectDictDataSelective(code);
        List<DictDataResponse> dictDataResponses = BeanUtil.copyToList(dictDataSelectiveResponses, DictDataResponse.class);
        return Response.success(dictDataResponses);
    }
}
