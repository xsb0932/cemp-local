package com.landleaf.bms.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.bms.api.weather.dto.ProjectCityWeatherDTO;
import com.landleaf.bms.dal.mapper.AddressMapper;
import com.landleaf.bms.domain.entity.AddressEntity;
import com.landleaf.bms.domain.request.AddressQueryRequest;
import com.landleaf.bms.domain.response.AddressResponse;
import com.landleaf.bms.service.AddressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 行政区域的业务逻辑接口实现
 *
 * @author hebin
 * @since 2023-06-25
 */
@Service
@AllArgsConstructor
@Slf4j
public class AddressServiceImpl extends ServiceImpl<AddressMapper, AddressEntity> implements AddressService {
    private static Map<String, AddressResponse> provinceMap;
    private static Map<String, AddressResponse> cityMap;
    private static Map<String, AddressResponse> countyMap;
    /**
     * 地址类型：省份
     */
    private static final int ADDR_TYPE_PROVINCE = 1;
    /**
     * 地址类型：城市
     */
    private static final int ADDR_TYPE_CITY = 2;
    /**
     * 地址类型：区
     */
    private static final int ADDR_TYPE_COUNTY = 3;

    /**
     * 初始化
     */
    @PostConstruct
    private void init() {
        reload();
    }

    private void reload() {
        List<AddressEntity> list = baseMapper.selectList(Wrappers.emptyWrapper());
        provinceMap = new HashMap<>(16);
        cityMap = new HashMap<>(16);
        countyMap = new HashMap<>(64);

        provinceMap = list.stream().filter(i -> i.getAddressType() == ADDR_TYPE_PROVINCE).collect(
                Collectors.toMap(AddressEntity::getAddressCode, i -> BeanUtil.copyProperties(i, AddressResponse.class)));
        cityMap = list.stream().filter(i -> i.getAddressType() == ADDR_TYPE_CITY).collect(
                Collectors.toMap(AddressEntity::getAddressCode, i -> BeanUtil.copyProperties(i, AddressResponse.class)));
        countyMap = list.stream().filter(i -> i.getAddressType() == ADDR_TYPE_COUNTY).collect(
                Collectors.toMap(AddressEntity::getAddressCode, i -> BeanUtil.copyProperties(i, AddressResponse.class)));

        for (AddressResponse addressResponse : countyMap.values()) {
            if (cityMap.containsKey(addressResponse.getParentCode())) {
                cityMap.get(addressResponse.getParentCode()).addChildren(addressResponse);
            }
        }
        for (AddressResponse addressResponse : cityMap.values()) {
            if (provinceMap.containsKey(addressResponse.getParentCode())) {
                provinceMap.get(addressResponse.getParentCode()).addChildren(addressResponse);
            }
        }
    }

    @Override
    public List<AddressResponse> list(AddressQueryRequest queryRequest) {
        return provinceMap.values().stream()
                .sorted(Comparator.comparing(AddressResponse::getId))
                .collect(Collectors.toList());
    }

    @Override
    public String getCountyNameByAddressCode(String addressCode) {
        AddressResponse county = countyMap.get(addressCode);
        if (null == county) {
            return null;
        }
        AddressResponse city = cityMap.get(county.getParentCode());
        if (null == city) {
            return null;
        }
        AddressResponse province = provinceMap.get(city.getParentCode());
        if (null == province) {
            return null;
        }
        return province.getAddressName() + city.getAddressName() + county.getAddressName();
    }

    @Override
    public String getWeatherCodeByAddressCode(String addressCode) {
        AddressResponse county = countyMap.get(addressCode);
        if (null == county) {
            return null;
        }
        AddressResponse city = cityMap.get(county.getParentCode());
        if (null == city) {
            return null;
        }
        return city.getWeatherCode();
    }

    @Override
    public String getCityNameByAddressCode(String addressCode) {
        AddressResponse county = countyMap.get(addressCode);
        if (null == county) {
            return null;
        }
        AddressResponse city = cityMap.get(county.getParentCode());
        if (null == city) {
            return null;
        }
        return city.getAddressName();
    }

    @Override
    public Collection<ProjectCityWeatherDTO> getWeatherCodeByAddressCode(List<String> addressCodeList) {
        HashMap<String, ProjectCityWeatherDTO> map = new HashMap<>(16);
        for (String addressCode : addressCodeList) {
            AddressResponse county = countyMap.get(addressCode);
            if (null != county) {
                // 区域找城市
                AddressResponse city = cityMap.get(county.getParentCode());
                if (null != city) {
                    map.put(city.getWeatherCode(), new ProjectCityWeatherDTO().setWeatherCode(city.getWeatherCode()).setWeatherName(city.getWeatherName()));
                }
            }
            AddressResponse city = cityMap.get(addressCode);
            if (null != city) {
                map.put(city.getWeatherCode(), new ProjectCityWeatherDTO().setWeatherCode(city.getWeatherCode()).setWeatherName(city.getWeatherName()));
            }
        }
        return map.values();
    }

    @Override
    public String getWeatherNameByProjectAddressCode(String addressCode) {
        AddressResponse county = countyMap.get(addressCode);
        if (null != county) {
            // 区域找城市
            AddressResponse city = cityMap.get(county.getParentCode());
            if (null != city) {
                return city.getWeatherName();
            }
        }
        AddressResponse city = cityMap.get(addressCode);
        if (null != city) {
            return city.getWeatherName();
        }
        return null;
    }
}