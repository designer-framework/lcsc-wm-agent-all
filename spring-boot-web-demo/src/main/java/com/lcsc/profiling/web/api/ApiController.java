package com.lcsc.profiling.web.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcsc.profiling.web.annotation.Test;
import com.lcsc.profiling.web.feign.TestFeign;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Map;

@RestController
public class ApiController implements SmartInitializingSingleton, InitializingBean {

    @Autowired
    private TestFeign testFeign;

    @Value("classpath:/static/AnalysisJson.json")
    private Resource resource;

    @Autowired
    private ObjectMapper objectMapper;

    private Map map;

    @RequestMapping("/test")
    public String test(String test, MultipartFile multipartFile) {
        return testFeign.test(test);
    }

    @RequestMapping("/test1")
    public String test1(String test1) {
        System.out.println(test1);
        return test1;
    }

    @RequestMapping("/analysis/json")
    public Object analysisJson(@RequestParam("type") String type) {
        System.out.println(type);
        if (type == null) {
            return map;
        }
        return map.get(type);
    }

    @Override
    @SneakyThrows
    @Test
    public void afterSingletonsInstantiated() {
        System.out.println(123);
        Thread.sleep(123);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try (InputStream inputStream = resource.getInputStream()) {
            map = objectMapper.readValue(inputStream, Map.class);
        }
    }

}
