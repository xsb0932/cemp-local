package com.landleaf.bms.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.bms.api.weather.dto.ProjectCityWeatherDTO;
import com.landleaf.bms.domain.entity.AddressEntity;
import com.landleaf.bms.domain.request.AddressQueryRequest;
import com.landleaf.bms.domain.response.AddressResponse;

import java.util.Collection;
import java.util.List;

/**
 * 行政区域的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-06-25
 */
public interface AddressService extends IService<AddressEntity> {

    /**
     * 根据查询条件，查询行政区域的集合
     *
     * @param queryRequest 查询条件封装
     * @return List<AddressResponse>
     */
    List<AddressResponse> list(AddressQueryRequest queryRequest);

    /**
     * 根据区编号获取省市区名称
     *
     * @param addressCode 区编号
     * @return 名称 xx省xx市xx区
     */
    String getCountyNameByAddressCode(String addressCode);

    /**
     * 根据区编号获取城市天气的code
     *
     * @param addressCode 区编号
     * @return 中国气象的城市code
     */
    String getWeatherCodeByAddressCode(String addressCode);

    /**
     * 根据区域编号，获取城市名称
     *
     * @param addressCode 区编号
     * @return 城市名称
     */
    String getCityNameByAddressCode(String addressCode);

    /**
     * 根据项目区域编号 获取天气code信息
     *
     * @param addressCodeList 项目区域编号
     * @return Collection<ProjectCityWeatherDTO>
     */
    Collection<ProjectCityWeatherDTO> getWeatherCodeByAddressCode(List<String> addressCodeList);

    /**
     * 获取项目的天气城市名
     *
     * @param addressCode 项目区域编码
     * @return 天气城市名
     */
    String getWeatherNameByProjectAddressCode(String addressCode);
}