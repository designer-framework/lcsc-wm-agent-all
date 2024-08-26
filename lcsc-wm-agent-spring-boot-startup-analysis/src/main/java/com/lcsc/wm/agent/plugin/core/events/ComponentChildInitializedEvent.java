package com.lcsc.wm.agent.plugin.core.events;

import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import lombok.Getter;

import java.util.List;

@Getter
public class ComponentChildInitializedEvent extends ComponentEvent {

    private final List<InitializedComponent> children;

    public ComponentChildInitializedEvent(Object source, List<InitializedComponent> children) {
        super(source);
        this.children = children;
    }

}
