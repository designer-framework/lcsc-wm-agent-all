package com.lcsc.wm.agent.plugin.core.configuration.trubo;

import com.lcsc.wm.agent.plugin.core.condition.OnTurboCondition;
import com.lcsc.wm.agent.plugin.core.configuration.SpringComponentMethodInvokeAutoConfiguration;
import com.lcsc.wm.agent.plugin.core.properties.ComponentTurboProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * list.{ ?#this == 99999}
 *
 * @description:
 * @author: Designer
 * @date : 2024-07-26 23:04
 * @see OnTurboCondition
 * @see ComponentTurboProperties
 */
@Configuration(proxyBeanMethods = false)
@Import({
        ApolloTurboConfiguration.class,
        SwaggerTurboConfiguration.class,
        FeignClientTurboConfiguration.class,
        ForkJoinTurboConfiguration.class
})
@EnableConfigurationProperties(value = ComponentTurboProperties.class)
@AutoConfigureBefore(SpringComponentMethodInvokeAutoConfiguration.class)
public class SpringComponentTurboAutoConfiguration {
}
