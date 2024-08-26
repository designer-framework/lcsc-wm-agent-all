# 应用启动性能优化

> 1. 打开`SpringAgent`内置的应用启动耗时优化功能, 目前支持主流组件: Apollo, OpenFeign, Swagger, Aop

```yaml
spring:
  profiles:
    include: plugins
  agent:
    turbo:
      #打开组件的启动耗时优化(默认为false不开启)
      enabled-by-default: true
      #将forkJoin线程池的线程数量修置为cpu*2
      forkJoin:
        enabled: true
      #并行加载apollo命名空间
      apollo:
        enabled: true
      #不加载swagger
      swagger:
        enabled: true
      #并行加载OpenFeign客户端
      openFeign:
        enabled: true
      #AOP加速优化
      aop:
        enabled: true
```

> 2. 以Apollo配置中心启动速度优化为例进行原理讲解分析

* Apollo加载配置慢的原因  
  com.ctrip.framework.apollo.internals.DefaultConfigManager#getConfig\(java.lang.String\)使用了synchronized\(this\)关键字,导致无法并行加载配置
* 优化方案  
  借助bytekit重写类方法, 将 锁修改成命名空间级别: synchronized(namespace.intern())
* 具体优化代码详见AutoConfiguration配置类(下面会详细分析该配置类)  
  com.lcsc.wm.agent.plugin.core.configuration.trubo.ApolloTurboConfiguration

```java
//替换类中同方法名同入参的方法, 将Apollo加载命名空间的方式由串行改成并行
@EnabledInstrument({
        //instrumentClass中同名同入参的方法 覆盖 用户类className中的方法, 方法名和入参相同的方法才会替换)
        @Retransform(className = TurboConstants.ApolloInjector, instrumentClass = ApolloInjector.class),
        @Retransform(className = TurboConstants.DefaultConfigManager, instrumentClass = DefaultConfigManager.class)
})
@Configuration(proxyBeanMethods = false)
//激活配置, 该Configuration类才生效(对应上面的yaml配置)
@ConditionalOnTurboPropCondition(pluginName = "apollo")
public class ApolloTurboConfiguration {

    /**
     * 并行加载, 需要解决命名空间重复加载 及 占位符解异常的问题, 所以必须等所有配置并行加载完成后才能执行后续代码块
     *
     * @return
     * @see com.ctrip.framework.apollo.ConfigService#getConfig(java.lang.String)
     */
    @Bean
    @ConditionalOnMissingBean(ApolloCreatorPointcutAdvisor.class)
    public ApolloCreatorTurboPointcutAdvisor apolloCreatorTurboPointcutAdvisor() {
        return new ApolloCreatorTurboPointcutAdvisor(
                SpringComponentEnum.APOLLO_APPLICATION_CONTEXT_INITIALIZER
                , ClassMethodInfo.create("com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(org.springframework.core.env.ConfigurableEnvironment)")
        );

    }

}
```
