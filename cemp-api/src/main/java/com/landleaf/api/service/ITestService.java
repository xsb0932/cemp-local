package com.landleaf.api.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.landleaf.api.domain.Test;

public interface ITestService extends IService<Test> {

    IPage<Test> page(int page, int size);
}
