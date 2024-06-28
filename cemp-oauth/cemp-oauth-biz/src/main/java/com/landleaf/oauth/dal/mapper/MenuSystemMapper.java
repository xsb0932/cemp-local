package com.landleaf.oauth.dal.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.landleaf.oauth.domain.entity.MenuSystemEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MenuSystemMapper
 *
 * @author 张力方
 * @since 2023/7/25
 **/
@Mapper
public interface MenuSystemMapper extends BaseMapper<MenuSystemEntity> {
    List<MenuSystemEntity> recursionMenuByPermission(@Param("permissions") List<String> permissions);
}
