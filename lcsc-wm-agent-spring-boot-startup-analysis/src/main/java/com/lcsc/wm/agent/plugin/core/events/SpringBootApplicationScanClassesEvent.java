package com.lcsc.wm.agent.plugin.core.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

@Getter
public class SpringBootApplicationScanClassesEvent extends ApplicationEvent {

    private final Set<String> classes;

    public SpringBootApplicationScanClassesEvent(Object source, Set<String> classes) {
        super(source);
        this.classes = classes;
    }

}
