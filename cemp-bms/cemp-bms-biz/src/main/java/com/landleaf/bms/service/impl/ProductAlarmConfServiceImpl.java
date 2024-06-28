package com.landleaf.bms.service.impl;

import cn.hutool.core.lang.Assert;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.dal.mapper.ProductAlarmConfMapper;
import com.landleaf.bms.dal.mapper.ProductMapper;
import com.landleaf.bms.domain.entity.ProductAlarmConfEntity;
import com.landleaf.bms.domain.entity.ProductEntity;
import com.landleaf.bms.api.enums.AlarmConfirmTypeEnum;
import com.landleaf.bms.api.enums.AlarmLevelEnum;
import com.landleaf.bms.domain.enums.AlarmTypeEnum;
import com.landleaf.bms.domain.request.ProductAlarmConfAddRequest;
import com.landleaf.bms.domain.request.ProductAlarmConfCodeUniqueRequest;
import com.landleaf.bms.domain.request.ProductAlarmConfEditRequest;
import com.landleaf.bms.domain.request.ProductAlarmConfQueryRequest;
import com.landleaf.bms.domain.response.AlarmConfOfExcel;
import com.landleaf.bms.api.dto.ProductAlarmConfListResponse;
import com.landleaf.bms.service.ProductAlarmConfService;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.*;

/**
 * ProductAlarmConfServiceImpl
 *
 * @author 张力方
 * @since 2023/8/11
 **/
@Service
@RequiredArgsConstructor
public class ProductAlarmConfServiceImpl implements ProductAlarmConfService {
    private final ProductMapper productMapper;
    private final ProductAlarmConfMapper productAlarmConfMapper;

    @Override
    public void addAlarmConf(ProductAlarmConfAddRequest request) {
        TenantContext.setIgnore(true);
        // 已发布的产品不能修改告警配置
        ProductEntity productEntity = productMapper.selectById(request.getProductId());
        if (productEntity.getStatus().equals(0)) {
            throw new ServiceException(PRODUCT_EDIT_FORBID);
        }
        ProductAlarmConfEntity productAlarmConfEntity = new ProductAlarmConfEntity();
        BeanUtils.copyProperties(request, productAlarmConfEntity);
        String alarmCode = productAlarmConfEntity.getAlarmCode();
        Long productId = productAlarmConfEntity.getProductId();
        Assert.isTrue(isUniqueCode(null, alarmCode, productId),
                () -> new ServiceException(ALARM_CODE_NOT_UNIQUE));
        productAlarmConfEntity.setIsDefault(Boolean.FALSE);
        productAlarmConfEntity.setTenantId(productEntity.getTenantId());
        productAlarmConfMapper.insert(productAlarmConfEntity);
    }

    @Override
    public void editAlarmConf(ProductAlarmConfEditRequest request) {
        TenantContext.setIgnore(true);
        // 已发布的产品不能修改告警配置
        ProductEntity productEntity = productMapper.selectById(request.getProductId());
        if (productEntity.getStatus().equals(0)) {
            throw new ServiceException(PRODUCT_EDIT_FORBID);
        }
        ProductAlarmConfEntity productAlarmConfEntity = productAlarmConfMapper.selectById(request.getId());
        BeanUtils.copyProperties(request, productAlarmConfEntity);
        productAlarmConfMapper.updateById(productAlarmConfEntity);
    }

    @Override
    public void deleteAlarmConf(Long id) {
        TenantContext.setIgnore(true);
        ProductAlarmConfEntity productAlarmConfEntity = productAlarmConfMapper.selectById(id);
        // 已发布的产品不能修改告警配置
        ProductEntity productEntity = productMapper.selectById(productAlarmConfEntity.getProductId());
        if (productEntity.getStatus().equals(0)) {
            throw new ServiceException(PRODUCT_EDIT_FORBID);
        }
        Assert.isFalse(productAlarmConfEntity.getIsDefault(),
                () -> new ServiceException(ALARM_CODE_DELETE_NOT_PERMISSION));
        productAlarmConfMapper.deleteById(id);
    }

    @Override
    public Page<ProductAlarmConfListResponse> pageQuery(ProductAlarmConfQueryRequest request) {
        TenantContext.setIgnore(true);
        Page<ProductAlarmConfListResponse> productAlarmConfListResponsePage = productAlarmConfMapper.pageQuery(
                Page.of(request.getPageNo(), request.getPageSize()), request);
        List<ProductAlarmConfListResponse> records = productAlarmConfListResponsePage.getRecords();
        for (ProductAlarmConfListResponse productAlarmConfListResponse : records) {
            productAlarmConfListResponse.setAlarmTypeName(AlarmTypeEnum.getName(productAlarmConfListResponse.getAlarmType()));
            productAlarmConfListResponse.setAlarmRelapseLevelName(AlarmLevelEnum.getName(productAlarmConfListResponse.getAlarmRelapseLevel()));
            productAlarmConfListResponse.setAlarmConfirmTypeName(AlarmConfirmTypeEnum.getName(productAlarmConfListResponse.getAlarmConfirmType()));
            productAlarmConfListResponse.setAlarmTriggerLevelName(AlarmLevelEnum.getName(productAlarmConfListResponse.getAlarmTriggerLevel()));
        }
        return productAlarmConfListResponsePage;
    }

    @Override
    public boolean checkCodeUnique(ProductAlarmConfCodeUniqueRequest request) {
        TenantContext.setIgnore(true);
        return isUniqueCode(request.getId(), request.getCode(), request.getProductId());
    }

    @Override
    public List<String> importFile(MultipartFile file, Long productId) throws IOException {
        TenantContext.setIgnore(true);
        ExcelReader reader = ExcelUtil.getReader(file.getInputStream());

        reader.addHeaderAlias("告警码", "alarmCode");
        reader.addHeaderAlias("告警类型", "alarmType");
        reader.addHeaderAlias("告警描述", "alarmDesc");
        reader.addHeaderAlias("触发等级", "alarmTriggerLevel");
        reader.addHeaderAlias("复归等级", "alarmRelapseLevel");
        reader.addHeaderAlias("确认方式", "alarmConfirmType");

        List<AlarmConfOfExcel> list = reader.readAll(AlarmConfOfExcel.class);
        List<String> errMsgList;
        List<ProductAlarmConfEntity> alarmConfEntities = new ArrayList<>();

        // 校验数据合法性
        errMsgList = checkValidData(list, productId);
        if (!CollectionUtils.isEmpty(errMsgList)) {
            return errMsgList;
        }
        // 组装告警实体类
        for (AlarmConfOfExcel alarmConfOfExcel : list) {
            ProductAlarmConfEntity productAlarmConfEntity = new ProductAlarmConfEntity();
            productAlarmConfEntity.setProductId(productId);
            productAlarmConfEntity.setIsDefault(false);
            productAlarmConfEntity.setAlarmCode(alarmConfOfExcel.getAlarmCode());
            productAlarmConfEntity.setAlarmType(alarmConfOfExcel.getAlarmType());
            productAlarmConfEntity.setAlarmTriggerLevel(alarmConfOfExcel.getAlarmTriggerLevel());
            productAlarmConfEntity.setAlarmRelapseLevel(alarmConfOfExcel.getAlarmRelapseLevel());
            productAlarmConfEntity.setAlarmConfirmType(alarmConfOfExcel.getAlarmConfirmType());
            productAlarmConfEntity.setAlarmDesc(alarmConfOfExcel.getAlarmDesc());
            alarmConfEntities.add(productAlarmConfEntity);
        }
        productAlarmConfMapper.insertBatchSomeColumn(alarmConfEntities);
        return errMsgList;
    }

    private List<String> checkValidData(List<AlarmConfOfExcel> list, Long productId) {
        List<String> errMsg = new ArrayList<>();
        Map<String, List<AlarmConfOfExcel>> alarmCodes = list.stream().collect(Collectors.groupingBy(AlarmConfOfExcel::getAlarmCode));
        alarmCodes.forEach((s, alarmConfOfExcels) -> {
            if (alarmConfOfExcels.size() > 1) {
                errMsg.add(String.format("%s:有重复的产品告警码", s));
            }
        });
        for (AlarmConfOfExcel alarmConfOfExcel : list) {
            // 校验告警码
            String alarmCode = alarmConfOfExcel.getAlarmCode();
            boolean uniqueCode = isUniqueCode(null, alarmCode, productId);
            if (!uniqueCode) {
                errMsg.add(String.format("%s:有重复的设备名称", alarmCode));
            }
            // 校验字典
            String alarmType = alarmConfOfExcel.getAlarmType();
            AlarmTypeEnum alarmTypeEnum = AlarmTypeEnum.fromName(alarmType);
            if (alarmTypeEnum == null) {
                errMsg.add(String.format("%s:非法的告警类型", alarmType));
            } else {
                alarmConfOfExcel.setAlarmType(alarmTypeEnum.getCode());
            }
            String alarmTriggerLevel = alarmConfOfExcel.getAlarmTriggerLevel();
            AlarmLevelEnum alarmTriggerLevelEnum = AlarmLevelEnum.fromName(alarmTriggerLevel);
            if (alarmTriggerLevelEnum == null) {
                errMsg.add(String.format("%s:非法的告警触发类型", alarmTriggerLevel));
            } else {
                alarmConfOfExcel.setAlarmTriggerLevel(alarmTriggerLevelEnum.getCode());
            }
            String alarmRelapseLevel = alarmConfOfExcel.getAlarmRelapseLevel();
            AlarmLevelEnum alarmRelapseLevelEnum = AlarmLevelEnum.fromName(alarmRelapseLevel);
            if (alarmRelapseLevelEnum == null) {
                errMsg.add(String.format("%s:非法的告警复归类型", alarmRelapseLevel));
            } else {
                alarmConfOfExcel.setAlarmRelapseLevel(alarmRelapseLevelEnum.getCode());
            }
            String alarmConfirmType = alarmConfOfExcel.getAlarmConfirmType();
            if(StringUtils.isNotBlank(alarmConfirmType)){
                AlarmConfirmTypeEnum alarmConfirmTypeEnum = AlarmConfirmTypeEnum.fromName(alarmConfirmType);
                if (alarmConfirmTypeEnum == null) {
                    errMsg.add(String.format("%s:非法的告警确认类型", alarmConfirmType));
                } else {
                    alarmConfOfExcel.setAlarmConfirmType(alarmConfirmTypeEnum.getCode());
                }
            }
        }
        return errMsg;
    }

    private boolean isUniqueCode(Long id, String code, Long productId) {
        if (id == null) {
            return !productAlarmConfMapper.exists(
                    Wrappers.<ProductAlarmConfEntity>lambdaQuery()
                            .eq(ProductAlarmConfEntity::getProductId, productId)
                            .eq(ProductAlarmConfEntity::getAlarmCode, code)
            );
        } else {
            return !productAlarmConfMapper.exists(
                    Wrappers.<ProductAlarmConfEntity>lambdaQuery()
                            .eq(ProductAlarmConfEntity::getProductId, productId)
                            .eq(ProductAlarmConfEntity::getAlarmCode, code)
                            .ne(ProductAlarmConfEntity::getId, id)
            );
        }
    }
}
