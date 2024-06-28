package com.landleaf.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.dal.mapper.DictDataMapper;
import com.landleaf.bms.dal.mapper.DictTypeMapper;
import com.landleaf.bms.dal.redis.DictRedisDAO;
import com.landleaf.bms.domain.entity.DictDataEntity;
import com.landleaf.bms.domain.entity.DictTypeEntity;
import com.landleaf.bms.domain.enums.DictDefaultStatusEnum;
import com.landleaf.bms.domain.enums.DictTypeEnum;
import com.landleaf.bms.domain.enums.TenantConstants;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.domain.response.DictDataSelectiveResponse;
import com.landleaf.bms.domain.response.DictDetailsResponse;
import com.landleaf.bms.domain.response.DictTypeListResponse;
import com.landleaf.bms.service.DictService;
import com.landleaf.bms.service.DictUsedRecordService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.dto.TenantInfoResponse;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.*;

/**
 * DictServiceImpl
 *
 * @author 张力方
 * @since 2023/6/15
 **/
@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {
    private final DictTypeMapper dictTypeMapper;
    private final DictDataMapper dictDataMapper;
    private final TenantApi tenantApi;
    private final DictUsedRecordService dictUsedRecordService;
    private final DictRedisDAO dictRedisDAO;

    @Override
    public List<DictTypeListResponse> getDictTypeListResponse(DictTypeListRequest request) {
        TenantContext.setIgnore(true);
        List<DictTypeListResponse> dictTypeListResponseList = new ArrayList<>();

        Long loginTenantId = LoginUserUtil.getLoginTenantId();
        boolean isPlatform = TenantConstants.PLATFORM_ID.equals(loginTenantId);
        // 平台用户返回系统+全部租户数据字典
        if (isPlatform) {
            // 系统字典
            List<DictTypeEntity> systemDictTypes = dictTypeMapper.searchSystemDictTypes(request);
            if (!CollectionUtils.isEmpty(systemDictTypes)) {
                DictTypeListResponse systemDictTypeListResponse = new DictTypeListResponse();
                systemDictTypeListResponse.setDictType(DictTypeEnum.SYSTEM.getName());
                List<DictTypeListResponse.Dict> sysytemDictList = DictTypeListResponse.Dict.covertFrom(systemDictTypes);
                systemDictTypeListResponse.setDictList(sysytemDictList);
                dictTypeListResponseList.add(systemDictTypeListResponse);
            }
            List<DictTypeEntity> tenantDictTypes = dictTypeMapper.searchTenantDictTypes(null, request.getKeywords());
            LinkedHashMap<Long, List<DictTypeEntity>> tenantMap = tenantDictTypes.stream().collect(Collectors.groupingBy(TenantBaseEntity::getTenantId, LinkedHashMap::new, Collectors.toList()));
            tenantMap.forEach((tenantId, value) -> {
                Response<TenantInfoResponse> tenantInfo = tenantApi.getTenantInfo(tenantId);
                DictTypeListResponse tenantDictTypeListResponse = new DictTypeListResponse();
                String name = tenantInfo.getResult().getName();
                tenantDictTypeListResponse.setDictType(name);
                List<DictTypeListResponse.Dict> dictList = DictTypeListResponse.Dict.covertFrom(value);
                tenantDictTypeListResponse.setDictList(dictList);
                dictTypeListResponseList.add(tenantDictTypeListResponse);
            });
        } else {
            // 租户用户返回自己的数据字典
            List<DictTypeEntity> tenantDictTypes = dictTypeMapper.searchTenantDictTypes(loginTenantId, request.getKeywords());
            Response<TenantInfoResponse> tenantInfo = tenantApi.getTenantInfo(loginTenantId);
            String name = tenantInfo.getResult().getName();
            DictTypeListResponse tenantDictTypeListResponse = new DictTypeListResponse();
            tenantDictTypeListResponse.setDictType(name);
            List<DictTypeListResponse.Dict> dictList = DictTypeListResponse.Dict.covertFrom(tenantDictTypes);
            tenantDictTypeListResponse.setDictList(dictList);
            dictTypeListResponseList.add(tenantDictTypeListResponse);
        }
        return dictTypeListResponseList;
    }

    @Override
    public DictDetailsResponse getDictDetails(Long dictId) {
        TenantContext.setIgnore(true);
        DictDetailsResponse dictDetailsResponse = new DictDetailsResponse();
        DictTypeEntity dictTypeEntity = dictTypeMapper.selectById(dictId);
        if (dictTypeEntity == null) {
            throw new ServiceException(DICT_TYPE_NOT_EXIST);
        }
        dictDetailsResponse.setDictId(dictTypeEntity.getId());
        dictDetailsResponse.setTenantId(dictTypeEntity.getTenantId());
        dictDetailsResponse.setCode(dictTypeEntity.getCode());
        dictDetailsResponse.setName(dictTypeEntity.getName());
        dictDetailsResponse.setType(dictTypeEntity.getType());
        dictDetailsResponse.setDescription(dictTypeEntity.getDescription());
        List<DictDataEntity> dictDataEntities = dictDataMapper.selectList(Wrappers.<DictDataEntity>lambdaQuery()
                .eq(DictDataEntity::getDictId, dictId)
                .orderBy(true, true, DictDataEntity::getSort));
        dictDetailsResponse.setDictDataList(DictDetailsResponse.DictData.convertFrom(dictDataEntities));
        return dictDetailsResponse;
    }

    @Override
    public void modifyDictBase(DictBaseEditRequest request) {
        TenantContext.setIgnore(true);
        Long id = request.getId();
        DictTypeEntity dictTypeEntity = dictTypeMapper.selectById(id);
        if (dictTypeEntity == null) {
            throw new ServiceException(DICT_TYPE_NOT_EXIST);
        }
        dictTypeEntity.setName(request.getName());
        dictTypeEntity.setDescription(request.getDescription());
        dictTypeMapper.updateById(dictTypeEntity);
    }

    @Override
    public void addDictData(DictDataAddRequest request) {
        TenantContext.setIgnore(true);
        DictDataEntity dictDataEntity = new DictDataEntity();
        Long dictId = request.getDictId();
        // 平台可以管理所有的租户字典数据
        DictTypeEntity dictTypeEntity = dictTypeMapper.selectById(dictId);
        Long tenantId = request.getTenantId();
        if (dictTypeEntity.getType().equals(DictTypeEnum.SYSTEM.getType())) {
            tenantId = null;
        }
        if (!checkValueUnique(request.getValue(), dictId, null)) {
            throw new ServiceException(DICT_DATA_VALUE_NOT_UNIQUE);
        }
        dictDataEntity.setTenantId(tenantId);
        dictDataEntity.setDictId(dictId);
        dictDataEntity.setDictCode(dictTypeEntity.getCode());
        dictDataEntity.setStatus(request.getStatus());
        dictDataEntity.setLabel(request.getLabel());
        dictDataEntity.setValue(request.getValue());
        dictDataEntity.setIsDefault(DictDefaultStatusEnum.NOT_DEFAULT.getType());
        boolean exists = dictDataMapper.exists(Wrappers.<DictDataEntity>lambdaQuery().eq(DictDataEntity::getDictId, dictId));
        int maxSort;
        if (exists) {
            maxSort = dictDataMapper.maxSort(dictId);
        } else {
            maxSort = 0;
        }
        dictDataEntity.setSort(maxSort + 1);
        dictDataMapper.insert(dictDataEntity);
        // 处理 redis
//        dictRedisDAO.reloadDictRedisCache(dictTypeEntity.getCode());
        dictRedisDAO.reloadDictRedisCache();
    }

    @Override
    public void modifyDictData(DictDataEditRequest request) {
        TenantContext.setIgnore(true);
        Long id = request.getId();
        DictDataEntity dictDataEntity = dictDataMapper.selectById(id);
        if (dictDataEntity == null) {
            throw new ServiceException(DICT_DATA_NOT_EXIST);
        }
        // 默认字典码值只允许修改值描述
        if (dictDataEntity.getIsDefault().equals(DictDefaultStatusEnum.DEFAULT.getType())) {
            String value = dictDataEntity.getValue();
            Integer status = dictDataEntity.getStatus();
            if (!request.getValue().equals(value) || !request.getStatus().equals(status)) {
                throw new ServiceException(DICT_DATA_DEFAULT_MODIFY_FORBID);
            }
        }
        if (!checkValueUnique(request.getValue(), dictDataEntity.getDictId(), id)) {
            throw new ServiceException(DICT_DATA_VALUE_NOT_UNIQUE);
        }
        dictDataEntity.setStatus(request.getStatus());
        dictDataEntity.setLabel(request.getLabel());
        dictDataEntity.setValue(request.getValue());
        dictDataMapper.updateById(dictDataEntity);
        // 处理 redis
//        dictRedisDAO.reloadDictRedisCache(dictDataEntity.getDictCode());
        dictRedisDAO.reloadDictRedisCache();
    }

    @Override
    public void deleteDictData(Long dictDataId) {
        TenantContext.setIgnore(true);
        boolean inUsed = dictUsedRecordService.inUsed(dictDataId);
        if (inUsed) {
            throw new ServiceException(DICT_IN_USED);
        }
        DictDataEntity dictDataEntity = dictDataMapper.selectById(dictDataId);
        if (dictDataEntity == null) {
            throw new ServiceException(DICT_DATA_NOT_EXIST);
        }
        // 默认码值不允许删除
        if (dictDataEntity.getIsDefault().equals(DictDefaultStatusEnum.DEFAULT.getType())) {
            throw new ServiceException(DICT_DATA_DEFAULT_DELETE_FORBID);
        }
        dictDataMapper.deleteById(dictDataId);
        // 处理 redis
//        dictRedisDAO.reloadDictRedisCache(dictDataEntity.getDictCode());
        dictRedisDAO.reloadDictRedisCache();
    }

    @Override
    public void modifyDictDataSort(DictDataSortRequest request) {
        TenantContext.setIgnore(true);
        Long dictDataId = request.getDictDataId();
        Long nextDictDataId = request.getNextDictDataId();
        DictDataEntity dictDataEntity = dictDataMapper.selectById(dictDataId);
        Long dictId = dictDataEntity.getDictId();
        // 当前字典项全部字典数据
        List<DictDataEntity> dictDataEntities = dictDataMapper.selectList(Wrappers
                .<DictDataEntity>lambdaQuery()
                .eq(DictDataEntity::getDictId, dictId)
                .orderBy(true, true, DictDataEntity::getSort));
        dictDataEntities.removeIf(item -> item.getId().equals(dictDataId));
        if (nextDictDataId == null) {
            dictDataEntities.add(dictDataEntity);
        } else {
            DictDataEntity nextDictDataEntity = dictDataMapper.selectById(nextDictDataId);
            int nextDictDataEntityIndex = dictDataEntities.indexOf(nextDictDataEntity);
            dictDataEntities.add(nextDictDataEntityIndex, dictDataEntity);
        }
        int sort = 0;
        for (DictDataEntity dataEntity : dictDataEntities) {
            dataEntity.setSort(sort++);
            dictDataMapper.updateById(dataEntity);
        }
        // 处理 redis
//        dictRedisDAO.reloadDictRedisCache(dictDataEntity.getDictCode());
        dictRedisDAO.reloadDictRedisCache();
    }

    @Override
    public List<DictDataSelectiveResponse> selectDictDataSelective(String dictTypeCode) {
        Assert.notBlank(dictTypeCode, "参数异常");
        TenantContext.setIgnore(true);
        LambdaQueryWrapper<DictTypeEntity> queryWrapper = Wrappers.<DictTypeEntity>lambdaQuery()
                .eq(DictTypeEntity::getCode, dictTypeCode)
                .and(it -> it.eq(DictTypeEntity::getTenantId, TenantContext.getTenantId()).or().isNull(DictTypeEntity::getTenantId))
                .orderByAsc(DictTypeEntity::getTenantId);
        DictTypeEntity dictTypeEntity = dictTypeMapper.selectOne(queryWrapper.last("limit 1"));
        Assert.notNull(dictTypeEntity, "目标不存在");

        return dictRedisDAO.searchDictDataList(dictTypeEntity)
                .stream()
                .sorted(Comparator.comparing(DictDataEntity::getSort))
                .map(DictDataSelectiveResponse::from)
                .toList();
    }

    @Override
    public boolean checkValueUnique(DictDataValueUniqueRequest request) {
        return checkValueUnique(request.getValue(), request.getDictId(), request.getDictDataId());
    }

    private boolean checkValueUnique(String value, Long dictId, Long dictDataId) {
        if (dictDataId == null) {
            return !dictDataMapper.exists(Wrappers.<DictDataEntity>lambdaQuery()
                    .eq(DictDataEntity::getDictId, dictId)
                    .eq(DictDataEntity::getValue, value));
        } else {
            return !dictDataMapper.exists(Wrappers.<DictDataEntity>lambdaQuery()
                    .eq(DictDataEntity::getDictId, dictId)
                    .eq(DictDataEntity::getValue, value)
                    .ne(DictDataEntity::getId, dictDataId));
        }
    }
}
