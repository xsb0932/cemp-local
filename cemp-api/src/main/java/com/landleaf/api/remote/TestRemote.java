package com.landleaf.api.remote;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("cemp-data")
public interface TestRemote {
    @RequestMapping(value = "/test-data", method = RequestMethod.GET)
    String test();
}
