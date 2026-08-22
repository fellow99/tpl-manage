# 工程构建与部署 功能规格

> Module: feature-120-project-build
> Status: Implemented
> Last Updated: 2026-08-11
> 来源：tpl-manage 独有

## 1. 模块概述

### 1.1 Purpose — 为什么需要这个模块

tpl-manage 需要在**不修改 RuoYi-Vue-Plus 源代码**的前提下，集成其全部能力并添加「tpl-workspace」管理后台特有的配置和功能。因此需要一个工程级的构建系统来管理这种"聚合式"的项目结构。

### 1.2 Problems Solved — 解决什么问题

- **源码分离** — RVP 代码与 tpl-manage 自定义代码物理隔离，互不污染
- **级联编译** — 一条命令完成 RVP + tpl-manage 的全部编译，开发体验流畅
- **版本管理** — 通过 POM 集中管理 RVP 版本号，升级只需改一处
- **数据库迁移** — PostgreSQL DDL 脚本独立维护，与 RVP 的 MySQL 脚本解耦

### 1.3 Scope — 包含和不包含

**包含：**
- Maven 聚合工程 POM 配置
- tpl-manage-admin 模块的依赖管理
- PostgreSQL 数据库初始化 SQL 脚本
- 应用配置文件（application.yml / application-dev.yml / application-docker.yml）

**不包含：**
- RuoYi-Vue-Plus 的源代码（通过 Maven 聚合引用）
- CI/CD 流水线配置（Jenkinsfile / GitHub Actions）
- Dockerfile 和容器编排（待后续补充）

---

## 2. 用户故事

- 作为开发者，我可以在 tpl-manage 根目录执行 `mvn clean install`，即可自动完成 RVP 和 tpl-manage 的全部编译，无需手动切换目录
- 作为开发者，我可以修改根 POM 中的 `<ruoyi-vue-plus.version>` 属性来升级 RVP 版本
- 作为运维人员，我可以执行 `sql/001-init-sys.sql` 在空 PostgreSQL 数据库中初始化全部表结构和种子数据
- 作为运维人员，我可以通过 `application-dev.yml` 覆盖默认配置来适配本地开发环境

---

## 3. 功能需求

### 3.1 Maven 聚合构建

- FR-120-001: 根 POM 必须通过 `<modules>` 标签将 `../RuoYi-Vue-Plus/` 和 `tpl-manage-admin/` 聚合在一起
- FR-120-002: 根 POM 必须通过 `<dependencyManagement>` 导入 RVP 的 BOM，确保依赖版本一致
- FR-120-003: 根 POM 必须在 `<properties>` 中定义 `ruoyi-vue-plus.version` 属性，支持集中升级
- FR-120-004: tpl-manage-admin 的 POM 必须将 `org.dromara:ruoyi-vue-plus` 作为 `<parent>`，继承 RVP 的版本管理
- FR-120-005: 级联编译必须保证 RVP 模块先编译，tpl-manage-admin 后编译

### 3.2 数据库初始化

- FR-120-006: SQL 脚本必须使用 PostgreSQL 方言编写（不使用 MySQL 特有语法）
- FR-120-007: 脚本必须使用 `CREATE TABLE IF NOT EXISTS` 确保幂等执行
- FR-120-008: 种子数据必须使用固定雪花 ID，确保不同环境可复现
- FR-120-009: 脚本必须包含完整的系统表定义：sys_user, sys_role, sys_menu, sys_dept, sys_post 及所有关联表
- FR-120-010: 脚本必须包含默认管理员用户 admin 的种子数据
- FR-120-011: 脚本必须包含完整的菜单树种子数据

### 3.3 配置管理

- FR-120-012: `application.yml` 必须覆盖 RVP 默认配置：端口 8081、应用名 tpl-manage、验证码禁用
- FR-120-013: `application-dev.yml` 必须包含 PostgreSQL 数据源配置（driver/url/username/password）
- FR-120-014: `application-dev.yml` 必须包含 Redis、Redisson、MinIO 连接配置
- FR-120-015: 配置必须分层：全局配置 → 环境配置，通过 `spring.profiles.active` 切换

### 3.4 模块依赖

- FR-120-016: tpl-manage-admin 必须依赖 `org.postgresql:postgresql`（PostgreSQL JDBC 驱动）
- FR-120-017: tpl-manage-admin 必须依赖 RuoYi-Vue-Plus 的核心模块：ruoyi-common-doc, ruoyi-common-log, ruoyi-common-security, ruoyi-common-web, ruoyi-api, ruoyi-system
- FR-120-018: 打包输出文件名为 `tpl-manage-admin.jar`

---

## 4. 关键实体

| 实体 | 描述 | 关键属性 |
|------|------|----------|
| Maven POM | 聚合工程 Maven 配置 | groupId=org.fellow99.tpl, artifactId=tpl-manage, version=1.0.0-SNAPSHOT |
| SQL 初始化脚本 | PostgreSQL DDL + DML | 文件路径: sql/001-init-sys.sql |
| 应用配置 | Spring Boot 配置文件 | application.yml, application-dev.yml, application-docker.yml |

---

## 5. 验收场景

### Scenario: 级联编译成功

- Given RVP 和 tpl-manage 工程目录结构正确
- When 在 tpl-manage 根目录执行 `mvn clean install -P dev`
- Then RVP 所有模块编译成功，然后 tpl-manage-admin 编译打包成功

### Scenario: PostgreSQL 数据库初始化

- Given 已创建空数据库 `tpl_manage`
- When 执行 `sql/001-init-sys.sql`
- Then 所有系统表创建成功，种子数据插入成功，admin 用户可用

### Scenario: 通过配置切换环境

- Given 服务已编译打包
- When 使用 `--spring.profiles.active=docker` 启动参数
- Then 服务加载 application-docker.yml 中的配置

---

## 6. 非功能性需求

- 构建时间：级联编译在常规开发机上 5 分钟内完成
- 幂等性：SQL 脚本可重复执行，不会因表已存在而报错
- 可维护性：版本号集中在根 POM 的 `<properties>` 中管理

---

## 7. 假设与约束

- 假设 RVP 工程位于 `../RuoYi-Vue-Plus/` 相对路径
- 假设 Maven 版本 ≥ 3.9
- 假设 JDK 版本 = 21
- 假设 PostgreSQL 数据库版本 ≥ 15

---

## 8. 依赖

- **上游依赖：** RuoYi-Vue-Plus 6.0.0（通过 Maven 聚合）
- **下游依赖：** 无（本模块为工程基础设施，被所有模块依赖）
