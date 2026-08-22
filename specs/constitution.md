# 宪法原则文档 (constitution.md)

**版本：** 1.0.0-SNAPSHOT  
**最后更新：** 2026-08-11  
**项目：** tpl-manage（tpl-workspace管理后台）  
**关联工程：** RuoYi-Vue-Plus 6.0.0

> **说明：** tpl-manage 继承 RuoYi-Vue-Plus 的全部宪法原则，本章额外补充 tpl-manage 项目特有的原则和约定。

---

## 一、继承原则（来自 RuoYi-Vue-Plus）

tpl-manage 在代码层面完全继承 RuoYi-Vue-Plus 的宪法原则。以下 RVP 原则同样适用于本工程：

1. **简单优于复杂** — 代码简洁易懂，避免过度设计
2. **约定优于配置** — 遵循 RVP 既定的命名规范、目录结构、代码风格
3. **实用优于完美** — 解决实际问题优先，迭代改进
4. **安全优于便利** — 安全考虑优先，输入验证、权限控制
5. **插件化设计** — 模块低耦合高内聚，按需引入
6. **分布式就绪** — 无状态设计，JWT 认证
7. **扩展性优先** — 接口化设计、注解驱动
8. **前后端分离** — RESTful API + 独立前端项目

完整原则见关联工程文档：`../RuoYi-Vue-Plus/specs/constitution.md`

---

## 二、tpl-manage 特有原则

### 2.1 工程组织原则

1. **不修改 RVP 源码**
   - 任何情况下都不得修改 `../RuoYi-Vue-Plus/` 目录下的源代码
   - 功能扩展通过在 tpl-manage 中新增代码实现
   - 配置调整通过 `application.yml` / `application-dev.yml` 覆盖

2. **聚合构建原则**
   - tpl-manage 根 POM 通过 `<modules>` 聚合 RVP 工程
   - POM 版本号通过 `<properties>` 集中管理
   - `mvn clean install` 在根目录执行，自动级联编译

3. **最小化自定义代码**
   - 优先复用 RVP 已有功能，不重新实现已有的 Controller/Service
   - 仅在 RVP 不满足需求时才编写自定义代码
   - 当前自定义代码：TplManageApplication、AuthController、CaptchaController

### 2.2 数据库原则

1. **PostgreSQL 优先**
   - 全部数据表使用 PostgreSQL 方言
   - DDL 脚本保持 PostgreSQL 兼容性（不使用 MySQL 特有语法）
   - 表名、字段名保持与 RVP 一致（驼峰转下划线）

2. **SQL 脚本管理**
   - 初始化脚本放在 `sql/` 目录，按序号命名（如 `001-init-sys.sql`）
   - 脚本必须幂等（使用 `CREATE TABLE IF NOT EXISTS`）
   - 种子数据使用固定雪花 ID 确保可复现

### 2.3 配置管理原则

1. **配置分层**
   - `application.yml` — 全局通用配置
   - `application-dev.yml` — 开发环境（数据源/Redis/MinIO 连接信息）
   - `application-docker.yml` — Docker 环境

2. **配置覆盖而非修改**
   - RVP 已有的配置通过 tpl-manage 的 `application.yml` 覆盖
   - 不修改 RVP 的配置文件

### 2.4 命名空间原则

1. **包命名**
   - tpl-manage 自定义代码统一使用 `org.fellow99.tpl.manage` 包路径
   - 不得在 `org.dromara` 包下新增代码

2. **应用标识**
   - `spring.application.name` = `tpl-manage`
   - 与「tpl-workspace」产品线命名保持一致

---

## 三、代码规范（继承 RVP + 补充）

### 3.1 命名规范（与 RVP 一致）

| 类型 | 规范 | 示例 |
|------|------|------|
| 类名 | 大驼峰 PascalCase | `AuthController` |
| 方法/变量 | 小驼峰 camelCase | `getUserById` |
| 包名 | 全小写点分隔 | `org.fellow99.tpl.manage.controller` |

### 3.2 注释规范（补充要求）

1. **变更说明** — tpl-manage 自定义代码需要注明为何不直接使用 RVP 已有功能
2. **配置说明** — 覆盖 RVP 配置时需要在配置文件中加注释说明原因

---

## 四、开发流程原则

### 4.1 依赖管理流程

1. 新增 RVP 模块依赖时，在 `tpl-manage-admin/pom.xml` 中声明
2. 如需新增独立依赖，优先检查 RVP 的 `dependencyManagement` 是否已定义版本
3. 版本号统一在根 POM 的 `<properties>` 中管理

### 4.2 数据库变更流程

1. 新增表/字段 → 编写新的 SQL 脚本（按序号递增）
2. SQL 脚本需同时提供 PostgreSQL 版本
3. 脚本提交前需在本地 PostgreSQL 环境验证

### 4.3 代码提交原则

1. **小步提交** — 每次提交解决一个问题
2. **规范提交** — 遵循 Conventional Commits 规范
3. **不提交 RVP 代码** — 确保 `.gitignore` 排除了关联工程

---

## 五、安全原则

### 5.1 认证安全

1. **JWT Secret 管理** — JWT Secret Key 不得硬编码在生产配置中，应通过环境变量或配置中心注入
2. **密码加密** — 用户密码使用 BCrypt 加密存储（继承 RVP 机制）
3. **验证码策略** — 开发阶段可禁用，上线前必须启用

### 5.2 数据库安全

1. **密码加密存储** — 配置中的数据库密码最终应通过环境变量注入
2. **最小权限** — 数据库用户仅授予必要的 CRUD 权限

---

## 六、质量原则

### 6.1 代码质量

1. **Alibaba 规范** — 遵循 Alibaba Java 开发手册（继承 RVP 规范）
2. **统一格式化** — 使用 `.editorconfig` 统一格式化
3. **无警告提交** — 提交前消除编译警告

### 6.2 文档原则

1. **规范文档** — 维护 `specs/` 目录下的完整规范文档
2. **引用关联工程** — RVP 已有的模块文档直接引用，不重复编写
3. **接口文档** — 使用 SpringDoc 自动生成 API 文档（已配置）
4. **变更记录** — 架构变更、配置变更需在规范文档中更新

---

## 七、关联工程引用规范

1. **RuoYi-Vue-Plus 规范** — 位于 `../RuoYi-Vue-Plus/specs/`，tpl-manage 的 spec 文档应明确引用
2. **tpl-manage-ui 规范** — 位于 `../tpl-manage-ui/specs/`，前端页面/路由定义引用该工程
3. **RuoYi-Vue-Plus-UI** — tpl-manage-ui 基于该工程改造，作为参考

---

**遵守本宪法是每个开发者的责任。本宪法应随工程演进持续更新。**

---

## 八、文档分工治理规则（与父工程对齐）

> 本节遵循父工程宪法 `../../specs/constitution.md` 第四章「文档分工治理规则」定义的文档分工契约。本工程 MUST 遵守以下规则：

1. **模块编号对齐**：本工程与父工程共享的产品功能（如用户认证）MUST 使用一致编号；本工程特有模块（feature-120/121）保持自身编号体系。
2. **父工程侧重需求规格**：产品功能需求的权威定义在父工程 `spec.md`，本工程 `spec.md` 不重复编写需求。
3. **父工程 plan 侧重实现逻辑**：父工程 `plan.md` 描述跨工程实现逻辑，本工程 `plan.md` 不重复。
4. **本工程侧重落地实现**：本工程规范文档 MUST 重点描述功能在本工程的落地实现，核心文档是 `plan.md` 与 `test-cases.md`。
5. **本工程 spec 引用父工程**：本工程 `spec.md` SHOULD 引用父工程对应 `spec.md`（如 feature-121 认证引用父工程 002-user-auth），再补充本工程必要的特有规格（如 RBAC 权限）。
