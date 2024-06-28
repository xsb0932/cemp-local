package com.landleaf.jjgj.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.jjgj.domain.entity.DeviceCategoryKpiConfigEntity;
import com.landleaf.jjgj.domain.vo.EnergySelectedVO;

import java.util.List;

/**
 * DeviceCategoryKpiConfigEntity对象的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-08-01
 */
public interface DeviceCategoryKpiConfigService extends IService<DeviceCategoryKpiConfigEntity> {



    List<DeviceCategoryKpiConfigEntity> listCategory();

    List<EnergySelectedVO> getKpi(String code);
}
