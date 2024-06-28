package com.landleaf.jjgj.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.jjgj.domain.dto.CheckinDayAddDTO;
import com.landleaf.jjgj.domain.dto.CheckinDayQueryDTO;
import com.landleaf.jjgj.domain.entity.CheckinDayEntity;
import com.landleaf.job.api.dto.JobRpcRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.YearMonth;
import java.util.List;

/**
 * JjgjCheckinDayEntity对象的业务逻辑接口定义
 *
 * @author hebin
 * @since 2023-10-16
 */
public interface CheckinDayService extends IService<CheckinDayEntity> {

    /**
     * 新增一个对象
     *
     * @param addInfo 新增对象的数据的封装
     * @return 新增后的数据对象
     */
    CheckinDayAddDTO save(CheckinDayAddDTO addInfo);

    /**
     * 修改一个对象
     *
     * @param updateInfo 修改对象的数据的封装
     */
    void update(CheckinDayAddDTO updateInfo);

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
    CheckinDayEntity selectById(Integer id);

    /**
     * 根据查询条件，查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合
     */
    List<CheckinDayEntity> list(CheckinDayQueryDTO queryInfo);

    /**
     * 根据查询条件，分页查询实体的集合
     *
     * @param queryInfo 查询条件封装
     * @return 实体的集合的分页信息
     */
    IPage<CheckinDayEntity> page(CheckinDayQueryDTO queryInfo);

    /**
     * 根据文件，导入入住信息
     *
     * @param bizProjectId
     * @param file
     * @return
     */
    List<String> importFile(String bizProjectId, MultipartFile file) throws IOException;

    void staMonth(YearMonth month, JobRpcRequest request);
}
