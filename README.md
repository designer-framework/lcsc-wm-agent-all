## Spring Agent

![SpringAgent](docs/images/Agent.png)

`Spring Agent` 是一款针对SpringBoot应用启动耗时分析的工具, 参考借鉴了阿里的 `Arthas` 线上诊断工具的实现原理

当你遇到以下类似问题而毫无头绪时, `Spring Agent` 可以帮助你很好的定位, 同时它还提供了一些优化方案：

1. 你的应用是否在启动过程中, 在某个节点停顿相当长一段时间?
2. 同样的代码量, 相同的技术栈, 为什么应用启动耗时差异这么大?
3. 为什么项目发包时, 启动应用耗时巨长?
4. 是否有一个工具能直观的观测项目中加载较为耗时的组件以及组件的耗时明细?
5. 借助SpringAOP打印相关组件加载耗时, 可能导致Bean加载顺序被干扰从而出现配置加载异常或其它未知问题,
   有什么办法能在不侵入用户代码的情况下实现监控?
6. 是否能有一个易用且具有一定拓展性的应用启动耗时分析工具来帮助我解决上述问题?

#### 1. `SpringAgent`目录结构

* 目录结构

        ------------------------------------------------------------------------------------------------------------------------------------
        |--- lcsc-wm-agent-packaging                                    * All in one, maven打包工具
        |------------> 1. 负责将所有jar包及配置文件打包到统一文件夹
        |--- lcsc-wm-agent-spring-boot-maven-plugin                     * maven打包工具
        |------------> 1. 解决lcsc-wm-agent-core模块打成jar包后, 多个spring.factoryies文件被覆盖导致SpringAgent启动失败的问题
        |--- lcsc-wm-agent                                              * javaagent 入口代码
        |--- lcsc-wm-agent-common                                       * 存放公共Utils, 及常量
        |--- lcsc-wm-agent-framework                                    * SpringAgent骨架
        |------------> 1. 定义了用于判定增强类的API 
        |------------> 2. 定义了被增强的类方法调用之前, 调用之后, 及调用抛出异常之后的API接口
        |------------> 3. 定义了增强的起始和结束触发点
        |--- lcsc-wm-agent-core                                         * 基于 lcsc-wm-agent-framework开发
        |------------> 1. 实现lcsc-wm-agent-framework中定义的API, 开发者可基于`SpringAgent`提供的抽象类及默认实现类进行功能的拓展增强
        |--- lcsc-wm-agent-spring-boot-startup-analysis                 * 基于 lcsc-wm-agent-core 开发
        |------------> 1. 为SpringBoot应用量身打造, 用于分析SpringBoot应用从启动直至启动完成期间的高耗时代码. 
        |--- lcsc-wm-agent-spring-boot-startup-analysis-plugin-template * 开发插件使用此模板(内置参考示例)
        |------------> 1. 以该项目作为插件开发模板, 可以像写传统业务代码一样完成对SpringAgent功能的拓展
        |--- spring-boot-web-demo                                       * 应用启动耗时性能分析示例项目
        |------------> 1. 启动项目时增加JVM启动参数 -Denv=DEV -javaagent:此处改成lcsc-wm-agent-core.jar包的绝对路径, 请修改Apollo配置中心服务端配置项
        ------------------------------------------------------------------------------------------------------------------------------------

#### 2. 使用`spring-agent`

* 安装好maven, 切记关闭maven的多线程打包, 然后运行 `mvn clean package`,
  最后在需要进行性能分析的应用JVM启动参数中添加`-javaagent`配置, 示例如下：
    * `java -javaagent:D:\TeamWork\lcsc-wm-agent-all\lcsc-wm-agent-packaging\target\agent-bin\lcsc-wm-agent.jar=;spring.application.name=SpringAgent -jar spring-boot-web-demo-1.0.0.jar`

```
    
  javaAgent配置说明:
        1. 前半部分为`SpringAgent`jar包所在的路径, 通常在当前maven项目的`lcsc-wm-agent-packaging/target/agent-bin`路径下  
            示例: D:\TeamWork\lcsc-wm-agent-all\lcsc-wm-agent-packaging\target\agent-bin\lcsc-wm-agent.jar=;spring.application.name=SpringAgent
        2. 为`SpringAgent`应用添加应用启动参数 `myKey=myVal`, 在jar包路径后追加参数: myKey=myVal, 效果类似JVM启动参数 -Dspring.application.name=SpringAgent`
            例如: D:\TeamWork\lcsc-wm-agent-all\lcsc-wm-agent-packaging\target\agent-bin\lcsc-wm-agent.jar=;spring.application.name=SpringAgent
```

#### 备注:

* 后端项目github地址: https://github.com/designer-framework/lcsc-wm-agent-all
* 前端项目github地址: https://github.com/designer-framework/spring-agent-vue
* 后端项目构建并启动, 然后下载并构建好前端项目并启动, 访问前端首页地址 http://127.0.0.1:8082

### 文档

* [使用说明](docs/README_usage.md)
* [性能优化](docs/README_turbo.md)
* [插件拓展](docs/README_extension.md)

### 参考资料

* Apache Tomcat如何实现Web应用之间的隔离
    - https://zhuanlan.zhihu.com/p/700250179

* Arthas如何实现代码隔离
    - https://yeas.fun/archives/arthas-isolation

* bytekit基于注解的字节码增强框架
    - https://github.com/alibaba/bytekit

* 性能火焰图
    - https://arthas.aliyun.com/doc/profiler.html
    - https://github.com/async-profiler/async-profiler

* SpringAOP
    - ```org.springframework.aop.support.DefaultPointcutAdvisor```
    - ```org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#createProxy```
