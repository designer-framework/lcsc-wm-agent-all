package com.lcsc.wm.agent.plugin.core.vo;

import lombok.Data;
import org.springframework.core.Ordered;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-03 15:29
 */
@Data
public class SpringBootApplicationLabelVO implements Ordered {

    private final int order;

    private final String label;

    private final Object value;

    private transient final boolean show;

    private SpringBootApplicationLabelVO(int order, String label, Object value, boolean show) {
        this.order = order;
        this.label = label;
        this.value = value;
        this.show = show;
    }

    public SpringBootApplicationLabelVO(SpringLabel springLabel, Object value) {
        this(springLabel.getOrder(), springLabel.getLabel(), value, springLabel.isShow());
    }

}
