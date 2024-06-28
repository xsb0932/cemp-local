package com.landleaf.bms.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.bms.dal.mapper.DictUsedRecordMapper;
import com.landleaf.bms.domain.entity.DictUsedRecordEntity;
import com.landleaf.bms.api.dto.DictUsedRecordEditRequest;
import com.landleaf.bms.api.dto.DictUsedRecordRequest;
import com.landleaf.bms.service.DictUsedRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * DictUsedRecordServiceImpl
 *
 * @author 张力方
 * @since 2023/6/16
 **/
@Service
@RequiredArgsConstructor
public class DictUsedRecordServiceImpl implements DictUsedRecordService {
    private final DictUsedRecordMapper dictUsedRecordMapper;

    @Override
    public boolean inUsed(Long dictDataId) {
        List<DictUsedRecordEntity> dictUsedRecordEntities = dictUsedRecordMapper.selectList(Wrappers.
                <DictUsedRecordEntity>lambdaQuery()
                .eq(DictUsedRecordEntity::getDictDataId, dictDataId));
        return !CollectionUtils.isEmpty(dictUsedRecordEntities);
    }

    @Override
    public void addUsedRecord(DictUsedRecordRequest request) {
        DictUsedRecordEntity dictUsedRecordEntity = new DictUsedRecordEntity();
        dictUsedRecordEntity.setDictDataId(request.getDictDataId());
        dictUsedRecordEntity.setUniqueCode(request.getUniqueCode());
        dictUsedRecordMapper.insert(dictUsedRecordEntity);
    }

    @Override
    public void editUsedRecord(DictUsedRecordEditRequest request) {
        DictUsedRecordEntity dictUsedRecordEntity = dictUsedRecordMapper.selectOne(Wrappers
                .<DictUsedRecordEntity>lambdaQuery()
                .eq(DictUsedRecordEntity::getDictDataId, request.getOldDictDataId())
                .eq(DictUsedRecordEntity::getUniqueCode, request.getUniqueCode()));
        dictUsedRecordEntity.setDictDataId(request.getNewDictDataId());
        dictUsedRecordMapper.updateById(dictUsedRecordEntity);
    }

    @Override
    public void deleteUsedRecord(DictUsedRecordRequest request) {
        dictUsedRecordMapper.delete(Wrappers
                .<DictUsedRecordEntity>lambdaQuery()
                .eq(DictUsedRecordEntity::getDictDataId, request.getDictDataId())
                .eq(DictUsedRecordEntity::getUniqueCode, request.getUniqueCode()));
    }
}
