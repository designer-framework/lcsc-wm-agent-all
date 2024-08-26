package com.lcsc.wm.agent.plugin.core.events;

import com.lcsc.wm.agent.plugin.core.vo.InitializedComponent;
import lombok.Getter;

@Getter
public class ComponentRootInitializedEvent extends ComponentEvent {

    private final InitializedComponent initializedComponent;

    public ComponentRootInitializedEvent(Object source, InitializedComponent initializedComponent) {
        super(source);
        this.initializedComponent = initializedComponent;
    }

}
