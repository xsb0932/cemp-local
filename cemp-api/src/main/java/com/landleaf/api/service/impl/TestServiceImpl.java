package com.landleaf.api.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.landleaf.api.mapper.TestMapper;
import com.landleaf.api.domain.Test;
import com.landleaf.api.service.ITestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl extends ServiceImpl<TestMapper, Test> implements ITestService {


    @Override
    public IPage<Test> page(int page, int size) {
        IPage<Test> p = new Page<Test>(page,size);
        return baseMapper.selectPage(p, null);
    }
}
