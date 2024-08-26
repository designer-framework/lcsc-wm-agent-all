package com.lcsc.wm.agent.plugin.core.condition;

import com.lcsc.wm.agent.plugin.core.annotation.ConditionalOnTurboPropCondition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.lang.annotation.Annotation;
import java.text.MessageFormat;
import java.util.Map;

public class OnTurboCondition extends SpringBootCondition {

    private static final String PREFIX = "spring.agent.turbo";

    private static final String ENABLED_BY_DEFAULT_KEY = PREFIX + ".enabled-by-default";

    private static final String ENABLED_PLUGIN_KEY = PREFIX + ".{0}.enabled";

    /**
     * @see ConditionalOnTurboPropCondition#pluginName()
     */
    private static final String PLUGIN_NAME = "pluginName";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return getEnablementOutcome(context, metadata, ConditionalOnTurboPropCondition.class);
    }

    protected ConditionOutcome getEnablementOutcome(ConditionContext context, AnnotatedTypeMetadata metadata,
                                                    Class<? extends Annotation> annotationClass) {
        //spring.agent.turbo.${pluginName}.enabled = true
        String key = enableTurboKey(metadata, annotationClass);

        Environment environment = context.getEnvironment();

        if (key != null) {
            Boolean userDefinedEnabled = environment.getProperty(key, Boolean.class);
            //自定义配置
            if (userDefinedEnabled != null) {
                return new ConditionOutcome(userDefinedEnabled, ConditionMessage.forCondition(annotationClass)
                        .because("found property " + key + " with value " + userDefinedEnabled));
            }
        }

        //默认配置值
        Boolean userDefinedDefault = environment.getProperty(ENABLED_BY_DEFAULT_KEY, Boolean.class);
        if (userDefinedDefault != null) {
            return new ConditionOutcome(userDefinedDefault, ConditionMessage.forCondition(annotationClass).because(
                    "no property " + key + " found so using user defined default from " + ENABLED_BY_DEFAULT_KEY));
        }

        //都没配置
        return new ConditionOutcome(false, ConditionMessage.forCondition(annotationClass)
                .because("no property " + key + " found so using endpoint default"));
    }

    private String enableTurboKey(
            AnnotatedTypeMetadata metadata, Class<? extends Annotation> annotationClass
    ) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(annotationClass.getName());
        if (attributes == null) {
            return null;
        }

        String pluginName = (String) attributes.get(PLUGIN_NAME);

        if (StringUtils.isNotBlank(pluginName)) {

            return MessageFormat.format(ENABLED_PLUGIN_KEY, pluginName);

        } else {

            return null;

        }
    }

}
