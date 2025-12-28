### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">任务1：项目初始化与数据库建表
输出：
完整的MySQL建表SQL脚本（users, items, borrow_records）
逾期预警视图SQL
基础Maven pom.xml依赖配置
application.yml数据库连接配置
技术要求：
Java 21
MySQL 8.0+
包含索引、外键约束、ON DELETE CASCADE
视图包含逾期天数计算
验收标准：
三个表结构符合要求
视图能正确筛选逾期记录
配置支持环境变量注入</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

该视图自动计算逾期天数（基于当前日期 CURDATE()）。

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">任务2：Spring Boot基础框架与实体类
输出：
三个JPA实体类：User、Item、BorrowRecord
对应Repository接口（JPA）
统一响应类ApiResponse
枚举类：UserType、UserStatus、ItemStatus、BorrowStatus
技术要求：
使用Lombok简化代码
JPA注解正确映射表结构
实体类包含所有属性、关系映射、时间字段
验收标准：
实体类与数据库表一一对应
关系映射正确（如用户与借阅记录的一对多）
枚举类完整覆盖状态类型</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

实体类与数据库表一一对应：字段名、类型、约束、索引、枚举完全匹配任务1中的 SQL 表结构。

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">任务3：用户认证与权限控制（Spring Security + JWT）
输出：
JWT工具类（生成、验证）
认证请求/响应DTO
Spring Security配置类（支持JWT过滤）
用户登录/注销API（/api/auth/login, /api/auth/logout）
技术要求：
Spring Security 5.7+
JJWT库
密码使用BCrypt加密
角色区分USER和ADMIN
验收标准：
登录成功返回token
接口基于角色授权
密码安全存储</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

登录成功返回 JWT token（Bearer 类型）及用户信息

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">任务4：用户管理模块
输出：
UserService业务层
AdminUserController（管理员操作用户）
用户CRUD API
用户查询分页支持
技术要求：
仅管理员可操作
分页查询支持
删除用户时级联处理借阅记录
验收标准：
管理员可创建/修改/删除/查询用户
普通用户无访问权限
分页查询功能正常</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

仅管理员可操作：控制器上使用 @PreAuthorize("hasRole('ADMIN')")，结合 Spring Security 实现严格权限控制，普通用户访问将返回 403。

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">任务5：物品管理模块
输入：任务4的输出
输出：
ItemService业务层
ItemController（物品增删改查）
物品分页、分类、状态查询API
状态自动更新逻辑
技术要求：
分页查询
多条件筛选（类别、状态、名称）
物品状态自动同步
验收标准：
管理员可管理物品
用户可查询可借物品
搜索功能正常</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

管理员可管理物品：增删改接口加 @PreAuthorize("hasRole('ADMIN')")，普通用户无权访问。

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">任务6：借阅管理模块
输出：
BorrowService核心业务逻辑
BorrowController（借出、归还、查询）
借阅状态自动计算逻辑
逾期天数计算逻辑
技术要求：
借出时默认7天归还
归还时更新物品状态
自动计算逾期状态和天数
验收标准：
用户可借出/归还物品
借阅记录正确更新
逾期状态自动识别</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

用户可借出/归还物品：完整支持借出（默认7天）、归还操作。

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">任务7：预警查询模块
输出：
AlertService预警查询逻辑
AlertController（逾期、即将到期查询）
支持按用户/全部查询
分页返回预警数据
技术要求：
查询3天内到期记录
查询已逾期记录
可查询指定用户逾期情况
验收标准：
预警接口返回正确数据
分页和筛选功能正常
逾期天数计算准确</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

预警接口返回正确数据：
已逾期：overdueDays 正数显示逾期天数
即将到期：daysUntilDue 显示剩余天数（1~3）

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">任务8：全局异常处理、数据验证与项目整合
输出：
全局异常处理器GlobalExceptionHandler
统一错误响应类
输入参数验证注解（@Valid）
完整的项目结构树与README
集成测试示例
技术要求：
@ControllerAdvice统一异常处理
@Valid进行参数校验
日志记录异常信息
项目可完整启动运行
验收标准：
所有接口有统一的响应格式
输入验证有效
异常信息友好且安全
项目可成功启动并无编译错误</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

配置 application.yml 中的数据库连接

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">修复错误：
1、ApiResponse.java：ApiResponse&lt;&gt;(code, message, null)--无法解析构造函数 'ApiResponse(int, String, null)'
2、GlobalExceptionHandler.java：return ApiResponse.clientError(400, "参数校验失败").setData(errors);--必需类型:ApiResponse&lt;java.util.Map&lt;java.lang.String,java.lang.String&gt;&gt;提供:void
3、AlertService.java：.findByUserIdAndReturnDateIsNullAndDueDateBetween()方法不存在
4、ItemService.java： }).filter(item -&gt; item != null); 必需类型:Page&lt;Item&gt;提供:Streamable&lt;Item&gt;</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

ApiResponse：新增带 data 的 clientError 重载方法

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">ApiResponse仍然存在相同的问题：无法解析构造函数 'ApiResponse(int, String, T)'</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

显式传入 LocalDateTime.now()，避免依赖默认值（timestamp = LocalDateTime.now()）在构造时未初始化的问题。

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">[ERROR] Failed to execute goal org.springframework.boot:spring-boot-maven-plugin:3.3.0:run (default-cli) on project library-system: Unable to find a suitable main class, please add a 'mainClass' property -&gt; [Help 1]
项目主类是不是没有写？</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

如果你的所有代码都在 com.library 包及其子包下（如 com.library.controller、com.library.entity 等），上面这个包名 package com.library; 是正确的。

---

### Grok AI

<div class="relative"><div class="relative"><div class="relative response-content-markdown markdown [&amp;_a:not(.not-prose)]:text-current [&amp;_a:not(.not-prose):hover]:text-primary [&amp;_a:not(.not-prose):hover]:decoration-primary [&amp;_a:not(.not-prose)]:underline [&amp;_a:not(.not-prose)]:decoration-primary/30 [&amp;_a:not(.not-prose)]:underline-offset-2 [&amp;_h2:not(.not-prose):first-child]:mt-0 [&amp;_h3:not(.not-prose):first-child]:mt-0 [&amp;_h4:not(.not-prose):first-child]:mt-0"><p dir="auto" class="break-words" node="[object Object]" style="white-space: pre-wrap;">Error starting ApplicationContext. To display the condition evaluation report re-run your application with 'debug' enabled.
2025-12-26T00:08:11.191+08:00 ERROR 31480 --- [ main] o.s.boot.SpringApplication : Application run failed
org.springframework.context.ApplicationContextException: Unable to start web server
at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:165) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:618) ~[spring-context-6.1.8.jar:6.1.8]
at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:146) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:754) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:456) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.SpringApplication.run(SpringApplication.java:335) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.SpringApplication.run(SpringApplication.java:1363) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.SpringApplication.run(SpringApplication.java:1352) ~[spring-boot-3.3.0.jar:3.3.0]
at com.library.LibrarySystemApplication.main(LibrarySystemApplication.java:10) ~[classes/:na]
Caused by: org.springframework.boot.web.server.WebServerException: Unable to start embedded Tomcat
at org.springframework.boot.web.embedded.tomcat.TomcatWebServer.initialize(TomcatWebServer.java:147) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.embedded.tomcat.TomcatWebServer.&lt;init&gt;(TomcatWebServer.java:107) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.getTomcatWebServer(TomcatServletWebServerFactory.java:516) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.getWebServer(TomcatServletWebServerFactory.java:222) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.createWebServer(ServletWebServerApplicationContext.java:188) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.onRefresh(ServletWebServerApplicationContext.java:162) ~[spring-boot-3.3.0.jar:3.3.0]
... 8 common frames omitted
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'securityConfig' defined in file [C:\Users\起源\Desktop\文件夹\人工智能理论论文\实验数据\项目结构复杂度测试\复杂项目\项目代码\grok\target\classes\com\library\config\SecurityConfig.class]: Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'customUserDetailsService' defined in file [C:\Users\起源\Desktop\文件夹\人工智能理论论文\实验数据\项目结构复杂度测试\复杂项目\项目代码\grok\target\classes\com\library\service\CustomUserDetailsService.class]: Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'userRepository' defined in com.library.repository.UserRepository defined in @EnableJpaRepositories declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration: Cannot resolve reference to bean 'jpaSharedEM_entityManagerFactory' while setting bean property 'entityManager'
at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:795) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:237) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:1357) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1194) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:562) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:409) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1337) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1167) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:562) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:205) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.boot.web.servlet.ServletContextInitializerBeans.getOrderedBeansOfType(ServletContextInitializerBeans.java:211) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.servlet.ServletContextInitializerBeans.addAsRegistrationBean(ServletContextInitializerBeans.java:174) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.servlet.ServletContextInitializerBeans.addAsRegistrationBean(ServletContextInitializerBeans.java:169) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.servlet.ServletContextInitializerBeans.addAdaptableBeans(ServletContextInitializerBeans.java:154) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.servlet.ServletContextInitializerBeans.&lt;init&gt;(ServletContextInitializerBeans.java:87) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.getServletContextInitializerBeans(ServletWebServerApplicationContext.java:266) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.selfInitialize(ServletWebServerApplicationContext.java:240) ~[spring-boot-3.3.0.jar:3.3.0]
at org.springframework.boot.web.embedded.tomcat.TomcatStarter.onStartup(TomcatStarter.java:52) ~[spring-boot-3.3.0.jar:3.3.0]
at org.apache.catalina.core.StandardContext.startInternal(StandardContext.java:4414) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:171) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1203) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1193) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317) ~[na:na]
at org.apache.tomcat.util.threads.InlineExecutorService.execute(InlineExecutorService.java:75) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at java.base/java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:145) ~[na:na]
at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:749) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.core.StandardHost.startInternal(StandardHost.java:772) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:171) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1203) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.core.ContainerBase$StartChild.call(ContainerBase.java:1193) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at java.base/java.util.concurrent.FutureTask.run(FutureTask.java:317) ~[na:na]
at org.apache.tomcat.util.threads.InlineExecutorService.execute(InlineExecutorService.java:75) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at java.base/java.util.concurrent.AbstractExecutorService.submit(AbstractExecutorService.java:145) ~[na:na]
at org.apache.catalina.core.ContainerBase.startInternal(ContainerBase.java:749) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.core.StandardEngine.startInternal(StandardEngine.java:203) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:171) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.core.StandardService.startInternal(StandardService.java:415) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:171) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.core.StandardServer.startInternal(StandardServer.java:874) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.util.LifecycleBase.start(LifecycleBase.java:171) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.apache.catalina.startup.Tomcat.start(Tomcat.java:437) ~[tomcat-embed-core-10.1.24.jar:10.1.24]
at org.springframework.boot.web.embedded.tomcat.TomcatWebServer.initialize(TomcatWebServer.java:128) ~[spring-boot-3.3.0.jar:3.3.0]
... 13 common frames omitted
Caused by: org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'customUserDetailsService' defined in file [C:\Users\起源\Desktop\文件夹\人工智能理论论文\实验数据\项目结构复杂度测试\复杂项目\项目代码\grok\target\classes\com\library\service\CustomUserDetailsService.class]: Unsatisfied dependency expressed through constructor parameter 0: Error creating bean with name 'userRepository' defined in com.library.repository.UserRepository defined in @EnableJpaRepositories declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration: Cannot resolve reference to bean 'jpaSharedEM_entityManagerFactory' while setting bean property 'entityManager'
at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:795) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.ConstructorResolver.autowireConstructor(ConstructorResolver.java:237) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.autowireConstructor(AbstractAutowireCapableBeanFactory.java:1357) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1194) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:562) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1443) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:904) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:782) ~[spring-beans-6.1.8.jar:6.1.8]
... 63 common frames omitted
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'userRepository' defined in com.library.repository.UserRepository defined in @EnableJpaRepositories declared on JpaRepositoriesRegistrar.EnableJpaRepositoriesConfiguration: Cannot resolve reference to bean 'jpaSharedEM_entityManagerFactory' while setting bean property 'entityManager'
at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveReference(BeanDefinitionValueResolver.java:377) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveValueIfNecessary(BeanDefinitionValueResolver.java:135) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.applyPropertyValues(AbstractAutowireCapableBeanFactory.java:1687) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.populateBean(AbstractAutowireCapableBeanFactory.java:1436) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:599) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.config.DependencyDescriptor.resolveCandidate(DependencyDescriptor.java:254) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.DefaultListableBeanFactory.doResolveDependency(DefaultListableBeanFactory.java:1443) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.DefaultListableBeanFactory.resolveDependency(DefaultListableBeanFactory.java:1353) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.ConstructorResolver.resolveAutowiredArgument(ConstructorResolver.java:904) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.ConstructorResolver.createArgumentArray(ConstructorResolver.java:782) ~[spring-beans-6.1.8.jar:6.1.8]
... 77 common frames omitted
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'jpaSharedEM_entityManagerFactory': Cannot resolve reference to bean 'entityManagerFactory' while setting constructor argument
at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveReference(BeanDefinitionValueResolver.java:377) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveValueIfNecessary(BeanDefinitionValueResolver.java:135) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.ConstructorResolver.resolveConstructorArguments(ConstructorResolver.java:682) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:509) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1337) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1167) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:562) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveReference(BeanDefinitionValueResolver.java:365) ~[spring-beans-6.1.8.jar:6.1.8]
... 91 common frames omitted
Caused by: org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'entityManagerFactory' defined in class path resource [org/springframework/boot/autoconfigure/orm/jpa/HibernateJpaConfiguration.class]: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.exception.GenericJDBCException: Unable to open JDBC Connection for DDL execution [Unsupported character encoding 'utf8mb4'] [n/a]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1788) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:600) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:522) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:337) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:234) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:335) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:200) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.BeanDefinitionValueResolver.resolveReference(BeanDefinitionValueResolver.java:365) ~[spring-beans-6.1.8.jar:6.1.8]
... 103 common frames omitted
Caused by: jakarta.persistence.PersistenceException: [PersistenceUnit: default] Unable to build Hibernate SessionFactory; nested exception is org.hibernate.exception.GenericJDBCException: Unable to open JDBC Connection for DDL execution [Unsupported character encoding 'utf8mb4'] [n/a]
at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:421) ~[spring-orm-6.1.8.jar:6.1.8]
at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.afterPropertiesSet(AbstractEntityManagerFactoryBean.java:396) ~[spring-orm-6.1.8.jar:6.1.8]
at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.afterPropertiesSet(LocalContainerEntityManagerFactoryBean.java:366) ~[spring-orm-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.invokeInitMethods(AbstractAutowireCapableBeanFactory.java:1835) ~[spring-beans-6.1.8.jar:6.1.8]
at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.initializeBean(AbstractAutowireCapableBeanFactory.java:1784) ~[spring-beans-6.1.8.jar:6.1.8]
... 110 common frames omitted
Caused by: org.hibernate.exception.GenericJDBCException: Unable to open JDBC Connection for DDL execution [Unsupported character encoding 'utf8mb4'] [n/a]
at org.hibernate.exception.internal.StandardSQLExceptionConverter.convert(StandardSQLExceptionConverter.java:63) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert(SqlExceptionHelper.java:108) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.engine.jdbc.spi.SqlExceptionHelper.convert(SqlExceptionHelper.java:94) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.resource.transaction.backend.jdbc.internal.DdlTransactionIsolatorNonJtaImpl.getIsolatedConnection(DdlTransactionIsolatorNonJtaImpl.java:74) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.resource.transaction.backend.jdbc.internal.DdlTransactionIsolatorNonJtaImpl.getIsolatedConnection(DdlTransactionIsolatorNonJtaImpl.java:39) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.tool.schema.internal.exec.ImprovedExtractionContextImpl.getJdbcConnection(ImprovedExtractionContextImpl.java:63) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.tool.schema.internal.exec.ImprovedExtractionContextImpl.getJdbcDatabaseMetaData(ImprovedExtractionContextImpl.java:70) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.tool.schema.extract.internal.InformationExtractorJdbcDatabaseMetaDataImpl.processTableResultSet(InformationExtractorJdbcDatabaseMetaDataImpl.java:65) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.tool.schema.extract.internal.AbstractInformationExtractorImpl.getTables(AbstractInformationExtractorImpl.java:570) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.tool.schema.extract.internal.DatabaseInformationImpl.getTablesInformation(DatabaseInformationImpl.java:122) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.tool.schema.internal.GroupedSchemaValidatorImpl.validateTables(GroupedSchemaValidatorImpl.java:41) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.tool.schema.internal.AbstractSchemaValidator.performValidation(AbstractSchemaValidator.java:98) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.tool.schema.internal.AbstractSchemaValidator.doValidation(AbstractSchemaValidator.java:76) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.performDatabaseAction(SchemaManagementToolCoordinator.java:289) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.lambda$process$5(SchemaManagementToolCoordinator.java:144) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at java.base/java.util.HashMap.forEach(HashMap.java:1429) ~[na:na]
at org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator.process(SchemaManagementToolCoordinator.java:141) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.boot.internal.SessionFactoryObserverForSchemaExport.sessionFactoryCreated(SessionFactoryObserverForSchemaExport.java:37) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.internal.SessionFactoryObserverChain.sessionFactoryCreated(SessionFactoryObserverChain.java:35) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.internal.SessionFactoryImpl.&lt;init&gt;(SessionFactoryImpl.java:322) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.boot.internal.SessionFactoryBuilderImpl.build(SessionFactoryBuilderImpl.java:457) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl.build(EntityManagerFactoryBuilderImpl.java:1506) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.springframework.orm.jpa.vendor.SpringHibernateJpaPersistenceProvider.createContainerEntityManagerFactory(SpringHibernateJpaPersistenceProvider.java:75) ~[spring-orm-6.1.8.jar:6.1.8]
at org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean.createNativeEntityManagerFactory(LocalContainerEntityManagerFactoryBean.java:390) ~[spring-orm-6.1.8.jar:6.1.8]
at org.springframework.orm.jpa.AbstractEntityManagerFactoryBean.buildNativeEntityManagerFactory(AbstractEntityManagerFactoryBean.java:409) ~[spring-orm-6.1.8.jar:6.1.8]
... 114 common frames omitted
Caused by: java.sql.SQLException: Unsupported character encoding 'utf8mb4'
at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:130) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:98) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:90) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:64) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:74) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:85) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.jdbc.ConnectionImpl.&lt;init&gt;(ConnectionImpl.java:434) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.jdbc.ConnectionImpl.getInstance(ConnectionImpl.java:241) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.jdbc.NonRegisteringDriver.connect(NonRegisteringDriver.java:189) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.zaxxer.hikari.util.DriverDataSource.getConnection(DriverDataSource.java:137) ~[HikariCP-5.1.0.jar:na]
at com.zaxxer.hikari.pool.PoolBase.newConnection(PoolBase.java:360) ~[HikariCP-5.1.0.jar:na]
at com.zaxxer.hikari.pool.PoolBase.newPoolEntry(PoolBase.java:202) ~[HikariCP-5.1.0.jar:na]
at com.zaxxer.hikari.pool.HikariPool.createPoolEntry(HikariPool.java:461) ~[HikariCP-5.1.0.jar:na]
at com.zaxxer.hikari.pool.HikariPool.checkFailFast(HikariPool.java:550) ~[HikariCP-5.1.0.jar:na]
at com.zaxxer.hikari.pool.HikariPool.&lt;init&gt;(HikariPool.java:98) ~[HikariCP-5.1.0.jar:na]
at com.zaxxer.hikari.HikariDataSource.getConnection(HikariDataSource.java:111) ~[HikariCP-5.1.0.jar:na]
at org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl.getConnection(DatasourceConnectionProviderImpl.java:122) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator$ConnectionProviderJdbcConnectionAccess.obtainConnection(JdbcEnvironmentInitiator.java:437) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
at org.hibernate.resource.transaction.backend.jdbc.internal.DdlTransactionIsolatorNonJtaImpl.getIsolatedConnection(DdlTransactionIsolatorNonJtaImpl.java:46) ~[hibernate-core-6.5.2.Final.jar:6.5.2.Final]
... 135 common frames omitted
Caused by: com.mysql.cj.exceptions.WrongArgumentException: Unsupported character encoding 'utf8mb4'
at java.base/jdk.internal.reflect.DirectConstructorHandleAccessor.newInstance(DirectConstructorHandleAccessor.java:62) ~[na:na]
at java.base/java.lang.reflect.Constructor.newInstanceWithCaller(Constructor.java:502) ~[na:na]
at java.base/java.lang.reflect.Constructor.newInstance(Constructor.java:486) ~[na:na]
at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:61) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.exceptions.ExceptionFactory.createException(ExceptionFactory.java:104) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.util.StringUtils.getBytes(StringUtils.java:237) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.jdbc.JdbcPropertySetImpl.postInitialization(JdbcPropertySetImpl.java:65) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.conf.DefaultPropertySet.initializeProperties(DefaultPropertySet.java:232) ~[mysql-connector-j-8.3.0.jar:8.3.0]
at com.mysql.cj.jdbc.ConnectionImpl.&lt;init&gt;(ConnectionImpl.java:387) ~[mysql-connector-j-8.3.0.jar:8.3.0]
... 147 common frames omitted
Caused by: java.io.UnsupportedEncodingException: utf8mb4
at java.base/java.lang.String.lookupCharset(String.java:861) ~[na:na]
at java.base/java.lang.String.getBytes(String.java:1795) ~[na:na]
at com.mysql.cj.util.StringUtils.getBytes(StringUtils.java:235) ~[mysql-connector-j-8.3.0.jar:8.3.0]
... 150 common frames omitted</p></div></div><section class="inline-media-container flex flex-col gap-1 clear-both"></section><section class="auth-notification flex flex-col gap-1"></section></div>

---

### 用户

修改 application.yml 中的 URL（去掉 characterEncoding=utf8mb4）

