package com.landleaf.bms.api.weather;


import com.landleaf.bms.api.weather.dto.ProjectCityWeatherDTO;
import com.landleaf.bms.domain.entity.ProjectEntity;
import com.landleaf.bms.service.AddressService;
import com.landleaf.bms.service.ProjectService;
import com.landleaf.comm.base.pojo.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

/**
 * @author Yang
 */
@RestController
@RequiredArgsConstructor
public class ProjectWeatherApiImpl implements ProjectWeatherApi {
    private final ProjectService projectService;
    private final AddressService addressService;

    @Override
    public Response<Collection<ProjectCityWeatherDTO>> listProjectCityWeather() {
        List<String> addressCodeList = projectService.listAllAddressCode();
        Collection<ProjectCityWeatherDTO> data = addressService.getWeatherCodeByAddressCode(addressCodeList);
        return Response.success(data);
    }

    @Override
    public Response<String> getProjectWeatherName(String bizProjectId) {
        ProjectEntity project = projectService.selectByBizProjectId(bizProjectId);
        if (null == project) {
            return Response.success();
        }
        List<String> addressCode = project.getAddressCode();
        return Response.success(addressService.getWeatherNameByProjectAddressCode(addressCode == null ? null : addressCode.get(addressCode.size() - 1)));
    }
}
