package com.lcsc.wm.agent.plugin.core.events;

import com.lcsc.wm.agent.plugin.core.vo.SpringBootApplicationLabelVO;
import com.lcsc.wm.agent.plugin.core.vo.SpringLabel;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SpringBootApplicationLabelEvent extends ApplicationEvent {

    private final SpringBootApplicationLabelVO springBootApplicationLabelVO;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringBootApplicationLabelEvent(Object source, SpringBootApplicationLabelVO springBootApplicationLabelVO) {
        super(source);
        this.springBootApplicationLabelVO = springBootApplicationLabelVO;
    }

    public static SpringBootApplicationLabelEvent create(Object source, SpringLabel springLabel, Object value){
        return new SpringBootApplicationLabelEvent(source, new SpringBootApplicationLabelVO(springLabel, value));
    }

}
