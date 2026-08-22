# 技术选型文档 (TECH.md)

**版本：** 1.0.0-SNAPSHOT  
**最后更新：** 2026-08-11  
**项目：** tpl-manage（tpl-workspace管理后台）  
**关联工程：** RuoYi-Vue-Plus 6.0.0

---

## 一、技术栈概览

tpl-manage 是基于 **RuoYi-Vue-Plus 6.0.0** 构建的后台管理系统，继承 RVP 全部技术栈，在数据库选型上切换为 **PostgreSQL**。整体架构为 **前后端分离 + Maven 聚合工程** 模式。

### 架构模式
- **架构风格：** 前后端分离（后端 REST API + 前端独立项目 tpl-manage-ui）
- **后端架构：** Spring Boot 四层分层（Controller → Service → Mapper → Entity）
- **构建方式：** Maven 聚合工程（级联编译 RVP + tpl-manage）
- **前端架构：** Vue 3 + TypeScript + Element Plus（tpl-manage-ui 独立项目）
- **通信协议：** HTTP/HTTPS + RESTful API + JSON
- **数据格式：** JSON

---

## 二、后端技术栈

### 2.1 核心框架（继承自 RVP）

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| JDK | 21 | 基础运行环境 | LTS 版本，支持虚拟线程 |
| Spring Boot | 4.1.0 | 应用框架 | RVP 依赖管理统一版本 |
| Sa-Token | 1.45.0 | 权限认证 | 登录校验、角色权限、JWT |
| Sa-Token JWT | — | Token 令牌 | 无状态认证 |
| Spring MVC | 6.x | Web 框架 | RESTful API |
| Jetty | 12.x | Web 容器 | 基于 Netty 的高性能容器 |

### 2.2 数据持久层

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| MyBatis-Plus | 3.5.17 | ORM 框架 | 通用 CRUD、分页、数据权限插件 |
| MyBatis | 3.5.19 | SQL 映射 | 动态 SQL、XML 映射 |
| HikariCP | — | 数据库连接池 | Spring 官方内置 |
| **PostgreSQL** | 15+ | 关系数据库 | **tpl-manage 特有，RVP 默认 MySQL** |
| PostgreSQL JDBC | — | 数据库驱动 | `org.postgresql:postgresql` |

> **PostgreSQL 选型理由：**「tpl-workspace」产品体系统一使用 PostgreSQL 作为业务主存储，tpl-manage 作为管理后台，与核心业务（tpl-app-api）保持一致数据库选型。相比 MySQL，PostgreSQL 在复杂查询、JSON 支持、地理空间数据等方面有优势。

### 2.3 缓存与分布式（继承自 RVP）

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| Redis | 7.0+ | 缓存数据库 | 热点数据缓存、分布式锁 |
| Redisson | 4.6.1 | Redis 客户端 | 基于 Netty，支持 90%+ Redis 命令 |
| Lock4j | 2.2.7 | 分布式锁 | 基于 Redisson 的分布式锁封装 |
| Spring Cache | — | 缓存注解 | 扩展支持过期时间等 |

### 2.4 认证与授权

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| Sa-Token | 1.45.0 | 权限认证 | 登录校验、角色校验、权限校验 |
| JWT | — | Token 令牌 | 无状态认证，JWT Secret Key 配置化 |
| BCrypt | — | 密码加密 | 用户密码 BCrypt 加密存储 |
| JustAuth | 3.0.1 | 第三方登录 | 已配置但未启用（sys_social 表已定义） |

### 2.5 文件存储

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| MinIO | — | 分布式文件存储 | Bucket: `tpl-manage` |
| AWS S3 SDK | 2.48.1 | 云存储兼容 | 支持 S3 协议厂家 |

### 2.6 接口文档

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| SpringDoc | 3.0.3 | API 文档 | 基于 Javadoc，零注解入侵 |
| Therapi | 0.15.0 | Javadoc 运行时 | 运行时读取 Javadoc 注释 |

### 2.7 工具库（继承自 RVP）

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| Hutool | 5.8.47 | 工具类库 | 上百种工具覆盖 90% 需求 |
| Lombok | 1.18.46 | 代码简化 | 自动生成 getter/setter/builder |
| MapStruct-Plus | 1.5.1 | 对象映射 | 类型安全的对象转换 |
| Jackson | — | JSON 处理 | Spring 官方序列化框架 |
| Ip2Region | 3.3.7 | IP 定位 | 离线 IP 地址定位库 |

### 2.8 消息与推送（配置存在，部分未启用）

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| Spring SSE | — | 服务端推送 | 默认消息推送通道 |
| Spring WebSocket | — | WebSocket | 已配置 |
| Mail-API | — | 邮件发送 | 已配置但 disabled |
| SMS4J | 3.3.5 | 短信融合 | 已配置但未启用 |

### 2.9 监控（部分未启用）

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| Spring Boot Admin | 4.1.2 | 服务监控 | Client 端已配置（dev 环境 disabled） |
| Actuator | — | 探针端点 | 全部端点已暴露 |

### 2.10 额外已配置但未启用的模块

| 技术 | 说明 |
|------|------|
| SnailJob 2.0.2 | 分布式任务调度（dev 环境 disabled） |
| SnailAI 1.1.1 | AI 服务（dev 环境 disabled） |
| Warm-Flow 1.8.9 | 工作流引擎（已配置 enabled） |
| LiteFlow 2.16.0 | 规则引擎（已配置 enabled，依赖 Warm-Flow） |
| MCP Server | Model Context Protocol（已配置 enabled） |
| MCP Client | MCP 客户端（disabled） |
| MQTT | Mica-MQTT 物联网协议（disabled） |
| Easy-Es | Elasticsearch ORM（disabled） |

---

## 三、前端技术栈（tpl-manage-ui，独立工程）

tpl-manage-ui 是基于 RuoYi-Vue-Plus 前端项目 `plus-ui` 改造的管理后台前端。

| 技术 | 用途 | 说明 |
|------|------|------|
| Vue.js 3.x | 前端框架 | Composition API |
| TypeScript | 类型系统 | 静态类型检查 |
| Vue Router 4.x | 路由管理 | SPA 路由 |
| Pinia 2.x | 状态管理 | 全局状态共享 |
| Element Plus | UI 组件库 | 桌面端组件库 |
| Axios | HTTP 客户端 | 请求封装 |
| Vite 5.x | 构建工具 | HMR 热重载 |

---

## 四、构建与部署

### 4.1 构建工具

| 技术 | 版本 | 用途 | 说明 |
|------|------|------|------|
| Maven | 3.9+ | 项目构建 | 聚合工程，级联编译 |
| spring-boot-maven-plugin | 4.x | 打包插件 | 生成可执行 JAR |

### 4.2 构建命令

```bash
# 在 tpl-manage 根目录执行，Maven 自动先编译 RVP 再编译 tpl-manage
mvn clean install -P dev

# 打包可执行 JAR
cd tpl-manage-admin && mvn package -P dev
# 输出：tpl-manage-admin/target/tpl-manage-admin.jar
```

### 4.3 部署环境

| 环境 | Profile | 数据库 | 说明 |
|------|---------|--------|------|
| 本地开发 | dev | PostgreSQL localhost:5432/tpl_manage | 默认激活 |
| Docker | docker | 按配置 | 容器化部署 |

---

## 五、开发环境要求

### 5.1 后端环境

| 软件 | 最低版本 | 推荐版本 | 说明 |
|------|----------|----------|------|
| JDK | 21 | 21 LTS | — |
| Maven | 3.6 | 3.9+ | 聚合工程需要 |
| PostgreSQL | 15 | 15+ | 业务主存储 |
| Redis | 7.0 | 7.0+ | 缓存 |
| MinIO | — | Latest | 文件存储 |

### 5.2 前端环境（tpl-manage-ui）

| 软件 | 最低版本 | 推荐版本 |
|------|----------|----------|
| Node.js | 18.x | 20.x LTS |
| npm/pnpm | 9.x | 10.x+ |

---

## 六、技术选型理由

### 6.1 为什么选择 Maven 聚合工程方案？

- **零代码侵入：** 不修改 RuoYi-Vue-Plus 任何源代码
- **级联编译：** `mvn clean install` 一条命令完成全部构建
- **版本一致性：** 通过 POM 依赖管理确保版本统一
- **模块复用：** 按需引用 RVP 模块，不引入不需要的功能

### 6.2 为什么选择 PostgreSQL 替代 MySQL？

- **产品体系统一：** 「tpl-workspace」产品线（tpl-app-api、tpl-manage）统一使用 PostgreSQL，便于运维和知识复用
- **功能优势：** 更好的 JSON 支持、更强的复杂查询能力、更完善的 ACID 特性
- **许可证友好：** PostgreSQL 使用宽松的 PostgreSQL License

### 6.3 为什么禁用验证码？

- **开发阶段：** 目前处于 MVP 开发阶段，验证码功能影响开发效率
- **安全规划：** 后续上线前会重新启用验证码和限流保护策略
