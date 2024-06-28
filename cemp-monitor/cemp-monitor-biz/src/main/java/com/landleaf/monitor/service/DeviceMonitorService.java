package com.landleaf.monitor.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.comm.constance.DeviceStaCategoryEnum;
import com.landleaf.monitor.api.dto.DeviceStaDTO;
import com.landleaf.monitor.api.dto.ProjectStaDTO;
import com.landleaf.monitor.domain.dto.DeviceMonitorAddDTO;
import com.landleaf.monitor.domain.dto.DeviceMonitorCurrentDTO;
import com.landleaf.monitor.domain.dto.DeviceMonitorQueryDTO;
import com.landleaf.monitor.domain.entity.DeviceMonitorEntity;
import com.landleaf.monitor.domain.request.*;
import com.landleaf.monitor.domain.response.AVueDeviceListResponse;
import com.landleaf.monitor.domain.response.AVueDevicePageResponse;
import com.landleaf.monitor.domain.response.NodeProjectDeviceTreeResponse;
import com.landleaf.monitor.domain.response.NodeSpaceDeviceTreeResponse;
import com.landleaf.monitor.domain.vo.DeviceMonitorTableLabelVo;
import com.landleaf.monitor.domain.vo.DeviceMonitorVO;
import com.landleaf.monitor.domain.vo.ProjectBizCategoryVO;

import java.util.List;

/**
 * 设备-监测平台的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-06-05
 */
public interface DeviceMonitorService extends IService<DeviceMonitorEntity> {

    /**
     * 新增一个对象
     *
     * @param addInfo 新增对象的数据的封装
     * @return 新增后的数据对象
     */
    DeviceMonitorAddDTO save(DeviceMonitorAddDTO addInfo);

    /**
     * 修改一个对象
     *
     * @param updateInfo 修改对象的数据的封装
     */
    void update(DeviceMonitorAddDTO updateInfo);

    /**
     * 修改数据的is_delete标识
     *
     * @param ids       要修改的数据的编号
     * @param isDeleted 删除标记
     */
    void updateIsDeleted(String ids, Integer isDeleted);

    /**
     * 根据id，查询详情
     *
     * @param id 编号
     * @return 详情信息
     */
    DeviceMonitorEntity selectById(Long id);

    /**
     * 根据查询条件，查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合
     */
    List<DeviceMonitorEntity> list(DeviceMonitorQueryDTO queryInfo);

    /**
     * 根据查询条件，分页查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合的分页信息
     */
    IPage<DeviceMonitorEntity> page(DeviceMonitorQueryDTO queryInfo);

    List<ProjectBizCategoryVO> getProjectBizCategory(DeviceMonitorCurrentDTO qry);

    /**
     * 查询品类的动态表头
     *
     * @param categoryBizId 品类业务ID
     * @return 结果
     */
    List<DeviceMonitorTableLabelVo> searchTableLabelList(String categoryBizId);

    /**
     * 变更表头列是否显示
     *
     * @param request 请求
     */
    void tableLabelShow(TableLabelShowRequest request);

    IPage<JSONObject> getcurrent(DeviceMonitorCurrentDTO qry);

    /**
     * 根据业务设备编号，查询对饮设备信息
     *
     * @param bizDeviceId 业务编号
     * @return 设备信息封装
     */
    DeviceMonitorEntity selectByBizDeviceId(String bizDeviceId);

    /**
     * 获取设备的树状结构
     *
     * @return
     */
    NodeProjectDeviceTreeResponse getDeviceTree();

    NodeProjectDeviceTreeResponse getDeviceTreeProduct();

    NodeProjectDeviceTreeResponse getDeviceTreeSpace();

    NodeSpaceDeviceTreeResponse nodeSpaceDeviceTree(String bizCategoryId);

    /**
     * 根据业务设备编号， 批量查询对饮设备信息
     *
     * @param bizDeviceIds
     */
    List<DeviceMonitorEntity> selectByBizDeviceIds(List<String> bizDeviceIds);

    /**
     * 根据品类获取设备统计任务的设备信息
     *
     * @param categoryType 品类类型
     * @return 设备信息
     */
    List<DeviceStaDTO> listStaDeviceByCategory(DeviceStaCategoryEnum categoryType);

    /**
     * 变更表头列顺序
     *
     * @param request 请求
     */
    void tableLabelSort(TableLabelSortRequest request);

    IPage<DeviceMonitorVO> listByProject(DeviceMonitorQueryDTO queryInfo);

    void edit(DeviceMonitorVO editInfo);

    void delete(String bizDeviceId);

    DeviceMonitorEntity getbyDeviceId(String bizDeviceId);

    DeviceMonitorEntity getbyOutId(String outId);

    List<ProjectStaDTO> listStaProject(Long tenantId);

    IPage<AVueDevicePageResponse> aVueGetDevices(String bizProjectId, AVueDevicePageRequest request);

    List<AVueDeviceListResponse> aVueGetDeviceAll(String bizProjectId);

    /**
     * 新的懒加载的数据，只保留项目信息，不加载里面的内容
     *
     * @return
     */
    NodeProjectDeviceTreeResponse getDeviceTreeV1Space(String bizId);

    /**
     * 新的懒加载的数据，只保留项目信息，不加载里面的内容
     *
     * @param bizProjId
     * @return
     */
    NodeProjectDeviceTreeResponse getDeviceTreeV1Product(String bizId);

    void tableLabelWidth(TableLabelWidthRequest request);

    void addServiceEvent(
            long time,
            Long tenantId,
            String username,
            DeviceMonitorEntity device,
            AVueServiceControlRequest request,
            Boolean isSuccess
    );
}
