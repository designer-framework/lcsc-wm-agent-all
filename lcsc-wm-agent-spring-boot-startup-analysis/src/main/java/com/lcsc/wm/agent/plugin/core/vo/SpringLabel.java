package com.lcsc.wm.agent.plugin.core.vo;

import org.springframework.core.Ordered;

public interface SpringLabel extends Ordered {

    String getLabel();

    boolean isShow();

}
