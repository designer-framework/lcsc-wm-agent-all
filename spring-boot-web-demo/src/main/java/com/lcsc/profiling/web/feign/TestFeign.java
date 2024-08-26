package com.lcsc.profiling.web.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 15:32
 */
@FeignClient(contextId = "TestFeign", name = "application-consumer")
public interface TestFeign {

    @RequestMapping("/test1")
    String test(String test);

}
