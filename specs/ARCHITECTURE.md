# 整体架构文档 (ARCHITECTURE.md)

**版本：** 1.0.0-SNAPSHOT  
**最后更新：** 2026-08-11  
**项目：** tpl-manage（tpl-workspace管理后台）  
**关联工程：** RuoYi-Vue-Plus 6.0.0

---

## 一、系统架构概述

### 1.1 架构风格

tpl-manage 采用 **Maven 聚合工程 + 前后端分离** 架构，以 RuoYi-Vue-Plus 6.0.0 为基础平台，在其上构建「tpl-workspace」管理后台功能。

```
┌─────────────────────────────────────────────────────────────┐
│     tpl-manage-ui（管理前端，独立工程，Vite alias 继承）        │
│     Vue 3 + TS + Element Plus（15%定制, 85%继承@plus-ui）     │
│     定制模块: feature-123(品牌) + feature-124(消息推送)         │
│     API 代理: /dev-api/* → localhost:8081                      │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ HTTP/HTTPS + JSON
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    tpl-manage（后端）                          │
│           Spring Boot 4.1 + Sa-Token + Jetty 12             │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         tpl-manage-admin（启动模块，端口 8081）         │   │
│  │  ┌──────────┬──────────┬────────────────────────┐    │   │
│  │  │AuthCtrl  │CaptchaCtrl│  TplManageApplication  │    │   │
│  │  └──────────┴──────────┴────────────────────────┘    │   │
│  └──────────────────────┬───────────────────────────────┘   │
│                         │ 依赖                               │
│  ┌──────────────────────┴───────────────────────────────┐   │
│  │            RuoYi-Vue-Plus 6.0.0                       │   │
│  │  ruoyi-system │ ruoyi-api │ ruoyi-common-*            │   │
│  │  (用户/角色/菜单/部门/岗位/字典/参数/通知/OSS/客户端)    │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              │
              ┌───────────────┼───────────────┐
              ▼               ▼               ▼
         ┌─────────┐    ┌─────────┐    ┌─────────┐
         │PostgreSQL│   │  Redis  │   │  MinIO  │
         │  数据库   │   │  缓存   │   │  文件存储│
         └─────────┘    └─────────┘    └─────────┘
```

### 1.2 架构特点

- **聚合工程：** 不修改 RVP 源码，通过 Maven `<modules>` 聚合实现级联编译
- **双包扫描：** `@SpringBootApplication(scanBasePackages = {"org.fellow99.tpl", "org.dromara"})`，合并 tpl-manage 自定义代码与 RVP 模块
- **继承式开发：** tpl-manage 代码量仅 3 个 Java 文件，核心能力源自 RVP
- **数据库切换：** PostgreSQL 替代 MySQL，通过自定义 SQL 脚本初始化库表

### 1.3 与 RuoYi-Vue-Plus 的架构关系

```
RuoYi-Vue-Plus (关联工程)
├── ruoyi-admin/          ─→ 被 tpl-manage-admin 替代
├── ruoyi-common/ (25个)  ─→ 被 tpl-manage-admin 选择性依赖
├── ruoyi-api/            ─→ 被 tpl-manage-admin 直接依赖
├── ruoyi-modules/        ─→ ruoyi-system 被依赖，其余未引入
│   ├── ruoyi-system      ─→ ✅ 依赖
│   ├── ruoyi-gen         ─→ ❌ 未引入
│   ├── ruoyi-job         ─→ ❌ 未引入
│   ├── ruoyi-demo        ─→ ❌ 未引入
│   ├── ruoyi-workflow    ─→ ❌ 未引入
│   └── ruoyi-ai          ─→ ❌ 未引入
└── ruoyi-extend/         ─→ 独立部署，不直接依赖

tpl-manage (本工程)
├── pom.xml               ─→ 聚合 RVP + tpl-manage-admin
├── tpl-manage-admin/      ─→ 自定义启动入口 + 认证接口
└── sql/                  ─→ PostgreSQL 初始化脚本
```

---

## 二、系统分层架构

tpl-manage 继承 RVP 的四层分层架构：

```
┌────────────────────────────────────────────────────────────┐
│                    Controller 层                            │
│  • org.fellow99.tpl.manage.controller（自定义）               │
│    - AuthController: POST /auth/login, POST /auth/logout   │
│    - CaptchaController: GET /auth/code                     │
│  • org.dromara.**.controller（继承 RVP）                   │
│    - 用户/角色/菜单/部门/岗位/字典/参数/通知等 Controller    │
│  • 参数校验（Validation 注解）                              │
│  • 返回统一响应格式 R<V>                                    │
└────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌────────────────────────────────────────────────────────────┐
│                     Service 层（全部继承 RVP）               │
│  • 业务逻辑实现（接口 + 实现分离）                            │
│  • 事务控制（@Transactional）                                │
│  • 数据组装（MapStruct-Plus）                                │
└────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌────────────────────────────────────────────────────────────┐
│                     Mapper 层（全部继承 RVP）                │
│  • 数据库访问（BaseMapperPlus）                              │
│  • 数据权限过滤（PlusDataPermissionInterceptor）             │
│  • SQL 日志（SqlLogInterceptor）                             │
│  • 雪花 ID 生成                                              │
└────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌────────────────────────────────────────────────────────────┐
│                     Entity 层（全部继承 RVP）                │
│  • 数据实体定义（@TableName 映射）                            │
│  • 继承 BaseEntity（通用审计字段）                            │
│  • PostgreSQL DDL 通过自定义 SQL 脚本维护                     │
└────────────────────────────────────────────────────────────┘
```

---

## 三、自定义代码与 RVP 的融合

### 3.1 包扫描机制

```java
@SpringBootApplication(scanBasePackages = {"org.fellow99.tpl", "org.dromara"})
public class TplManageApplication extends SpringBootServletInitializer {
    // 同时扫描 tpl-manage 和 RVP 的所有 Spring 组件
}
```

### 3.2 类加载优先级

- `org.fellow99.tpl.manage.controller.AuthController` 与 RVP 内置的 `AuthController` 路径相同（`/auth`）但类名不冲突（不同包路径）
- tpl-manage 的 `AuthController` 提供简化的用户名+密码登录，RVP 的多方式登录接口未被加载（因为 RVP 的 `AuthController` 也在扫描范围内，但由于 Spring 的 `@RequestMapping` 路径映射机制，同路径的 Controller 可能产生冲突）

> [NEEDS CLARIFICATION]: RVP 的 AuthController 是否因路径冲突被覆盖，或两者共存导致歧义。实际运行时以类加载行为为准。

### 3.3 配置覆盖

tpl-manage 通过 `application.yml` 覆盖 RVP 的部分默认配置：

| 配置项 | RVP 默认值 | tpl-manage 值 |
|--------|-----------|-------------|
| server.port | 8080 | 8081 |
| spring.application.name | ruoyi-admin | tpl-manage |
| captcha.enable | true | false |
| sa-token.jwt-secret-key | — | abcdefghijklmnopqrstuvwxyz |
| minio.bucketName | ruoyi-vue-plus | tpl-manage |
| springdoc.info.title | RuoYi-Vue-Plus | tpl-workspace管理后台 |
| spring.datasource.driver | MySQL | PostgreSQL |

---

## 四、部署架构

### 4.1 服务拓扑

```
                    ┌───────────────┐
                    │  Nginx / 直连  │
                    │  (可选)        │
                    └───────┬───────┘
                            │
                            ▼
              ┌─────────────────────────┐
              │  tpl-manage-admin:8081   │
              │  (Spring Boot + Jetty)  │
              └────────────┬────────────┘
                           │
          ┌────────────────┼────────────────┐
          ▼                ▼                ▼
   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
   │ PostgreSQL  │  │    Redis    │  │    MinIO    │
   │   :5432     │  │   :6379     │  │   :9000     │
   │ tpl_manage   │  │   db:0      │  │ tpl-manage   │
   └─────────────┘  └─────────────┘  └─────────────┘
```

### 4.2 端口规划

| 服务 | 端口 | 协议 | 说明 |
|------|------|------|------|
| tpl-manage-admin | 8081 | HTTP | 管理后台 REST API |
| tpl-manage-ui | 8082 | HTTP | 前端开发服务器（Vite） |
| PostgreSQL | 5432 | TCP | 业务数据库 |
| Redis | 6379 | TCP | 缓存 |
| MinIO | 9000 | HTTP | 文件存储 |

---

## 五、数据流

### 5.1 请求处理流程

```
1. 用户请求 → tpl-manage-admin:8081
2. Filter 链：XSS 过滤 → API 解密（已禁用）
3. Sa-Token 拦截器：JWT 解析 → 登录校验 → 权限校验
4. Controller：参数校验 → 调用 Service（来自 RVP ruoyi-system）
5. Service：业务逻辑 → Mapper 调用（数据权限自动注入）
6. Mapper：MyBatis-Plus 执行 SQL → HikariCP → PostgreSQL
7. Response：Jackson 序列化 → JSON → 客户端
```

### 5.2 认证流程

```
用户登录：POST /auth/login {username, password}
  → AuthController.login()
  → userService.selectUserByUserName() → PostgreSQL
  → BCrypt.checkpw() → 密码验证
  → permissionService.getRolePermission() + getMenuPermission()
  → LoginHelper.login() → Sa-Token 签发 JWT
  → 返回 {access_token: "xxx"}
```

---

## 六、关键架构决策

### 6.1 Maven 聚合而非代码复制

选择通过 `<modules>` 标签聚合 RVP 工程，而非复制 RVP 代码到 tpl-manage 目录。这确保了：
- RVP 版本升级只需修改 POM 版本号
- 代码审查只需关注 3 个自定义 Java 文件
- CI/CD 构建一条命令完成

### 6.2 PostgreSQL 替代 MySQL

统一「tpl-workspace」产品线数据库选型，简化运维和知识复用。

### 6.3 极简自定义代码

tpl-manage 仅有 3 个 Java 文件，极致复用 RVP 已有能力，降低维护成本。

### 6.4 验证码禁用（开发阶段）

MVP 阶段禁用验证码以提升开发效率，后续上线前重新启用。
