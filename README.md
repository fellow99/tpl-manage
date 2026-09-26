# tpl-manage

> **tpl-workspace** 管理后台 — 基于 RuoYi-Vue-Plus 6.0.0 构建

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)](./tpl-manage-admin)
[![JDK](https://img.shields.io/badge/JDK-21-orange)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-336791?logo=postgresql)](./sql)
[![License](https://img.shields.io/badge/license-MIT-blue)](./LICENSE)

---

## 一、项目简介


### 产品关系

```
tpl-workspace 产品线
├── tpl-app-web      ← 用户端 Web 应用（Vue 3 + TypeScript）
├── tpl-manage       ← 管理后台服务端（本项目）
├── tpl-manage-ui    ← 管理后台前端（Vue 3 + Element Plus）
├── tpl-app-android  ← Android 客户端
├── tpl-app-harmony  ← 鸿蒙客户端
└── tpl-app-mini     ← 微信小程序
```

### 核心定位

- **用户管理：** 管理 tpl-workspace 平台的注册用户，查看/禁用/解锁
- **系统管理：** 角色权限、菜单配置、操作日志等（继承 RuoYi-Vue-Plus）

---

## 二、技术架构

### 2.1 栈

| 层级 | 技术 | 说明 |
|------|------|------|
| **后端框架** | Spring Boot 4.1 + JDK 21 | RVP 依赖管理统一版本 |
| **认证授权** | Sa-Token 1.45.0 + JWT | 无状态认证，Sa-Token 注解式权限校验 |
| **ORM** | MyBatis-Plus 3.5.17 | 通用 CRUD、分页、数据权限插件 |
| **数据库** | PostgreSQL 15+ | 与核心业务服务统一选型 |
| **缓存** | Redis 7.0+ (Redisson 4.6.1) | 缓存、分布式锁 |
| **文件存储** | MinIO | Bucket: `tpl-manage` |
| **Web 容器** | Jetty 12 (Netty) | 高性能异步容器 |
| **前端（关联）** | Vue 3 + TypeScript + Element Plus | tpl-manage-ui 独立工程 |
| **接口文档** | SpringDoc (javadoc) | 自动生成，零注解入侵 |

### 2.2 与 RuoYi-Vue-Plus 的关系

```
tpl-manage (本工程)
├── pom.xml                     ← Maven 聚合 POM
│   ├── <modules>
│   │   ├── ../RuoYi-Vue-Plus/  ← 聚合 RVP 全部模块（不修改源码）
│   │   └── tpl-manage-admin/    ← 启动模块（3 个自定义 Java 文件）
│   └── <dependencyManagement>
│       └── org.dromara:ruoyi-vue-plus:6.0.0  ← 导入 RVP BOM
│
└── sql/
    └── 001-init-sys.sql  ← PostgreSQL 建表脚本 + 种子数据
```

**设计原则：**
- **不修改 RVP 任何源码**，通过 Maven `<modules>` 聚合实现级联编译
- 双包扫描：`scanBasePackages = {"org.fellow99.tpl", "org.dromara"}`
- 自定义代码仅 3 个 Java 文件，核心能力 95% 来自 RVP
- 配置文件覆盖（`application.yml`）替代修改 RVP 配置

### 2.3 自定义代码清单

| 文件 | 说明 |
|------|------|
| `TplManageApplication.java` | Spring Boot 启动类，双包扫描 |
| `AuthController.java` | 用户名+密码登录（简化版），`POST /auth/login`、`POST /auth/logout` |
| `CaptchaController.java` | 验证码接口（MVP 阶段已禁用，返回 `captchaEnabled: false`） |

### 2.4 关键差异（vs RuoYi-Vue-Plus）

| 维度 | RuoYi-Vue-Plus | tpl-manage |
|------|---------------|-----------|
| 数据库 | MySQL | **PostgreSQL** |
| 端口 | 8080 | **8081** |
| 应用名 | ruoyi-admin | **tpl-manage** |
| 包命名空间 | org.dromara | **org.fellow99.tpl** |
| 验证码 | 启用 | **禁用（MVP）** |
| 登录方式 | 多方式（密码/短信/邮箱/社交） | **简化：用户名+密码** |
| 代码生成 | ✅ | ❌ 未引入 |
| 构建方式 | 独立 Maven 工程 | **聚合 RVP + 级联编译** |

---

## 三、快速开始

### 3.1 环境要求

| 软件 | 版本要求 |
|------|----------|
| JDK | 21 LTS |
| Maven | 3.9+ |
| PostgreSQL | 15+ |
| Redis | 7.0+ |
| MinIO | Latest |

### 3.2 数据库初始化

```bash
# 1. 创建数据库
psql -U postgres -c "CREATE DATABASE tpl_manage;"

# 2. 执行初始化脚本（建表 + 种子数据）
psql -U postgres -d tpl_manage -f sql/001-init-sys.sql
```

种子数据包含：
- 管理员账号：`admin` / `admin123`
- 系统菜单树（系统管理 + 系统监控）
- 三个默认角色：超级管理员 / 本部门及以下 / 仅本人

### 3.3 启动服务

```bash
# 在 tpl-manage 根目录执行（级联编译 RVP + tpl-manage）
mvn clean install -P dev

# 启动
java -jar tpl-manage-admin/target/tpl-manage-admin.jar --spring.profiles.active=dev
```

服务启动后：
- **API 服务：** http://localhost:8081
- **API 文档（SpringDoc）：** http://localhost:8081/swagger-ui.html
- **登录接口：** `POST /auth/login` — `{"username":"admin","password":"admin123"}`

### 3.4 配置说明

核心配置文件：

| 文件 | 说明 |
|------|------|
| `application.yml` | 全局配置（端口、Sa-Token、日志、MCP 等） |
| `application-dev.yml` | 开发环境（PostgreSQL/Redis/MinIO 连接信息） |
| `application-docker.yml` | Docker 环境配置 |

关键配置项：
```yaml
server.port: 8081                              # 服务端口
spring.datasource.url: jdbc:postgresql://...   # PostgreSQL 连接
sa-token.jwt-secret-key: abcdefghij...          # JWT 签名密钥（生产环境需更换）
captcha.enable: false                           # 验证码（MVP 禁用）
```

---

## 四、项目结构

```
tpl-manage/
├── pom.xml                          # 聚合工程 POM
├── README.md                        # 本文件
├── sql/
│   └── 001-init-sys.sql      # PostgreSQL 建表 + 种子数据
├── tpl-manage-admin/                 # 启动模块
│   ├── pom.xml                      # 依赖声明（RVP 模块 + PostgreSQL 驱动）
│   └── src/main/
│       ├── java/org/fellow99/tpl/manage/
│       │   ├── TplManageApplication.java
│       │   └── controller/
│       │       ├── AuthController.java
│       │       └── CaptchaController.java
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           ├── application-docker.yml
│           ├── banner.txt
│           └── logback.xml
└── specs/                           # 规范文档（详见 specs/README.md）
    ├── README.md
    ├── ARCHITECTURE.md
    ├── TECH.md
    ├── STRUCTURE.md
    ├── constitution.md
    ├── overall-spec.md
    ├── overall-plan.md
    ├── overall-data-model.md
    ├── overall-api.md
    ├── overall-test-cases.md
    ├── SPECS_CHECKLIST.md
    ├── feature-120-project-build/
    └── feature-121-tpl-auth/
```

---

## 五、数据库表规范

### 5.1 命名约定

| 类型 | 命名格式 | 示例 |
|------|---------|------|
| RuoYi 标准表 | `sys_*` | `sys_user`、`sys_role`、`sys_config` |
| tpl-workspace业务表 | `tpl_*` | `tpl_user_profile` |
| 业务视图 | `tpl_*_view` | `tpl_user_view` |

> **原则：** RuoYi 标准表（`sys_*`）不做任何修改，业务扩展通过 `tpl_*` 表实现。

### 5.2 tpl-manage 直接维护的表

| 表 | 用途 |
|------|------|
| `sys_user` + `tpl_user_profile` | 用户账号与扩展信息（通过视图 `tpl_user_view` 查询） |
| `sys_dict_type` / `sys_dict_data` | 数据字典 |
| `sys_config` | 系统配置（如注册开关） |
| `sys_role` / `sys_menu` / `sys_dept` / `sys_post` | RBAC 权限体系 |

---

## 六、API 接口

### 6.1 自定义接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/login` | 用户名+密码登录，返回 access_token |
| POST | `/auth/logout` | 退出登录 |
| GET | `/auth/code` | 验证码状态（当前 disabled） |

### 6.2 继承 RVP 接口

继承 RuoYi-Vue-Plus `ruoyi-system` 模块的全部 REST API：

| 模块 | 路径前缀 | 主要功能 |
|------|---------|----------|
| 用户管理 | `/system/user/**` | CRUD、角色分配、密码重置 |
| 角色管理 | `/system/role/**` | CRUD、菜单权限分配、数据范围 |
| 菜单管理 | `/system/menu/**` | 菜单树、路由配置、按钮权限 |
| 部门管理 | `/system/dept/**` | 组织架构树 |
| 字典管理 | `/system/dict/**` | 字典类型+数据维护、缓存刷新 |
| 参数配置 | `/system/config/**` | 键值对系统参数 |
| 通知公告 | `/system/notice/**` | 公告发布维护 |
| 操作日志 | `/monitor/operlog/**` | 日志查询、导出 |
| 登录日志 | `/monitor/loginInfo/**` | 登录记录、账户解锁 |
| 在线用户 | `/monitor/online/**` | 在线监控、强制下线 |
| 缓存监控 | `/monitor/cache` | Redis 缓存信息 |
| 文件管理 | `/system/oss/**` | MinIO 文件上传/下载 |

完整 API 清单见：[specs/overall-api.md](./specs/overall-api.md)

---

## 七、文档导航

### 7.1 开发文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 架构设计 | [specs/ARCHITECTURE.md](./specs/ARCHITECTURE.md) | 聚合工程架构、分层设计、数据流 |
| 技术选型 | [specs/TECH.md](./specs/TECH.md) | 技术栈、版本、PostgreSQL 选型分析 |
| 项目结构 | [specs/STRUCTURE.md](./specs/STRUCTURE.md) | 目录结构、菜单树、配置索引 |
| 宪法原则 | [specs/constitution.md](./specs/constitution.md) | 开发规范、命名约定、构建原则 |

### 7.2 规格文档

| 文档 | 路径 | 说明 |
|------|------|------|
| 整体规格 | [specs/overall-spec.md](./specs/overall-spec.md) | 功能列表、用户故事 |
| 整体方案 | [specs/overall-plan.md](./specs/overall-plan.md) | 技术方案、认证流程 |
| 数据模型 | [specs/overall-data-model.md](./specs/overall-data-model.md) | 实体关系、PostgreSQL vs MySQL 差异 |
| API 接口 | [specs/overall-api.md](./specs/overall-api.md) | 自定义接口 + 继承 RVP 接口 |
| 测试用例 | [specs/overall-test-cases.md](./specs/overall-test-cases.md) | 认证测试 + 构建验证 |
| 文档索引 | [specs/README.md](./specs/README.md) | 全部规范文档导航 |

### 7.3 关联工程文档

| 工程 | 路径 | 说明 |
|------|------|------|
| RuoYi-Vue-Plus specs | `../RuoYi-Vue-Plus/specs/` | RVP 模块规范文档 |
| **tpl-manage-ui** | `../tpl-manage-ui/` | **管理后台前端工程** |
| tpl-manage-ui specs | `../tpl-manage-ui/specs/` | 前端规范文档（13 份，含 feature-123/124） |
| 产品概念设计 | `../docs/产品概念设计.md` | 产品整体规划 |
| 数据模型设计 | `../docs/数据模型设计.md` | 数据库详细设计 |

---

## 八、参考

- [RuoYi-Vue-Plus 官方文档](https://plus-doc.dromara.org)
- [RuoYi-Vue-Plus Gitee](https://gitee.com/dromara/RuoYi-Vue-Plus)
- [Sa-Token 文档](https://sa-token.cc)
- [MyBatis-Plus 文档](https://baomidou.com)

---

## License

本项目基于 [MIT License](./LICENSE) 开源。
