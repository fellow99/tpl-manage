# tpl-manage 项目结构文档 (STRUCTURE.md)

**版本：** 1.0.0-SNAPSHOT  
**生成时间：** 2026-08-11  
**项目：** tpl-manage（tpl-workspace管理后台）  
**关联工程：** RuoYi-Vue-Plus 6.0.0（`../RuoYi-Vue-Plus/`）  
**前端工程：** tpl-manage-ui（`../tpl-manage-ui/`）

---

## 一、项目概述

tpl-manage 是「tpl-workspace」产品的管理后台服务端工程，基于 **RuoYi-Vue-Plus 6.0.0** 开发平台构建。采用 **Maven 聚合工程** 方案，通过 `<modules>` 标签将 RuoYi-Vue-Plus 工程逻辑上聚合在一起，实现级联编译。

- **groupId：** org.fellow99.tpl
- **artifactId：** tpl-manage
- **版本：** 1.0.0-SNAPSHOT
- **Java：** 21
- **数据库：** PostgreSQL（RuoYi-Vue-Plus 使用 MySQL，此为关键差异）
- **端口：** 8081

### 1.1 与 RuoYi-Vue-Plus 的关系

```
tpl-manage（聚合工程 POM）
├── modules
│   ├── ../RuoYi-Vue-Plus/       ← Maven 模块聚合（级联编译）
│   └── tpl-manage-admin/          ← tpl-manage 启动入口模块
```

- **不修改 RVP 源代码**，也不复制 RVP 代码
- **直接依赖 RVP** 各模块的 POM
- **级联编译**：`mvn clean install` 自动先编译 RVP，再编译 tpl-manage

---

## 二、tpl-manage 顶层目录结构

```
tpl-manage/
├── pom.xml                          # 聚合工程 POM（Maven 父工程）
├── README.md                        # 项目说明（简要）
├── mvnw / mvnw.cmd                  # Maven Wrapper
├── .gitignore                       # Git 忽略配置
├── .editorconfig                    # 编辑器配置（继承 RVP 规范）
├── sql/                             # 数据库初始化脚本
│   └── 001-init-sys.sql       # PostgreSQL 建表 + 种子数据
├── tpl-manage-admin/                 # 管理后台启动模块
│   ├── pom.xml                      # 模块依赖声明
│   └── src/
│       └── main/
│           ├── java/org/fellow99/tpl/manage/
│           │   ├── TplManageApplication.java      # Spring Boot 启动类
│           │   └── controller/
│           │       ├── AuthController.java       # 登录/登出接口
│           │       └── CaptchaController.java    # 验证码（禁用状态）
│           └── resources/
│               ├── application.yml               # 主配置（含所有模块配置）
│               ├── application-dev.yml           # 开发环境配置（数据源/Redis/MinIO）
│               ├── application-docker.yml        # Docker 环境配置
│               ├── banner.txt                    # 启动横幅
│               └── logback.xml                   # 日志配置
└── specs/                           # 规范文档目录（本目录）
```

---

## 三、tpl-manage-admin 模块依赖

### 3.1 直接依赖（pom.xml 中声明）

| 依赖 | GroupId | 用途 |
|------|---------|------|
| ruoyi-common-doc | org.dromara | SpringDoc API 接口文档 |
| ruoyi-common-mail | org.dromara | 邮件发送服务 |
| ruoyi-common-log | org.dromara | 操作日志 AOP 记录 |
| ruoyi-common-security | org.dromara | 安全配置（URL 收集/安全属性） |
| ruoyi-common-web | org.dromara | Web 配置（CORS/XSS/验证码） |
| ruoyi-api | org.dromara | 跨模块 RPC 接口定义 |
| ruoyi-system | org.dromara | 系统管理业务模块（用户/角色/菜单/部门/岗位/字典/参数/通知/客户端/OSS） |
| spring-boot-admin-starter-client | de.codecentric | Spring Boot Admin 客户端监控 |
| postgresql | org.postgresql | PostgreSQL JDBC 驱动 |

### 3.2 间接依赖（通过 RVP 父 POM 传递）

通过 RVP 的 Maven 依赖管理，以下模块以传递方式引入：ruoyi-common-core、ruoyi-common-satoken、ruoyi-common-mybatis、ruoyi-common-redis、ruoyi-common-json、ruoyi-common-oss 等。

### 3.3 未引入的 RVP 模块

| 模块 | 说明 |
|------|------|
| ruoyi-gen | 代码生成器（未使用） |
| ruoyi-job | 定时任务模块（未使用，SnailJob 已禁用） |
| ruoyi-demo | 示例模块（未使用） |
| ruoyi-workflow | 工作流模块（未使用） |
| ruoyi-ai | AI 模块（未使用） |
| ruoyi-common-encrypt | 数据加解密（已配置但未启用） |
| ruoyi-common-sensitive | 数据脱敏（未引入） |
| ruoyi-common-translation | 数据翻译（未引入） |
| ruoyi-common-excel | Excel 导入导出（未引入） |
| ruoyi-common-sms | 短信服务（已配置但未启用） |
| ruoyi-common-social | 社交登录（已配置但未启用） |
| ruoyi-common-push | 消息推送（配置存在但功能未验证） |
| ruoyi-common-liteflow | LiteFlow 规则引擎（配置存在但未验证） |
| ruoyi-common-mqtt | MQTT 协议（已配置 disabled） |
| ruoyi-common-mcp | MCP 协议（服务端 enabled，客户端 disabled） |
| ruoyi-common-elasticsearch | Elasticsearch（已配置 disabled） |

---

## 四、自定义 Java 源码清单

tpl-manage 仅有 **3 个 Java 源文件**，其余全部复用 RVP 代码。

### 4.1 TplManageApplication.java

| 属性 | 值 |
|------|------|
| 路径 | `tpl-manage-admin/src/main/java/org/fellow99/tpl/manage/TplManageApplication.java` |
| 基类 | `SpringBootServletInitializer` |
| 注解 | `@SpringBootApplication(scanBasePackages = {"org.fellow99.tpl", "org.dromara"})` |
| 说明 | 扫描 tpl-manage 和 RVP 两个包路径，合并启动 |

### 4.2 AuthController.java

| 属性 | 值 |
|------|------|
| 路径 | `tpl-manage-admin/src/main/java/org/fellow99/tpl/manage/controller/AuthController.java` |
| 路径前缀 | `/auth` |
| 注解 | `@SaIgnore`（无需认证） |
| 端点 | `POST /auth/login` — 用户名+密码登录，返回 access_token |
| 端点 | `POST /auth/logout` — 退出登录 |

### 4.3 CaptchaController.java

| 属性 | 值 |
|------|------|
| 路径 | `tpl-manage-admin/src/main/java/org/fellow99/tpl/manage/controller/CaptchaController.java` |
| 注解 | `@SaIgnore`（无需认证） |
| 端点 | `GET /auth/code` — 获取验证码（已禁用，返回 `captchaEnabled: false`） |

---

## 五、配置文件索引

| 文件 | 路径 | 说明 |
|------|------|------|
| 主配置 | `tpl-manage-admin/src/main/resources/application.yml` | 全局配置：端口 8081、Sa-Token JWT、MyBatis-Plus、SpringDoc、Lock4j、Warm-Flow、MCP、MQTT、ES |
| 开发环境 | `tpl-manage-admin/src/main/resources/application-dev.yml` | PostgreSQL 数据源、Redis、Redisson、MinIO、SBA 客户端、SnailJob、SnailAI、邮件、短信、社交登录 |
| Docker 环境 | `tpl-manage-admin/src/main/resources/application-docker.yml` | Docker 环境配置 |
| 日志配置 | `tpl-manage-admin/src/main/resources/logback.xml` | Logback 日志配置 |
| 启动横幅 | `tpl-manage-admin/src/main/resources/banner.txt` | "tpl-workspace TPL-MANAGE 管理后台" ASCII 艺术字 |

---

## 六、数据库脚本索引

| 脚本 | 路径 | 说明 |
|------|------|------|
| 初始化脚本 | `sql/001-init-sys.sql` | PostgreSQL DDL + 种子数据 |

### 6.1 初始化脚本包含的表

| 表名 | 说明 | 类型 |
|------|------|------|
| sys_social | 第三方平台授权表（JustAuth） | 表定义 |
| sys_dept | 部门表（含种子数据） | 表定义 + 数据 |
| sys_user | 用户信息表（admin 用户种子数据） | 表定义 + 数据 |
| sys_post | 岗位信息表（含种子数据） | 表定义 + 数据 |
| sys_role | 角色信息表（superadmin/test1/test2 种子数据） | 表定义 + 数据 |
| sys_menu | 菜单权限表（菜单树种子数据） | 表定义 + 数据 |
| sys_user_role | 用户和角色关联表（admin→superadmin） | 表定义 + 数据 |
| sys_role_menu | 角色和菜单关联表（test1 角色授权） | 表定义 + 数据 |
| sys_role_dept | 角色和部门关联表 | 表定义 |
| sys_user_post | 用户与岗位关联表（admin→根岗位） | 表定义 + 数据 |
| sys_oper_log | 操作日志记录 | 表定义 |
| sys_dict_type | 字典类型表（含种子数据） | 表定义 + 数据 |
| sys_dict_data | 字典数据表（含种子数据） | 部分可见（截断） |

### 6.2 菜单结构（sys_menu 种子数据）

tpl-manage 的自定义菜单结构与 RVP 类似但进行了简化：

```
系统管理 (M)
├── 用户管理 (C) — system/user/index
├── 角色管理 (C) — system/role/index
├── 菜单管理 (C) — system/menu/index
├── 部门管理 (C) — system/dept/index
├── 岗位管理 (C) — system/post/index
├── 字典管理 (C) — system/dict/index
├── 参数设置 (C) — system/config/index
├── 通知公告 (C) — system/notice/index
├── 日志管理 (M)
│   ├── 操作日志 (C) — monitor/operlog/index
│   └── 登录日志 (C) — monitor/logininfo/index
├── 文件管理 (C) — system/oss/index
│   └── 文件配置管理 (C，隐藏) — system/oss/config
├── 客户端管理 (C) — system/client/index
├── 分配用户 (C，隐藏)
└── 分配角色 (C，隐藏)

系统监控 (M)
├── 在线用户 (C) — monitor/online/index
├── 缓存监控 (C) — monitor/cache/index
└── Admin监控 (C) — monitor/admin/index
```

---

## 七、关键差异：tpl-manage vs RuoYi-Vue-Plus

| 维度 | RuoYi-Vue-Plus 6.0.0 | tpl-manage 1.0.0 |
|------|----------------------|-----------------|
| 数据库 | MySQL（主） | PostgreSQL |
| 端口 | 8080 | 8081 |
| 应用名 | ruoyi-admin | tpl-manage |
| 包命名空间 | org.dromara | org.fellow99.tpl + org.dromara |
| 构建方式 | 独立 Maven 工程 | 聚合 RVP + 自建 admin 模块 |
| 验证码 | 默认启用 | 禁用 |
| 登录方式 | 多方式（密码/短信/邮箱/社交/小程序） | 简化：用户名+密码 |
| 代码生成 | ✅ 引入 ruoyi-gen | ❌ 未引入 |
| 定时任务 | ✅ SnailJob | ⚠️ 已配置但 disabled |
| 工作流 | ✅ Warm-Flow | ⚠️ 已配置但未引入 ruoyi-workflow |
| AI 模块 | ✅ SnailAI | ⚠️ 已配置但 disabled |
| 前端工程 | plus-ui（独立项目） | tpl-manage-ui（独立项目） |
