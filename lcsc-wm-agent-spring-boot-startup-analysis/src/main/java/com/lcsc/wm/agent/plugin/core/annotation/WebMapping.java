package com.lcsc.wm.agent.plugin.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Component
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface WebMapping {

    String[] value();

    String contentType() default "text/html; charset=UTF-8";

}
