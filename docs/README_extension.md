# 插件拓展

## 1. 插件拓展开发说明
> `SpringAgent` 是基于SpringBoot框架进行开发, 所以我们可以像日常开发spring-boot-starter组件一样, 很轻易的就能完成功能的拓展或者增强
1. 复制`SpringAgent`项目中的的子模块: `lcsc-wm-agent-spring-boot-startup-analysis-plugin-template`
2. 在新模块中新增AutoConfiguration配置类, 具体在后面说到

## 2. 功能拓展示例

### 2.1 方法调用耗时统计

> 1. 如果是对简单的对方法进行调用耗时统计, 可直接在application-agent.yml配置文件中新增待插桩的全限定方法名就能完成插桩

```yaml
spring:
  agent:
    #方法调用统计
    method-invoke:
      advisors:
        #方法名
        - method: java.net.URLClassLoader#findResource(java.lang.String)
          #是否在SpringAgent被加载时重新装载类(如果值为true, 已被类加载器加载的类也会被重载)
          can-retransform: true
```

> 2. 使用注解完成对目标类方法的插桩,可参考SpringAgent项目中的如下代码块:

```java
//只需要给一个全限定类名即可完成插桩
@Configuration(proxyBeanMethods = false)
@EnabledMethodInvokeWatch({
        //spring-boot3.2.0+= 类加载器
        @MethodInvokeWatch("org.springframework.boot.loader.launch.LaunchedClassloader#loadClass(java.lang.String, boolean)"),
        //spring-boot3.2.0- 类加载器
        @MethodInvokeWatch("org.springframework.boot.loader.LaunchedURLClassLoader#loadClass(java.lang.String, boolean)"),
        //扫包耗时
        @MethodInvokeWatch("org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)")
})
public class EnabledMethodInvokeWatchAutoConfiguration {
}
```

> 3. 如果插桩逻辑较为复杂,可结合bytekit官方API及com.lcsc.wm.agent.core.advisor.SimpleMethodInvokePointcutAdvisor类完成自定义的插桩逻辑,可参考SpringAgent项目中的如下代码块:
```java

@Bean
public SimpleMethodInvokePointcutAdvisor preInstantiateSingletonsPointcutAdvisor() {
    return new SimpleMethodInvokePointcutAdvisor(
            //指定被插桩的全限定方法名
            ClassMethodInfo.create("org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()")
            //由这个类定义触发SpyAPI端点的时机
            , InitializingSingletonsPointcutAdvisor.AfterSingletonsInstantiatedSpyInterceptorApi.class
    );
}
```

### 2.2 组件耗时统计
> 1. 基于API接口或提供的拓展点完成拓展, 可参考SpringAgent项目中的如下代码块:
```java
@Bean
public InitializingSingletonsPointcutAdvisor initializingSingletonsPointcutAdvisor() {
    return new InitializingSingletonsPointcutAdvisor(
            //组件名
            SpringComponentEnum.SMART_INITIALIZING_SINGLETON
            //定义被插桩的全限定方法名
            , ClassMethodInfo.create("org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()")
            //由这个类定义触发SpyAPI端点的时机
            , InitializingSingletonsPointcutAdvisor.AfterSingletonsInstantiatedSpyInterceptorApi.class
    );
}
