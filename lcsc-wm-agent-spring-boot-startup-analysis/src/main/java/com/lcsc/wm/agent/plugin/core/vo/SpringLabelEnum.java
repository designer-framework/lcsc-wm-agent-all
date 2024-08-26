package com.lcsc.wm.agent.plugin.core.vo;

import lombok.Getter;
import org.springframework.core.Ordered;

@Getter
public enum SpringLabelEnum implements SpringLabel {

    SPRING_BOOT_VERSION(Ordered.HIGHEST_PRECEDENCE, "Spring Boot 版本"),
    START_UP_TIME(Ordered.HIGHEST_PRECEDENCE + 10, "项目启动耗时"),
    BEAN_NUM(Ordered.HIGHEST_PRECEDENCE + 20, "已创建Bean总数"),
    SPRING_APPLICATION(Ordered.HIGHEST_PRECEDENCE + 30, "SpringBoot实例", false),//序列化会有循环依赖问题, 暂不序列化
    AOP_REPOSITORY(Ordered.HIGHEST_PRECEDENCE, "AOP缓存类型" + 40),
    ;

    private final int order;

    private final String label;

    private final boolean show;

    SpringLabelEnum(int order, String label) {
        this(order, label, true);
    }

    SpringLabelEnum(int order, String label, boolean show) {
        this.show = show;
        this.order = order;
        this.label = label;
    }

}
