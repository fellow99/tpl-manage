# 测试用例索引 (overall-test-cases.md)

**版本：** 1.0.0-SNAPSHOT  
**最后更新：** 2026-08-11  
**项目：** tpl-manage（tpl-workspace管理后台）

---

## 一、测试范围说明

tpl-manage 的测试分为两部分：

1. **tpl-manage 独有功能** — 需编写测试用例（自定义认证、聚合构建验证）
2. **继承 RVP 的功能** — 功能测试用例参见关联工程 `../RuoYi-Vue-Plus/specs/overall-test-cases.md`

---

## 二、tpl-manage 独有功能测试用例

### 2.1 认证模块测试（feature-121-tpl-auth）

#### 测试环境
- 数据库：PostgreSQL（需预先执行 `sql/001-init-sys.sql`）
- 默认管理员：admin / admin123（或种子数据中配置的密码）
- 服务端口：8081

#### 登录功能测试

| 用例编号 | 测试场景 | 前置条件 | 操作步骤 | 预期结果 |
|----------|----------|----------|----------|----------|
| TC-AUTH-001 | 正常登录 | admin 用户已存在于数据库 | `POST /auth/login {"username":"admin","password":"admin123"}` | HTTP 200，响应包含 access_token |
| TC-AUTH-002 | 空用户名登录 | 无 | `POST /auth/login {"username":"","password":"xxx"}` | HTTP 200，code!=200，msg："用户名或密码不能为空" |
| TC-AUTH-003 | 空密码登录 | 无 | `POST /auth/login {"username":"admin","password":""}` | HTTP 200，code!=200，msg："用户名或密码不能为空" |
| TC-AUTH-004 | 用户不存在 | 无 | `POST /auth/login {"username":"nonexistent","password":"xxx"}` | HTTP 200，code!=200，msg："用户不存在" |
| TC-AUTH-005 | 密码错误 | admin 用户已存在 | `POST /auth/login {"username":"admin","password":"wrongpassword"}` | HTTP 200，code!=200，msg："密码错误" |
| TC-AUTH-006 | 用户已停用 | admin 用户 status 设为 1 | `POST /auth/login {"username":"admin","password":"admin123"}` | HTTP 200，code!=200，msg："用户已被停用" |
| TC-AUTH-007 | 登录后获取 Token | TC-AUTH-001 通过 | 检查响应中 access_token 字段 | access_token 为非空字符串 |
| TC-AUTH-008 | Token 可访问受保护接口 | 持有有效 Token | `GET /system/user/list` 携带 Authorization header | HTTP 200，返回用户列表 |

#### 登出功能测试

| 用例编号 | 测试场景 | 前置条件 | 操作步骤 | 预期结果 |
|----------|----------|----------|----------|----------|
| TC-AUTH-010 | 正常登出 | 已登录持有 Token | `POST /auth/logout` 携带 Authorization header | HTTP 200，操作成功 |
| TC-AUTH-011 | 登出后 Token 失效 | TC-AUTH-010 通过 | 使用原 Token 访问 `/system/user/list` | 返回 401 未登录 |
| TC-AUTH-012 | 未登录直接登出 | 无有效 Token | `POST /auth/logout` 不携带 Authorization | 返回 401 未登录 |

#### 验证码禁用验证

| 用例编号 | 测试场景 | 前置条件 | 操作步骤 | 预期结果 |
|----------|----------|----------|----------|----------|
| TC-AUTH-020 | 获取验证码状态 | 服务已启动 | `GET /auth/code` | HTTP 200，captchaEnabled: false |

---

### 2.2 聚合构建验证测试（feature-120-project-build）

#### 构建测试

| 用例编号 | 测试场景 | 操作步骤 | 预期结果 |
|----------|----------|----------|----------|
| TC-BUILD-001 | 级联编译 | 在 tpl-manage 根目录执行 `mvn clean install -P dev` | RVP 模块先编译成功，tpl-manage-admin 后编译成功 |
| TC-BUILD-002 | 打包可执行 JAR | `cd tpl-manage-admin && mvn package -P dev` | 生成 `target/tpl-manage-admin.jar` |
| TC-BUILD-003 | JAR 可启动 | `java -jar tpl-manage-admin.jar --spring.profiles.active=dev` | 应用启动成功，日志包含 "TplManageApplication" |

#### 数据库初始化测试

| 用例编号 | 测试场景 | 操作步骤 | 预期结果 |
|----------|----------|----------|----------|
| TC-BUILD-010 | SQL 脚本可执行 | 在 PostgreSQL 中执行 `sql/001-init-sys.sql` | 所有表创建成功，种子数据插入成功 |
| TC-BUILD-011 | admin 用户可登录 | 执行 SQL 后启动服务，使用 admin 登录 | 登录成功，返回 Token |
| TC-BUILD-012 | 菜单数据正确 | 登录后请求 `/system/menu/getRouters` | 返回菜单树，包含系统管理和系统监控 |

---

## 三、继承 RVP 功能回归测试要点

以下 RVP 继承功能应在 tpl-manage 的 PostgreSQL 环境下执行回归验证：

| 模块 | 关键验证点 | 参考测试用例 |
|------|-----------|-------------|
| 用户管理 | CRUD 操作、角色分配、密码重置 | RVP TC-USER-* |
| 角色管理 | CRUD 操作、菜单权限分配、数据范围 | RVP TC-ROLE-* |
| 菜单管理 | 树形结构、按钮权限、路由配置 | RVP TC-MENU-* |
| 部门管理 | 树形结构、父子关系 | RVP TC-DEPT-* |
| 字典管理 | 类型+数据 CRUD、缓存刷新 | RVP TC-DICT-* |
| 操作日志 | 日志记录、查询、导出 | RVP TC-OPERLOG-* |
| 文件管理 | MinIO 上传/下载/删除 | RVP TC-OSS-* |

---

## 四、环境准备

### 4.1 测试环境要求

| 组件 | 要求 |
|------|------|
| PostgreSQL | 15+，数据库 `tpl_manage`，用户 `postgres` |
| Redis | 7.0+，localhost:6379 |
| MinIO | Latest，localhost:9000，Bucket: `tpl-manage` |
| JDK | 21 |

### 4.2 测试数据准备

```sql
-- 执行初始化脚本
\i sql/001-init-sys.sql
```

### 4.3 服务启动

```bash
cd tpl-manage-admin
mvn spring-boot:run -P dev
# 或
java -jar target/tpl-manage-admin.jar --spring.profiles.active=dev
```

服务启动后访问：
- API 服务：http://localhost:8081
- API 文档（SpringDoc）：http://localhost:8081/swagger-ui.html
