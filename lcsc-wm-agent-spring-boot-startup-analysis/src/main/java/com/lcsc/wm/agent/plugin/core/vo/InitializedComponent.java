package com.lcsc.wm.agent.plugin.core.vo;

import com.lcsc.wm.agent.core.vo.DurationVO;
import com.lcsc.wm.agent.plugin.core.enums.ComponentEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
@Setter
public class InitializedComponent extends DurationVO {

    private transient boolean lazyRoot;

    private transient ComponentEnum parent;

    /**
     * 组件名
     */
    private ComponentEnum componentName;

    private String name;

    /**
     * Echarts展示名
     */
    private String displayName;

    private String desc;

    private ConcurrentLinkedDeque<InitializedComponent> children = new ConcurrentLinkedDeque<>();

    private InitializedComponent(ComponentEnum componentEnum) {
        super();
        this.componentName = componentEnum;

        this.name = componentEnum.getComponentName();
        this.displayName = componentEnum.getDisplayName();
    }

    public static InitializedComponent root(ComponentEnum component, BigDecimal startMillis) {
        InitializedComponent child = new InitializedComponent(component);
        child.setName(component.getComponentName());
        child.setDisplayName(component.getDisplayName());
        child.setStartMillis(startMillis);
        return root(component, startMillis, false);
    }

    public static InitializedComponent root(ComponentEnum component, BigDecimal startMillis, boolean lazyRoot) {
        InitializedComponent child = new InitializedComponent(component);
        child.setLazyRoot(lazyRoot);
        child.setName(component.getComponentName());
        child.setDisplayName(component.getDisplayName());
        child.setStartMillis(startMillis);
        return child;
    }

    public static InitializedComponent child(ComponentEnum parent, String showName, BigDecimal startMillis) {
        InitializedComponent child = new InitializedComponent(parent);
        child.setParent(parent);
        child.setName(showName);
        child.setDisplayName(parent.getDisplayName());
        child.setStartMillis(startMillis);
        return child;
    }

    public void insertChildren(Collection<InitializedComponent> children) {
        this.children.addAll(children);
    }

    public void updateDurationByChildren() {
        if (!CollectionUtils.isEmpty(children)) {
            setEndMillis(BigDecimal.ZERO);
            setDuration(children.stream().map(InitializedComponent::getDuration).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
    }

}
