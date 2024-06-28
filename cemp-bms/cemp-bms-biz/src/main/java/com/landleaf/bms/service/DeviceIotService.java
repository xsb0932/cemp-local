package com.landleaf.bms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.bms.domain.entity.DeviceIotEntity;
import com.landleaf.bms.domain.entity.ProductDeviceParameterEntity;
import com.landleaf.bms.domain.request.DeviceIotRequest;
import com.landleaf.bms.domain.response.DeviceIotInfoResponse;
import com.landleaf.bms.domain.response.DeviceIotResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 设备-监测平台的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-07-12
 */
public interface DeviceIotService extends IService<DeviceIotEntity> {


    DeviceIotEntity add(DeviceIotRequest addInfo);

    void edit(DeviceIotRequest editInfo);

    IPage<DeviceIotResponse> getProjectStaData(DeviceIotRequest qry);

    DeviceIotInfoResponse info(Long id);

    List<String> importFile(MultipartFile file, Long productId) throws IOException;

    void deleteParameters(Long id);

    List<ProductDeviceParameterEntity> getParameters(Long productId);
}
