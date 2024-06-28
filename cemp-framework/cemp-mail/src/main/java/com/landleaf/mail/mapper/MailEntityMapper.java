package com.landleaf.mail.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.landleaf.mail.domain.entity.MailEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件模板Mapper
 *
 * @author yue lin
 * @since 2023/6/15 9:28
 */
@Mapper
public interface MailEntityMapper extends BaseMapper<MailEntity> {

    default String searchFormat(Long type) {
        MailEntity mailEntity = selectOne(Wrappers.<MailEntity>lambdaQuery().eq(MailEntity::getType, type));
        Assert.notNull(mailEntity, "目标邮件模板不存在");
        return mailEntity.getFormat();
    };

}
