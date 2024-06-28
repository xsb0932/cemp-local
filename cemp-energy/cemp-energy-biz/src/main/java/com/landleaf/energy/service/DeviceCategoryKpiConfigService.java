package com.landleaf.energy.service;

import java.util.List;


import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.energy.domain.entity.DeviceCategoryKpiConfigEntity;
import com.landleaf.energy.domain.vo.EnergySelectedVO;

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
