package com.landleaf.bms.service.impl;

import cn.hutool.core.lang.Assert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import com.landleaf.bms.domain.response.OperateLogResponse;
import com.landleaf.bms.service.DictService;
import com.landleaf.bms.service.DictUsedRecordService;
import com.landleaf.bms.service.OperateLogService;
import com.landleaf.comm.base.pojo.Response;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.sta.util.DateUtils;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.oauth.api.TenantApi;
import com.landleaf.oauth.api.dto.TenantInfoResponse;
import com.landleaf.operatelog.core.dal.OperateLogEntity;
import com.landleaf.operatelog.core.dal.OperateLogMapper;
import com.landleaf.pgsql.base.TenantBaseEntity;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.landleaf.bms.domain.enums.ErrorCodeConstants.*;

/**
 * OperateLogServiceImpl
 *
 * @author xushibai
 * @since 2024/4/25
 **/
@Service
@RequiredArgsConstructor
public class OperateLogServiceImpl implements OperateLogService {
    private final OperateLogMapper operateLogMapper;
    @Override
    public IPage<OperateLogResponse> list(OperateLogQueryRequest request) {

        Page<OperateLogEntity> page = new Page<>(request.getPageNo(), request.getPageSize());
        LambdaQueryWrapper<OperateLogEntity> lw = new LambdaQueryWrapper<>();
        lw.eq(OperateLogEntity::getUserId,request.getUserid());
        if(StringUtils.isNotBlank(request.getTimeBegin()) && StringUtils.isNotBlank(request.getTimeEnd())){
            lw.ge(OperateLogEntity::getCreateTime,request.getTimeBegin());
            LocalDate ld = LocalDate.parse(request.getTimeEnd(),DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            lw.le(OperateLogEntity::getCreateTime,ld.plusDays(1L));
        }else{
            //默认查询一个月
            LocalDate dateNow = LocalDate.now();
            LocalDate dateBegin  = dateNow.minusMonths(1L).minusDays(1L);
            lw.ge(OperateLogEntity::getCreateTime,dateBegin);
        }
        lw.orderByDesc(OperateLogEntity::getCreateTime);
        IPage<OperateLogEntity> result = operateLogMapper.selectPage(page,lw);
        List<OperateLogResponse> records = result.getRecords().stream().map(entity -> new OperateLogResponse(entity.getName(), DateUtils.date2Str(entity.getCreateTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))).collect(Collectors.toList());
        IPage<OperateLogResponse> responseIPage = new Page<>();
        responseIPage.setTotal(result.getTotal());
        responseIPage.setRecords(records);
        responseIPage.setCurrent(result.getCurrent());
        responseIPage.setTotal(result.getTotal());
        responseIPage.setSize(result.getSize());
        return responseIPage;
    }
}
