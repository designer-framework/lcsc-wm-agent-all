package com.lcsc.wm.agent.plugin.core.enums;

import lombok.Getter;

@Getter
public enum SpringComponentEnum implements ComponentEnum {

    SPRING_APPLICATION("SpringApplication", "SpringApplication"),
    ABSTRACT_AUTO_PROXY_CREATOR("AOP-代理类生成", "AbstractAutoProxyCreator"),
    CLASS_PATH_SCANNING_CANDIDATE("Spring包扫描", "ClassPathScanningCandidateComponentProvider"),
    INIT_ANNOTATION_BEAN("Bean初始化-InitializingBean", "InitializingBean"),
    SMART_INITIALIZING_SINGLETON("Bean初始化-SmartInitializingSingleton", "SmartInitializingSingleton"),
    DESTROY_ANNOTATION_BEAN("Bean销毁", "DestroyAnnotationBean"),
    APOLLO_APPLICATION_CONTEXT_INITIALIZER("Apollo配置中心", "ApolloApplicationContextInitializer"),
    FEIGN_CLIENT_FACTORY_BEAN("OpenFeign", "FeignClientFactoryBean"),
    SWAGGER("Swagger-API扫描", "DocumentationPluginsBootstrapper");

    private final String displayName;

    private final String componentName;

    SpringComponentEnum(String displayName, String componentName) {
        this.displayName = displayName;
        this.componentName = componentName;
    }

}
