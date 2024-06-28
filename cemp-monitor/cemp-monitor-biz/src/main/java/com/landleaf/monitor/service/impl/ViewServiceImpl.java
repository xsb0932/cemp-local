package com.landleaf.monitor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.api.DictApi;
import com.landleaf.bms.api.UserProjectApi;
import com.landleaf.bms.api.dto.DictDataResponse;
import com.landleaf.bms.api.dto.DictUsedRecordRequest;
import com.landleaf.bms.api.dto.UserProjectDTO;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.exception.ServiceException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.monitor.dal.mapper.ViewMapper;
import com.landleaf.monitor.domain.entity.ViewEntity;
import com.landleaf.monitor.domain.request.ViewDuplicateRequest;
import com.landleaf.monitor.domain.request.ViewPageRequest;
import com.landleaf.monitor.domain.request.ViewSaveRequest;
import com.landleaf.monitor.domain.request.ViewUpdateRequest;
import com.landleaf.monitor.domain.response.ViewInfoResponse;
import com.landleaf.monitor.domain.response.ViewPageResponse;
import com.landleaf.monitor.service.ViewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.landleaf.monitor.domain.enums.MonitorErrorCodeConstants.*;

/**
 * 视图的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-20
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ViewServiceImpl extends ServiceImpl<ViewMapper, ViewEntity> implements ViewService {
    private static final String AVUE_CREATE_REQUEST_BODY = "{\"visual\":{\"password\":\"\",\"category\":\"{VIEW_CATEGORY}\",\"title\":\"{VIEW_NAME}\"},\"config\":{\"detail\":\"{\\\"width\\\":\\\"1920\\\",\\\"height\\\":\\\"1080\\\",\\\"name\\\":\\\"{VIEW_NAME}\\\"}\"}}";
    private static final String VIEW_CATEGORY = "VIEW_CATEGORY";
    private static final String VIEW_NAME = "VIEW_NAME";
    private final DictApi dictApi;
    @Value("${cemp.avue.createUrl}")
    private String createUrl;
    @Value("${cemp.avue.duplicateUrl}")
    private String duplicateUrl;
    @Value("${cemp.avue.buildUrl}")
    private String buildUrl;
    @Value("${cemp.avue.updateUrl}")
    private String updateUrl;
    @Value("${cemp.avue.viewUrl}")
    private String viewUrl;
    @Value("${cemp.avue.category}")
    private String category;
    private final UserProjectApi userProjectApi;

    @Override
    public List<ViewEntity> tab(List<String> bizProjectIds) {
        return this.list(
                new LambdaQueryWrapper<ViewEntity>()
                        .in(ViewEntity::getBizProjectId, bizProjectIds)
                        // 2023-08-18 09:38会议PD增加type为0
                        .eq(ViewEntity::getViewType, 1)
                        .eq(ViewEntity::getStatus, 1)
                        .orderByAsc(ViewEntity::getSort)
        );
    }

    @Override
    public IPage<ViewPageResponse> selectPage(ViewPageRequest request) {
        List<String> bizProjectIds = userProjectApi.getUserProjectList(LoginUserUtil.getLoginUserId())
                .getCheckedData()
                .stream()
                .map(UserProjectDTO::getBizProjectId)
                .toList();
        if (CollectionUtil.isEmpty(bizProjectIds)) {
            return Page.of(request.getPageNo(), request.getPageSize());
        }
        IPage<ViewPageResponse> page = baseMapper.selectPage(new Page<>(request.getPageNo(), request.getPageSize()), request.getProjectName(), request.getViewName(), bizProjectIds);
        Map<String, String> viewTypeDicMap = dictApi.getDictDataList("VIEW_TYPE").getCheckedData().stream().collect(Collectors.toMap(DictDataResponse::getValue, DictDataResponse::getLabel));
        page.getRecords().forEach(o -> o.setTypeName(viewTypeDicMap.get(String.valueOf(o.getViewType()))));
        return page;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(ViewSaveRequest request) {
        ViewEntity entity = new ViewEntity();
        BeanUtil.copyProperties(request, entity);
        // 默认 未发布 avue视图
        entity.setStatus(0).setCustomType(0);
        // avue创建视图
        try {
            HttpResponse response = HttpUtil.createPost(createUrl)
                    .body(StrUtil.format(AVUE_CREATE_REQUEST_BODY, getFormatMap(entity.getName())))
                    // 1min超时
                    .timeout(6000)
                    .execute();
            if (response.isOk()) {
                JSONObject jsonObject = JSONUtil.parseObj(response.body());
                if (jsonObject.getBool("success")) {
                    String id = jsonObject.getJSONObject("data").getStr("id");
                    entity.setUrl(buildUrl + id).setViewUrl(viewUrl + id);
                }
            }
        } catch (Exception e) {
            log.error("调用avue接口异常", e);
        }
        if (StrUtil.isBlank(entity.getUrl()) || StrUtil.isBlank(entity.getViewUrl())) {
            throw new ServiceException(AVUE_CREATE_ERROR);
        }
        boolean save = this.save(entity);
        if (!save) {
            throw new ServiceException(VIEW_CREATE_ERROR);
        }
        // 新增字典使用记录
        dictApi.getDictDataList("VIEW_TYPE").getCheckedData()
                .stream()
                .filter(o -> o.getLabel().equals(String.valueOf(entity.getViewType())))
                .findAny()
                .ifPresent(dicData -> dictApi.addDictDataUsedRecord(new DictUsedRecordRequest().setDictDataId(dicData.getId()).setUniqueCode("tb_view" + entity.getId())));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ViewUpdateRequest request) {
        ViewEntity viewEntity = new ViewEntity().setId(request.getId()).setName(request.getName()).setSort(request.getSort());
        boolean update = this.updateById(viewEntity);
        if (!update) {
            throw new ServiceException(VIEW_UPDATE_ERROR);
        }
    }

    @Override
    public void duplicate(ViewDuplicateRequest request) {
        ViewEntity targetView = baseMapper.selectById(request.getId());
        if (null == targetView) {
            throw new BusinessException("复制的视图不存在");
        }
        if (0 != targetView.getCustomType()) {
            throw new BusinessException("不可复制非avue视图");
        }
        ViewEntity entity = new ViewEntity();
        // 页面可修改参数
        entity.setName(request.getName()).setSort(request.getSort());
        // 复制过来的参数，默认未发布状态
        entity.setStatus(0)
                .setCustomType(targetView.getCustomType())
                .setBizProjectId(targetView.getBizProjectId())
                .setViewType(targetView.getViewType());
        // avue复制视图
        String targetViewUrl = targetView.getViewUrl();
        if (StrUtil.isBlank(targetViewUrl)) {
            throw new BusinessException("复制视图参数异常");
        }
        String[] split = targetViewUrl.split("/");
        String targetId = split[split.length - 1];
        try {
            HttpResponse response = HttpUtil.createPost(duplicateUrl)
                    .form("id", targetId)
                    // 1min超时
                    .timeout(6000)
                    .execute();
            if (response.isOk()) {
                JSONObject jsonObject = JSONUtil.parseObj(response.body());
                if (jsonObject.getBool("success")) {
                    String id = jsonObject.getStr("data");
                    entity.setUrl(buildUrl + id).setViewUrl(viewUrl + id);
                    // avue修改视图名称
                    JSONObject payload = new JSONObject();
                    JSONObject visual = new JSONObject();
                    visual.set("id", id)
                            .set("password", "")
                            .set("category", category)
                            .set("status", 1)
                            .set("title", request.getName());
                    payload.set("visual", visual);
                    HttpResponse response2 = HttpUtil.createPost(updateUrl)
                            .body(payload.toString())
                            // 1min超时
                            .timeout(6000)
                            .execute();
                    if (!response2.isOk() || !JSONUtil.parseObj(response2.body()).getBool("success")) {
                        entity.setViewUrl(null);
                    }
                }
            }
        } catch (Exception e) {
            log.error("调用avue接口异常", e);
        }
        if (StrUtil.isBlank(entity.getUrl()) || StrUtil.isBlank(entity.getViewUrl())) {
            throw new ServiceException(AVUE_CREATE_ERROR);
        }
        boolean save = this.save(entity);
        if (!save) {
            throw new ServiceException(VIEW_CREATE_ERROR);
        }
        // 新增字典使用记录
        dictApi.getDictDataList("VIEW_TYPE").getCheckedData()
                .stream()
                .filter(o -> o.getLabel().equals(String.valueOf(entity.getViewType())))
                .findAny()
                .ifPresent(dicData -> dictApi.addDictDataUsedRecord(new DictUsedRecordRequest().setDictDataId(dicData.getId()).setUniqueCode("tb_view" + entity.getId())));
    }

    @Override
    public ViewInfoResponse info(Long viewId) {
        ViewInfoResponse info = baseMapper.selectInfo(viewId);
        if (null != info) {
            Map<String, String> viewTypeDicMap = dictApi.getDictDataList("VIEW_TYPE").getCheckedData().stream().collect(Collectors.toMap(DictDataResponse::getValue, DictDataResponse::getLabel));
            info.setTypeName(viewTypeDicMap.get(String.valueOf(info.getViewType())));
        }
        return info;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long viewId) {
        int row = baseMapper.changeStatus(viewId, LoginUserUtil.getLoginUserId());
        if (row <= 0) {
            throw new ServiceException(VIEW_PUBLISH_ERROR);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long viewId) {
        ViewEntity entity = getById(viewId);
        if (null == entity) {
            throw new ServiceException(VIEW_NOT_EXISTS);
        }
        boolean flag = this.removeById(viewId);
        if (!flag) {
            throw new ServiceException(VIEW_DELETE_ERROR);
        }
        // 移除字典使用记录
        dictApi.getDictDataList("VIEW_TYPE").getCheckedData()
                .stream()
                .filter(o -> o.getLabel().equals(String.valueOf(entity.getViewType())))
                .findAny()
                .ifPresent(dicData -> dictApi.deleteDictDataUsedRecord(new DictUsedRecordRequest().setDictDataId(dicData.getId()).setUniqueCode("tb_view" + entity.getId())));
    }

    @Override
    public String getBizProjectIdByAVueViewId(String viewId) {
        TenantContext.setIgnore(true);
        try {
            ViewEntity viewEntity = baseMapper.selectOne(new LambdaQueryWrapper<ViewEntity>().like(ViewEntity::getUrl, viewId));
            if (null != viewEntity) {
                return viewEntity.getBizProjectId();
            }
            return null;
        } finally {
            TenantContext.setIgnore(false);
        }
    }

    private Map<String, String> getFormatMap(String viewName) {
        Map<String, String> map = new HashMap<>(2);
        map.put(VIEW_NAME, viewName);
        map.put(VIEW_CATEGORY, category);
        return map;
    }
}