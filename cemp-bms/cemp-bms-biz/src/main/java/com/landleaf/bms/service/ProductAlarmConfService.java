package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.landleaf.bms.domain.request.*;
import com.landleaf.bms.api.dto.ProductAlarmConfListResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * ProductAlarmConfService
 *
 * @author 张力方
 * @since 2023/8/11
 **/
public interface ProductAlarmConfService {
    /**
     * 新增产品告警配置
     *
     * @param request 新增请求
     */
    void addAlarmConf(ProductAlarmConfAddRequest request);

    /**
     * 编辑产品告警配置
     *
     * @param request 编辑请求
     */
    void editAlarmConf(ProductAlarmConfEditRequest request);

    /**
     * 删除产品告警配置
     *
     * @param id 配置id
     */
    void deleteAlarmConf(Long id);

    /**
     * 分页查询告警配置
     *
     * @param request 查询条件
     * @return 告警配置列表
     */
    Page<ProductAlarmConfListResponse> pageQuery(ProductAlarmConfQueryRequest request);

    /**
     * 校验告警码是否唯一
     * <p>
     * true 唯一， false 不唯一
     *
     * @return true 唯一， false 不唯一
     */
    boolean checkCodeUnique(ProductAlarmConfCodeUniqueRequest request);

    /**
     * excel 导入
     *
     * @param file      excel
     * @param productId 产品id
     */
    List<String> importFile(MultipartFile file, Long productId) throws IOException;
}
