package com.lcsc.profiling.web.test.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @description:
 * @author: Designer
 * @date : 2024-09-27 02:54
 */
@Service
public class SecondService {

    @Autowired
    private ThirdService thirdService;

    @Autowired
    private LeafService leafService;

}
