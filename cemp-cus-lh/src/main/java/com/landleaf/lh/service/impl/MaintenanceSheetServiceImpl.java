package com.landleaf.lh.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.api.ManagementNodeApi;
import com.landleaf.bms.api.UserProjectApi;
import com.landleaf.comm.exception.BusinessException;
import com.landleaf.comm.tenant.TenantContext;
import com.landleaf.comm.util.servlet.LoginUserUtil;
import com.landleaf.lh.dal.mapper.MaintenanceSheetMapper;
import com.landleaf.lh.domain.dto.MaintenanceExportDTO;
import com.landleaf.lh.domain.dto.MaintenanceImportDTO;
import com.landleaf.lh.domain.dto.MaintenanceImportRowDTO;
import com.landleaf.lh.domain.entity.MaintenanceSheetEntity;
import com.landleaf.lh.domain.enums.MaintenanceTypeEnum;
import com.landleaf.lh.domain.request.MaintenanceAddRequest;
import com.landleaf.lh.domain.request.MaintenanceEditRequest;
import com.landleaf.lh.domain.request.MaintenanceExportRequest;
import com.landleaf.lh.domain.request.MaintenancePageRequest;
import com.landleaf.lh.domain.response.MaintenanceInfoResponse;
import com.landleaf.lh.domain.response.MaintenancePageResponse;
import com.landleaf.lh.service.MaintenanceSheetService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * MaintenanceSheetEntity对象的业务逻辑接口实现
 *
 * @author hebin
 * @since 2024-05-22
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MaintenanceSheetServiceImpl extends ServiceImpl<MaintenanceSheetMapper, MaintenanceSheetEntity> implements MaintenanceSheetService {
    private final ManagementNodeApi managementNodeApi;
    private final UserProjectApi userProjectApi;
    private final static DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private boolean dataExists(String id, String bizProjectId, String room, String maintenanceDate, String content) {
        return baseMapper.exists(new LambdaQueryWrapper<MaintenanceSheetEntity>()
                .ne(StrUtil.isNotBlank(id), MaintenanceSheetEntity::getId, id)
                .eq(MaintenanceSheetEntity::getBizProjectId, bizProjectId)
                .eq(MaintenanceSheetEntity::getRoom, room)
                .eq(MaintenanceSheetEntity::getMaintenanceDate, maintenanceDate)
                .eq(MaintenanceSheetEntity::getContent, content));
    }

    private List<Map<String, Object>> readExcel(MultipartFile file) {
        try (InputStream ins = file.getInputStream()) {
            return ExcelUtil.getReader(ins).readAll();
        } catch (IOException e) {
            throw new BusinessException("读取excel异常", e);
        }
    }

    private MaintenanceImportDTO baseCheck(List<Map<String, Object>> rowList) {
        MaintenanceImportDTO dto = new MaintenanceImportDTO();
        if (CollUtil.isEmpty(rowList)) {
            dto.getErrMsg().add("未读取到excel中待导入的数据");
            return dto;
        }
        HashSet<String> sameCheckList = new HashSet<>();
        for (int i = 0; i < rowList.size(); i++) {
            boolean checkFlag = true;
            Map<String, Object> row = rowList.get(i);
            MaintenanceImportRowDTO data = new MaintenanceImportRowDTO();
            data.setTenantId(TenantContext.getTenantId());
            data.setRow(i + 1);
            Object projectName = row.get("项目名称");
            if (null != projectName && StrUtil.isNotBlank(projectName.toString())) {
                data.setProjectName(projectName.toString());
                dto.getProjectNameList().add(projectName.toString());
            } else {
                dto.getErrMsg().add("数据第" + (i + 1) + "行项目名称为空");
                checkFlag = false;
            }
            Object room = row.get("幢号-单元号-房号");
            if (null != room && StrUtil.isNotBlank(room.toString())) {
                data.setRoom(room.toString());
            } else {
                dto.getErrMsg().add("数据第" + (i + 1) + "行幢号-单元号-房号为空");
                checkFlag = false;
            }
            Object yearMonth = row.get("月份");
            if (null != yearMonth && StrUtil.isNotBlank(yearMonth.toString())) {
                try {
                    String format = yearMonth + "-01";
                    LocalDate maintenanceYearMonth = LocalDate.parse(format, DATE_FORMATTER);
                    data.setMaintenanceYearMonth(maintenanceYearMonth);
                } catch (Exception e) {
                    dto.getErrMsg().add("数据第" + (i + 1) + "行月份格式异常");
                }
            } else {
                dto.getErrMsg().add("数据第" + (i + 1) + "行月份为空");
            }
            Object date = row.get("报修日期");
            if (null != date && StrUtil.isNotBlank(date.toString())) {
                try {
                    LocalDate maintenanceDate = LocalDate.parse(date.toString(), DATE_FORMATTER);
                    data.setMaintenanceDate(maintenanceDate);
                } catch (Exception e) {
                    dto.getErrMsg().add("数据第" + (i + 1) + "行报修日期格式异常");
                    checkFlag = false;
                }
            } else {
                dto.getErrMsg().add("数据第" + (i + 1) + "行报修日期为空");
                checkFlag = false;
            }
            Object maintenanceTypeName = row.get("报修类别\n（下拉选择）");
            if (null != maintenanceTypeName && StrUtil.isNotBlank(maintenanceTypeName.toString())) {
                String maintenanceType = MaintenanceTypeEnum.nameToCode(maintenanceTypeName.toString());
                if (null == maintenanceType) {
                    dto.getErrMsg().add("数据第" + (i + 1) + "行报修类别错误");
                } else {
                    data.setMaintenanceType(maintenanceType);
                }
            } else {
                dto.getErrMsg().add("数据第" + (i + 1) + "行报修类别为空");
            }
            Object content = row.get("报修内容");
            if (null != maintenanceTypeName && StrUtil.isNotBlank(maintenanceTypeName.toString())) {
                data.setContent(content.toString());
            } else {
                data.setContent("");
            }
            if (checkFlag) {
                String sameCheck = data.getProjectName() + data.getRoom() + data.getMaintenanceDate() + data.getContent();
                if (!sameCheckList.contains(sameCheck)) {
                    sameCheckList.add(sameCheck);
                } else {
                    dto.getErrMsg().add("数据第" + (i + 1) + "行导入数据重复");
                }
            }
            dto.getDataList().add(data);
        }
        return dto;
    }

    private void dbCheck(MaintenanceImportDTO dto) {
        HashMap<String, String> projectMap = userProjectApi.getUserProjectList(LoginUserUtil.getLoginUserId()).getCheckedData()
                .stream().collect(HashMap::new, (map, item) -> map.put(item.getName(), item.getBizProjectId()), HashMap::putAll);
        dto.setProjectMap(projectMap);
        dto.getProjectNameList().forEach(projectName -> {
            if (!projectMap.containsKey(projectName)) {
                dto.getErrMsg().add("当前用户下不存在项目[" + projectName + "]");
            }
        });
        dto.getDataList().forEach(data -> {
            TenantContext.setIgnore(true);
            String bizProjectId = projectMap.get(data.getProjectName());
            data.setBizProjectId(bizProjectId);
            if (null != bizProjectId) {
                boolean exists = baseMapper.exists(new LambdaQueryWrapper<MaintenanceSheetEntity>()
                        .eq(MaintenanceSheetEntity::getBizProjectId, bizProjectId)
                        .eq(MaintenanceSheetEntity::getRoom, data.getRoom())
                        .eq(MaintenanceSheetEntity::getMaintenanceDate, data.getMaintenanceDate())
                        .eq(MaintenanceSheetEntity::getContent, data.getContent())
                );
                if (exists) {
                    dto.getErrMsg().add("数据第" + data.getRow() + "行已导入");
                }
            }
        });
    }

    @Override
    public Page<MaintenancePageResponse> selectPage(MaintenancePageRequest request) {
        Long userId = LoginUserUtil.getLoginUserId();
        List<String> bizProjectIdList = managementNodeApi.getUserProjectByNode(request.getBizNodeId(), userId).getCheckedData();
        if (bizProjectIdList.isEmpty()) {
            return Page.of(request.getPageNo(), request.getPageSize());
        }
        TenantContext.setIgnore(true);
        String yearMonthStart = request.getYearMonthStart();
        String yearMonthEnd = request.getYearMonthEnd();
        try {
            YearMonth.parse(yearMonthStart, DateTimeFormatter.ofPattern("yyyy-MM"));
            YearMonth.parse(yearMonthEnd, DateTimeFormatter.ofPattern("yyyy-MM"));
            yearMonthStart = yearMonthStart + "-01";
            yearMonthEnd = yearMonthEnd + "-01";
        } catch (Exception e) {
            log.error("时间格式异常 {} {}", yearMonthStart, yearMonthEnd, e);
            throw new BusinessException("时间格式异常");
        }
        Page<MaintenancePageResponse> result = baseMapper.maintenancePageQuery(
                Page.of(request.getPageNo(), request.getPageSize()),
                bizProjectIdList,
                request.getMaintenanceType(),
                yearMonthStart,
                yearMonthEnd
        );
        result.getRecords().forEach(o -> o.setMaintenanceTypeName(MaintenanceTypeEnum.codeToName(o.getMaintenanceType())));
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(MaintenanceAddRequest request) {
        TenantContext.setIgnore(true);
        List<String> userBizProjectIds = userProjectApi.getUserProjectBizIds(LoginUserUtil.getLoginUserId()).getCheckedData();
        if (!userBizProjectIds.contains(request.getBizProjectId())) {
            throw new BusinessException("当前用户下不存在此项目");
        }
        if (!MaintenanceTypeEnum.typeExists(request.getMaintenanceType())) {
            throw new BusinessException("报修类型不存在");
        }
        if (dataExists(null, request.getBizProjectId(), request.getRoom(), request.getMaintenanceDate(), request.getContent())) {
            throw new BusinessException("报修记录重复");
        }
        MaintenanceSheetEntity entity = request.convertToEntity();
        boolean save = this.save(entity);
        if (!save) {
            throw new BusinessException("保存失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(MaintenanceEditRequest request) {
        TenantContext.setIgnore(true);
        MaintenanceSheetEntity entity = getById(request.getId());
        if (null == entity) {
            throw new BusinessException("旧报修记录不存在");
        }
        if (!MaintenanceTypeEnum.typeExists(request.getMaintenanceType())) {
            throw new BusinessException("报修类型不存在");
        }
        if (dataExists(request.getId(), entity.getBizProjectId(), request.getRoom(), request.getMaintenanceDate(), request.getContent())) {
            throw new BusinessException("报修记录重复");
        }
        request.updateEntity(entity);
        boolean update = this.updateById(entity);
        if (!update) {
            throw new BusinessException("更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        TenantContext.setIgnore(true);
        boolean delete = removeById(id);
        if (!delete) {
            throw new BusinessException("删除失败");
        }
    }

    @Override
    public MaintenanceInfoResponse info(Long id) {
        TenantContext.setIgnore(true);
        return baseMapper.info(id);
    }

    @Override
    public void export(MaintenanceExportRequest request, HttpServletResponse response) {
        TenantContext.setIgnore(true);
        try (ServletOutputStream os = response.getOutputStream()) {
            // 设置请求头数据
            String fileName = URLUtil.encode("报修数据");
            response.setCharacterEncoding(CharsetUtil.UTF_8);
            response.setContentType("application/x-msdownload");
            response.addHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
            // 获取模板
            ExcelWriter writer = ExcelUtil.getWriter(true);
            String yearMonthStart = request.getYearMonthStart();
            String yearMonthEnd = request.getYearMonthEnd();
            try {
                YearMonth.parse(yearMonthStart, DateTimeFormatter.ofPattern("yyyy-MM"));
                YearMonth.parse(yearMonthEnd, DateTimeFormatter.ofPattern("yyyy-MM"));
                yearMonthStart = yearMonthStart + "-01";
                yearMonthEnd = yearMonthEnd + "-01";
            } catch (Exception e) {
                log.error("时间格式异常 {} {}", yearMonthStart, yearMonthEnd, e);
                throw new BusinessException("时间格式异常");
            }
            List<String> bizProjectIdList = managementNodeApi.getUserProjectByNode(request.getBizNodeId(), LoginUserUtil.getLoginUserId()).getCheckedData();
            // 查询历史数据
            List<MaintenanceExportDTO> list;
            if (bizProjectIdList.isEmpty()) {
                list = new ArrayList<>();
            } else {
                list = baseMapper.selectExportList(
                        bizProjectIdList,
                        request.getMaintenanceType(),
                        yearMonthStart,
                        yearMonthEnd);
            }
            // 默认列宽
            writer.setColumnWidth(-1, 30);
            //各个字段标题
            List<String> header = CollUtil.newArrayList("项目", "房号", "月份", "日期", "报修类别", "报修内容");
            List<List<String>> rows = new ArrayList<>();
            for (MaintenanceExportDTO data : list) {
                List<String> row = new ArrayList<>();
                row.add(data.getProjectName());
                row.add(data.getRoom());
                row.add(data.getYearMonth());
                row.add(data.getMaintenanceDate());
                row.add(MaintenanceTypeEnum.codeToName(data.getMaintenanceType()));
                row.add(data.getContent());
                rows.add(row);
            }
            writer.writeHeadRow(header);
            writer.write(rows);
            writer.flush(os);
        } catch (Exception e) {
            log.error("报修数据导出异常", e);
        }
    }

    @Override
    public MaintenanceImportDTO excelImportCheck(MultipartFile file) {
        List<Map<String, Object>> rowList = readExcel(file);
        MaintenanceImportDTO dto = baseCheck(rowList);
        if (!dto.getErrMsg().isEmpty()) {
            return dto;
        }
        dbCheck(dto);
        return dto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void excelImportSave(MaintenanceImportDTO dto) {
        dto.getDataList().forEach(data -> save(data.convertToEntity()));
    }

}