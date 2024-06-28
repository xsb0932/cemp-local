package com.landleaf.energy.service;

import java.util.List;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import com.landleaf.energy.domain.dto.ProjectCnfSubareaAddDTO;
import com.landleaf.energy.domain.dto.ProjectCnfSubareaQueryDTO;
import com.landleaf.energy.domain.entity.DeviceMonitorEntity;
import com.landleaf.energy.domain.entity.ProjectCnfSubareaEntity;
import com.landleaf.energy.domain.vo.ProjectCnfSubareaVO;

/**
 * ProjectCnfSubareaEntity对象的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-06-24
 */
public interface ProjectCnfSubareaService extends IService<ProjectCnfSubareaEntity> {

    /**
     * 新增一个对象
     *
     * @param addInfo 新增对象的数据的封装
     * @return 新增后的数据对象
     */
    ProjectCnfSubareaAddDTO save(ProjectCnfSubareaAddDTO addInfo);

    void add(ProjectCnfSubareaAddDTO addInfo);

    void edit(ProjectCnfSubareaAddDTO addInfo);


    /**
     * 修改一个对象
     *
     * @param updateInfo 修改对象的数据的封装
     */
    void update(ProjectCnfSubareaAddDTO updateInfo);

    /**
     * 修改数据的is_delete标识
     *
     * @param ids       要修改的数据的编号
     * @param isDeleted 删除标记
     */
    void updateIsDeleted(String ids, Integer isDeleted);

    void delete(String id);

    /**
     * 根据id，查询详情
     *
     * @param id 编号
     * @return 详情信息
     */
    ProjectCnfSubareaEntity selectById(Long id);

    /**
     * 根据查询条件，查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合
     */
    List<ProjectCnfSubareaEntity> list(ProjectCnfSubareaQueryDTO queryInfo);

    List<ProjectCnfSubareaVO> listAll(String bizProjectId, String kpiTypeCode);

    /**
     * 根据查询条件，分页查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合的分页信息
     */
    IPage<ProjectCnfSubareaEntity> page(ProjectCnfSubareaQueryDTO queryInfo);

    ProjectCnfSubareaVO detail(Long id);

    List<DeviceMonitorEntity> allDevices(String bizProjectId);

    /**
     * 从空间批量导入分区
     *
     * @param bizProjectId
     */
    void batchImport(String bizProjectId);
}
