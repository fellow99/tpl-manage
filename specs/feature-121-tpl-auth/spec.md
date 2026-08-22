# 自定义认证 功能规格

> Module: feature-121-tpl-auth
> Status: Implemented
> Last Updated: 2026-08-11
> 来源：tpl-manage 独有
> 父工程规格引用：用户认证的产品需求基准见 [../../specs/002-user-auth/spec.md](../../specs/002-user-auth/spec.md)，本文档补充管理端 RBAC 认证特有规格

## 1. 模块概述

### 1.1 Purpose — 为什么需要这个模块

RuoYi-Vue-Plus 内置的认证模块支持多方式登录（密码/短信/邮箱/社交/小程序），但「tpl-workspace」管理后台在 MVP 阶段只需简单的用户名+密码登录。tpl-manage 自定义了认证接口，提供简化版的登录/登出功能，并禁用了验证码机制以提升开发效率。

### 1.2 Problems Solved — 解决什么问题

- **简化交互** — 仅保留用户名+密码登录，降低实现复杂度和前端对接成本
- **开发效率** — MVP 阶段禁用验证码，加速开发调试
- **包隔离** — 使用 `org.fellow99.tpl.manage` 命名空间，与 RVP 原有认证代码物理隔离
- **自主可控** — 登录逻辑在 tpl-manage 中实现，后续可独立扩展（如接入 tpl-app-api 的用户体系）

### 1.3 Scope — 包含和不包含

**包含：**
- 用户名+密码登录（POST /auth/login）
- 退出登录（POST /auth/logout）
- JWT Token 签发（通过 Sa-Token）
- 角色权限和菜单权限的加载
- 验证码接口（已禁用，返回预设响应）

**不包含：**
- 短信验证码登录
- 邮箱验证码登录
- 社交登录（微信/钉钉等）
- 小程序登录
- 用户注册
- 图形验证码生成与校验
- 多客户端管理（使用硬编码的 client_key）

---

## 2. 用户故事

- 作为管理员，我可以使用用户名和密码登录系统，获取 access_token 以便访问管理功能
- 作为管理员，我可以退出登录，使当前 Token 失效以保护账号安全
- 作为前端开发者，我可以调用 `/auth/code` 接口获取验证码状态，前端根据 `captchaEnabled` 字段决定是否展示验证码

---

## 3. 功能需求

### 3.1 登录功能

- FR-121-001: 系统必须提供 `POST /auth/login` 端点用于用户名+密码登录
- FR-121-002: 系统必须在用户名或密码为空时返回 "用户名或密码不能为空" 的错误消息
- FR-121-003: 系统必须在用户不存在时返回 "用户不存在" 的错误消息
- FR-121-004: 系统必须在密码错误时返回 "密码错误" 的错误消息
- FR-121-005: 系统必须在用户状态为停用（status='1'）时返回 "用户已被停用" 的错误消息
- FR-121-006: 系统必须在登录成功后返回包含 `access_token` 的 JSON 响应
- FR-121-007: 系统必须在登录成功后加载用户的角色权限（rolePermission）和菜单权限（menuPermission）
- FR-121-008: 系统必须使用 Sa-Token 的 `LoginHelper.login()` 方法签发 Token 并维护会话
- FR-121-009: 登录接口必须标注 `@SaIgnore`，无需认证即可访问
- FR-121-010: 系统必须使用 BCrypt 算法验证用户密码

### 3.2 登出功能

- FR-121-011: 系统必须提供 `POST /auth/logout` 端点用于退出登录
- FR-121-012: 登出操作必须使当前 Token 失效（调用 `StpUtil.logout()`）
- FR-121-013: 登出接口必须需要认证（携带有效的 Authorization Token）

### 3.3 验证码接口

- FR-121-014: 系统必须提供 `GET /auth/code` 端点用于获取验证码状态
- FR-121-015: 验证码接口必须标注 `@SaIgnore`，无需认证即可访问
- FR-121-016: 验证码接口必须在 MVP 阶段返回 `captchaEnabled: false`

---

## 4. 关键实体

| 实体 | 描述 | 关键属性 |
|------|------|----------|
| LoginRequest | 登录请求体 | username, password, code(预留), uuid(预留) |
| LoginUser | Sa-Token 登录用户模型（来自 RVP） | userId, username, nickname, deptId, userType, clientKey, rolePermission, menuPermission |
| CaptchaVo | 验证码响应体 | captchaEnabled(boolean), uuid(String), img(String) |

---

## 5. 验收场景

### Scenario: 正确凭据登录成功

- Given 数据库中存在 admin 用户，密码为 admin123
- When 发送 `POST /auth/login {"username":"admin","password":"admin123"}`
- Then 返回 HTTP 200，响应体 code=200，data.access_token 为非空 JWT Token

### Scenario: 错误密码登录失败

- Given admin 用户存在
- When 发送 `POST /auth/login {"username":"admin","password":"wrong"}`
- Then 返回 HTTP 200，响应体 msg="密码错误"

### Scenario: 不存在用户登录失败

- Given 用户 "ghost" 不存在
- When 发送 `POST /auth/login {"username":"ghost","password":"xxx"}`
- Then 返回 HTTP 200，响应体 msg="用户不存在"

### Scenario: 空用户名登录失败

- Given 无前置条件
- When 发送 `POST /auth/login {"username":"","password":"xxx"}`
- Then 返回 HTTP 200，响应体 msg="用户名或密码不能为空"

### Scenario: 登出使 Token 失效

- Given 已成功登录并持有 access_token
- When 携带 Token 发送 `POST /auth/logout`
- Then 返回 HTTP 200
- And 再次使用原 Token 访问任何需要认证的接口时返回 401

### Scenario: 验证码状态查询

- Given 服务已启动
- When 发送 `GET /auth/code`
- Then 返回 `{"data":{"captchaEnabled":false,"uuid":null,"img":null}}`

---

## 6. 非功能性需求

- **安全性：** 密码必须通过 BCrypt 加密比对，不记录明文密码
- **无状态：** Token 使用 JWT 格式，服务端不存储会话状态
- **扩展性：** 预留 code/uuid 字段，后续可启用验证码校验

---

## 7. 假设与约束

- 假设用户数据已通过 `sql/001-init-sys.sql` 初始化到 PostgreSQL
- 假设 client_key 固定为 `"e5cd7e4891bf95d1d19206ce24a7b32e"`（硬编码）
- 假设前端通过 `Authorization: Bearer <token>` 请求头携带 Token
- 假设验证码在 MVP 阶段永久禁用，上线前重新启用

---

## 8. 依赖

- **上游依赖：**
  - `ISysUserService`（来自 RVP ruoyi-system）— 查询用户信息
  - `ISysPermissionService`（来自 RVP ruoyi-system）— 加载角色和菜单权限
  - `LoginHelper`（来自 RVP ruoyi-common-satoken）— Token 签发和会话管理
  - `BCrypt`（来自 Sa-Token）— 密码加密比对
  - `CaptchaProperties`（来自 RVP ruoyi-common-web）— 验证码配置读取
- **下游依赖：**
  - tpl-manage-ui 前端 — 调用 `/auth/login`、`/auth/logout`、`/auth/code` 接口

---

## 微信登录集成变更说明

> 需求基准见父工程 [../../specs/002-user-auth/spec.md](../../specs/002-user-auth/spec.md) §11（FR-WX-6）。

- **后端接口保留（零改动）**：RVP 继承的社交登录接口（`GET /auth/binding/{source}`、`POST /auth/social/callback`、`DELETE /auth/unlock/{socialId}`、`GET /system/social/list`）**全部保留，不删除任何接口**。
- **能力禁用**：不配置 `justauth.type.*`（client-id/client-secret 留空），接口保留但调用时返回「该平台账号暂不支持」。
- **实际运行时范围**：tpl-manage 运行时仅 `ruoyi-system` 的 `/system/social/list` 生效（社交登录链路在 `ruoyi-admin`，不在本工程 classpath），故管理侧无第三方登录能力，符合「管理侧无需第三方登录」。
