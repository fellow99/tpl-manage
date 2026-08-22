# 规格文档索引

**项目名称：** tpl-manage（tpl-workspace管理后台）  
**版本：** 1.0.0-SNAPSHOT  
**技术栈：** Spring Boot 4.1 + Sa-Token + MyBatis-Plus + PostgreSQL + Vue 3 + TypeScript + Element Plus  
**文档生成时间：** 2026-08-11  
**最后更新：** 2026-08-11  
**关联工程：** RuoYi-Vue-Plus 6.0.0（`../RuoYi-Vue-Plus/specs/`）  
**前端工程：** tpl-manage-ui（`../tpl-manage-ui/`，specs 已生成：13 份文档、2 个独有模块）

> **核心定位：** tpl-manage 是基于 RuoYi-Vue-Plus 6.0.0 的 Maven 聚合构建项目，为「tpl-workspace」产品提供管理后台能力。本工程**不修改 RVP 源码**，通过 `<modules>` 标签聚合 RVP 工程实现级联编译，仅包含 3 个自定义 Java 源文件。

---

## 一、文档总览

| 层级 | 分类 | 文档数量 | 说明 |
|------|------|---------|------|
| 整体 | 项目级顶层文档 | 7 | 架构、技术、宪法、结构、检查清单等全局文档 |
| 整体 | 整体规格文档 | 5 | overall-* 系列文档 |
| 模块 | tpl-manage 独有模块 | 2 | feature-120 ~ feature-121 |
| 模块 | 继承 RVP 模块 | 18+ | 引用 `../RuoYi-Vue-Plus/specs/` |
| 前端 | tpl-manage-ui 独有模块 | 2 | 引用 `../tpl-manage-ui/specs/`（feature-123 ~ feature-124） |
| **合计** | **3 目录 / 14 文件** | | |

> 注：tpl-manage 继承模块的文档不在此目录中，而是通过引用关联工程的 specs 目录。继承模块列表详见 [SPECS_CHECKLIST.md](./SPECS_CHECKLIST.md)。

---

## 二、项目级顶层文档

全局性的架构、技术、宪法等文档，定义项目基线和开发准则。

| 文档 | 路径 | 说明 |
|------|------|------|
| **方案总纲** | [ARCHITECTURE.md](./ARCHITECTURE.md) | 系统整体架构设计：聚合工程架构、分层结构、模块依赖、数据流 |
| **技术选型** | [TECH.md](./TECH.md) | 核心技术栈选型理由、版本、依赖说明、PostgreSQL 选型分析 |
| **宪法原则** | [constitution.md](./constitution.md) | 项目开发原则（继承 RVP + tpl-manage 特有）、编码规范、治理规则 |
| **项目结构** | [STRUCTURE.md](./STRUCTURE.md) | 源码目录结构、模块依赖关系、配置文件索引、菜单树清单 |
| **检查清单** | [SPECS_CHECKLIST.md](./SPECS_CHECKLIST.md) | 规格文档完成度追踪（整体 + 独有模块 + 继承模块） |

### 整体规格文档

跨模块的全局规格、方案、数据模型和接口模型。

| 文档 | 路径 | 说明 |
|------|------|------|
| **整体规格** | [overall-spec.md](./overall-spec.md) | 系统级功能规格：核心特性、用户故事、功能模块清单 |
| **整体方案** | [overall-plan.md](./overall-plan.md) | 系统级技术方案：聚合构建细节、认证流程、配置文件层次 |
| **数据模型** | [overall-data-model.md](./overall-data-model.md) | 全局数据实体定义：核心实体关系、PostgreSQL vs MySQL 差异、种子数据 |
| **接口模型** | [overall-api.md](./overall-api.md) | 全局 API 规范：tpl-manage 自定义接口 + 继承 RVP 接口清单 |
| **测试用例索引** | [overall-test-cases.md](./overall-test-cases.md) | 认证模块测试用例 + 聚合构建验证 + RVP 回归测试要点 |

---

## 三、tpl-manage 独有模块（feature-120 ~ feature-121）

> 以下为 tpl-manage 相比 RuoYi-Vue-Plus 新增的功能模块。编号从 feature-120 开始，与 RVP 的 feature-101 ~ feature-119 编号区间区分。

### feature-120 — 工程构建与部署 (Project Build)

> Maven 聚合工程、PostgreSQL 数据库迁移、级联编译、配置管理。

| 文档 | 链接 | 说明 |
|------|------|------|
| 功能规格 | [feature-120-project-build/spec.md](./feature-120-project-build/spec.md) | 工程构建功能规格：聚合编译、数据库初始化、配置管理 |
| 技术方案 | [feature-120-project-build/plan.md](./feature-120-project-build/plan.md) | 工程构建技术方案：POM 结构、级联编译原理、DDL 方言适配 |

### feature-121 — 自定义认证 (TPL Auth)

> 用户名+密码登录（简化版）、JWT Token 签发、验证码禁用、角色权限加载。

| 文档 | 链接 | 说明 |
|------|------|------|
| 功能规格 | [feature-121-tpl-auth/spec.md](./feature-121-tpl-auth/spec.md) | 认证功能规格：登录/登出/验证码、用户故事、验收场景 |
| 技术方案 | [feature-121-tpl-auth/plan.md](./feature-121-tpl-auth/plan.md) | 认证技术方案：AuthController 实现、Token 签发流程、权限加载 |

---

## 四、继承 RuoYi-Vue-Plus 模块（引用关联工程）

> 以下模块由 tpl-manage 通过 Maven 聚合方式从 RuoYi-Vue-Plus 6.0.0 继承，模块文档位于 `../RuoYi-Vue-Plus/specs/`。

### 系统管理模块（002 ~ 009）

| 编号 | 模块 | 英文名 | 说明 |
|------|------|--------|------|
| 002 | 用户管理 | User | 系统用户 CRUD、导入导出、角色分配、密码重置、状态管理 |
| 003 | 角色管理 | Role | 角色 CRUD、菜单权限分配、数据范围权限、用户分配 |
| 004 | 部门管理 | Dept | 组织机构树形结构，数据权限关联 |
| 005 | 岗位管理 | Post | 用户职务配置，岗位与用户关联 |
| 006 | 菜单管理 | Menu | 菜单/按钮权限配置、路由管理、权限标识 |
| 007 | 字典管理 | Dict | 系统固定数据维护（类型 + 数据），缓存刷新 |
| 008 | 参数配置 | Config | 动态系统参数管理，键值对缓存 |
| 009 | 通知公告 | Notice | 通知公告发布维护 |

### 系统监控模块（010 ~ 016）

| 编号 | 模块 | 英文名 | 状态 |
|------|------|--------|------|
| 010 | 操作日志 | OperLog | ✅ 继承 |
| 011 | 登录日志 | LoginLog | ✅ 继承 |
| 012 | 在线用户 | Online | ✅ 继承 |
| 013 | 定时任务 | Job | ⚠️ 未启用（SnailJob disabled） |
| 014 | 缓存监控 | Cache | ✅ 继承 |
| 015 | 服务监控 | Server | ✅ 继承 |
| 016 | 连接池监控 | Pool | ✅ 继承 |

### tpl-manage 未引入的 RVP 模块

| 编号 | 模块 | 原因 |
|------|------|------|
| 017 | 代码生成 (Gen) | 未引入 ruoyi-gen 依赖 |
| 101 | 工作流管理 (Workflow) | 未引入 ruoyi-workflow 依赖 |
| 102 | 示例模块 (Demo) | 未引入 ruoyi-demo 依赖 |
| 114 | AI 模块 (AI) | snail-ai enabled: false |

---

## 五、前端工程 tpl-manage-ui 独有模块（feature-123 ~ feature-124）

> tpl-manage-ui 是基于 RuoYi-Vue-Plus-UI v6.0.0 的管理后台前端工程，采用 **Vite alias 继承** 架构（不 fork 源码）。详细文档见 `../tpl-manage-ui/specs/README.md`。

### 前后端模块对应关系

| 后端模块 (tpl-manage) | 前端模块 (tpl-manage-ui) | 通信方式 |
|----------------------|------------------------|----------|
| feature-121 自定义认证 | feature-123 品牌定制与登录注册 | REST API (`POST /auth/login`) |
| — | feature-123 品牌首页/布局/国际化 | — |
| — | feature-124 消息推送布局集成 | SSE + WebSocket |

### feature-123 — 品牌定制与登录注册 (Branding)

> 登录/注册/首页视觉设计、布局样式、国际化文案「tpl-workspace」品牌替换、系统默认设置。

| 文档 | 链接 | 说明 |
|------|------|------|
| 功能规格 | [../tpl-manage-ui/specs/feature-123-branding/spec.md](../tpl-manage-ui/specs/feature-123-branding/spec.md) | 品牌定制功能规格 |
| 技术方案 | [../tpl-manage-ui/specs/feature-123-branding/plan.md](../tpl-manage-ui/specs/feature-123-branding/plan.md) | 品牌定制技术实现 |
| 测试用例 | [../tpl-manage-ui/specs/feature-123-branding/test-cases.md](../tpl-manage-ui/specs/feature-123-branding/test-cases.md) | UI 功能测试用例 |

### feature-124 — 消息推送布局集成 (Message Integration)

> SSE/WebSocket 消息推送在自定义布局中的初始化与集成。

| 文档 | 链接 | 说明 |
|------|------|------|
| 功能规格 | [../tpl-manage-ui/specs/feature-124-message-integration/spec.md](../tpl-manage-ui/specs/feature-124-message-integration/spec.md) | 消息推送规格 |
| 技术方案 | [../tpl-manage-ui/specs/feature-124-message-integration/plan.md](../tpl-manage-ui/specs/feature-124-message-integration/plan.md) | 消息推送集成方案 |
| 测试用例 | [../tpl-manage-ui/specs/feature-124-message-integration/test-cases.md](../tpl-manage-ui/specs/feature-124-message-integration/test-cases.md) | 消息推送测试用例 |

### tpl-manage-ui 顶层文档速查

| 文档 | 路径 | 说明 |
|------|------|------|
| 架构设计 | [../tpl-manage-ui/specs/ARCHITECTURE.md](../tpl-manage-ui/specs/ARCHITECTURE.md) | alias 继承架构、前端分层设计 |
| 技术选型 | [../tpl-manage-ui/specs/TECH.md](../tpl-manage-ui/specs/TECH.md) | Vue 3 + Vite + Element Plus 技术栈 |
| 项目结构 | [../tpl-manage-ui/specs/STRUCTURE.md](../tpl-manage-ui/specs/STRUCTURE.md) | 页面路由清单、组件目录结构 |
| 文档索引 | [../tpl-manage-ui/specs/README.md](../tpl-manage-ui/specs/README.md) | tpl-manage-ui 规范文档总索引 |

---

## 六、模块编号一览

| 编号 | 模块名 | 英文名 | 分类 | 备注 |
|------|--------|--------|------|------|
| 002 ~ 009 | 系统管理 | — | 继承 RVP | 引用关联工程 specs |
| 010 ~ 016 | 系统监控 | — | 继承 RVP | 部分未启用 |
| 017 ~ 019 | 系统工具 | — | 继承 RVP | 017 未引入 |
| feature-120 | 工程构建与部署 | Project Build | tpl-manage 独有 | |
| feature-121 | 自定义认证 | TPL Auth | tpl-manage 独有 | |
| feature-123 | 品牌定制与登录注册 | Branding | tpl-manage-ui 独有 | 前端模块 |
| feature-124 | 消息推送布局集成 | Message Integration | tpl-manage-ui 独有 | 前端模块 |

> 注：feature-101 ~ feature-119 为 RVP 增强/v6 新增模块编号，tpl-manage 选择性引入。

---

## 七、模块文档结构规范

每个模块目录 `feature-NNN-name/` 下包含以下 2 份标准文档：

| 文件 | 命名 | 说明 |
|------|------|------|
| 功能规格 | `spec.md` | 定义模块的功能需求、用户故事、验收标准（技术无关） |
| 技术方案 | `plan.md` | 模块的技术实现方案、架构决策、接口契约、文件清单 |

> 继承 RVP 的模块文档见 `../RuoYi-Vue-Plus/specs/NNN-name/` 目录。

---

## 八、快速导航

| 目标读者 | 推荐阅读顺序 |
|---------|-------------|
| **新加入开发者** | STRUCTURE.md → constitution.md → overall-spec.md → feature-121-tpl-auth/ |
| **架构师 / Tech Lead** | ARCHITECTURE.md → TECH.md → overall-plan.md → overall-data-model.md |
| **后端开发** | overall-api.md → overall-data-model.md → feature-121-tpl-auth/plan.md |
| **前端开发（tpl-manage-ui）** | [../tpl-manage-ui/specs/README.md](../tpl-manage-ui/specs/README.md) → overall-api.md → feature-123-branding/ |
| **运维 / SRE** | STRUCTURE.md → overall-plan.md → feature-120-project-build/plan.md |
| **测试 / QA** | overall-test-cases.md → SPECS_CHECKLIST.md |

---

## 九、关联工程速查

| 工程 | 路径 | specs 目录 | 说明 |
|------|------|-----------|------|
| RuoYi-Vue-Plus | `../RuoYi-Vue-Plus/` | `../RuoYi-Vue-Plus/specs/` | tpl-manage 的底层开发平台（Maven 聚合） |
| tpl-manage-ui | `../tpl-manage-ui/` | `../tpl-manage-ui/specs/`（✅ 已生成） | 管理后台前端工程（Vite alias 继承 RuoYi-Vue-Plus-UI） |
| RuoYi-Vue-Plus-UI | `../RuoYi-Vue-Plus-UI/` | `../RuoYi-Vue-Plus-UI/specs/` | tpl-manage-ui 的上游前端框架 |
| tpl-app-api | `../tpl-app-api/` | — | 「tpl-workspace」核心业务服务 |
| docs | `../docs/` | — | 产品概念设计、数据模型设计、模块设计文档 |

---

## 十、父工程规范文档（tpl-workspace 产品线）

tpl-workspace产品线级规范文档位于父工程 `../../specs/`，用于理解本工程在整体产品中的定位与约束。

| 文档 | 链接 | 说明 |
|------|------|------|
| 父工程规范索引 | [../../specs/README.md](../../specs/README.md) | 产品线级规范文档索引（22 份） |
| 整体架构 | [../../specs/ARCHITECTURE.md](../../specs/ARCHITECTURE.md) | 产品线整体架构、子工程依赖关系、部署拓扑 |
| 宪法原则 | [../../specs/constitution.md](../../specs/constitution.md) | 产品线级开发原则（10 条） |
| 整体数据模型 | [../../specs/overall-data-model.md](../../specs/overall-data-model.md) | 全局数据实体、表结构、数据字典 |

---

**文档维护者：** tpl-manage 开发团队  
**文档仓库：** `tpl-manage/specs/`  
**最后更新：** 2026-08-11
