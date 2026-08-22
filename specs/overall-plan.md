# 整体技术方案 (overall-plan.md)

**版本：** 1.0.0-SNAPSHOT  
**最后更新：** 2026-08-11  
**项目：** tpl-manage（tpl-workspace管理后台）  
**关联工程：** RuoYi-Vue-Plus 6.0.0

---

## 一、技术上下文

### 1.1 运行时环境
- **JDK：** 21（LTS）
- **构建工具：** Maven 3.9+
- **Web 容器：** Jetty 12（基于 Netty）
- **数据库：** PostgreSQL 15+（主存储）
- **缓存：** Redis 7.0+
- **文件存储：** MinIO（Bucket: tpl-manage）

### 1.2 关键依赖

| 分类 | 依赖 | 版本 | 作用 |
|------|------|------|------|
| 框架 | Spring Boot | 4.1.0 | 应用框架 |
| 认证 | Sa-Token + JWT | 1.45.0 | 权限认证 |
| ORM | MyBatis-Plus | 3.5.17 | 数据访问 |
| 缓存 | Redisson | 4.6.1 | Redis 客户端 |
| 数据库 | PostgreSQL JDBC | — | 数据库驱动 |
| 文件 | MinIO + S3 SDK | 2.48.1 | 对象存储 |
| 文档 | SpringDoc | 3.0.3 | API 文档 |
| 监控 | Spring Boot Admin | 4.1.2 | 服务监控（可选） |

---

## 二、宪法合规检查

| 原则 | 状态 | 说明 |
|------|------|------|
| 不修改 RVP 源码 | ✅ | 所有代码在 org.fellow99.tpl 包下 |
| 聚合构建原则 | ✅ | pom.xml 通过 <modules> 聚合 RVP |
| 最小化自定义代码 | ✅ | 仅 3 个 Java 源文件 |
| PostgreSQL 优先 | ✅ | 全部 DDL 使用 PostgreSQL 方言 |
| 配置覆盖而非修改 | ✅ | 通过 application.yml 覆盖 RVP 默认配置 |
| 命名空间隔离 | ✅ | 自定义代码统一 org.fellow99.tpl.manage |
| 插件化设计 | ✅ | 继承 RVP 25 个 common 子模块架构 |
| 分布式就绪 | ✅ | JWT 无状态 + Redisson + Lock4j |
| 前后端分离 | ✅ | 独立前端项目 tpl-manage-ui |
| 安全优先 | ✅ | BCrypt 密码加密、XSS 防护、操作日志 |

---

## 三、实现策略概述

### 3.1 聚合工程构建

```
tpl-manage/pom.xml (聚合 POM)
├── <properties>
│   └── <ruoyi-vue-plus.version>6.0.0</ruoyi-vue-plus.version>
├── <dependencyManagement>  ← 导入 RVP BOM
│   └── org.dromara:ruoyi-vue-plus:6.0.0
├── <modules>
│   ├── ../RuoYi-Vue-Plus/     ← Maven 级联编译
│   └── tpl-manage-admin/        ← 自定义启动模块
└── <repositories>  ← 华为云 Maven 镜像
```

### 3.2 启动模块依赖

```
tpl-manage-admin/pom.xml
├── <parent> org.dromara:ruoyi-vue-plus:6.0.0
├── <dependencies>
│   ├── org.postgresql:postgresql（运行时）
│   ├── ruoyi-common-doc（接口文档）
│   ├── ruoyi-common-mail（邮件，可选）
│   ├── ruoyi-common-log（操作日志）
│   ├── ruoyi-common-security（安全配置）
│   ├── ruoyi-common-web（Web 配置）
│   ├── ruoyi-api（RPC 接口）
│   ├── ruoyi-system（系统管理模块）
│   └── spring-boot-admin-starter-client（监控，可选）
└── <build>
    └── spring-boot-maven-plugin（可执行 JAR）
```

### 3.3 认证流程（自定义）

```
用户 → POST /auth/login {username, password}
  → AuthController.login()
  → ISysUserService.selectUserByUserName()     // RVP 提供的服务
  → BCrypt.checkpw(password, user.password)    // 密码验证
  → ISysPermissionService.getRolePermission()  // 加载角色权限
  → ISysPermissionService.getMenuPermission()  // 加载菜单权限
  → LoginHelper.login(loginUser, param)        // Sa-Token 签发 JWT
  → {access_token: "xxx"}                      // 返回 Token
```

相比 RVP 的多方式登录（密码/短信/邮箱/社交/小程序），tpl-manage 提供简化的用户名+密码登录。验证码功能已禁用（返回 `captchaEnabled: false`）。

### 3.4 数据库初始化

```
tpl-manage/sql/001-init-sys.sql
  ├── sys_social 表（第三方登录绑定）
  ├── sys_dept 表 + 种子数据（根机构 + admin 创建部门）
  ├── sys_user 表 + 种子数据（admin 用户，BCrypt 密码）
  ├── sys_post 表 + 种子数据（根岗位）
  ├── sys_role 表 + 种子数据（superadmin/test1/test2）
  ├── sys_menu 表 + 种子数据（完整菜单树 + 按钮权限）
  ├── sys_user_role（admin → superadmin）
  ├── sys_role_menu（test1 角色授权）
  ├── sys_role_dept（角色部门关联）
  ├── sys_user_post（admin → 根岗位）
  ├── sys_oper_log（操作日志表）
  ├── sys_dict_type + sys_dict_data（字典数据）
  └── ...（其余系统表）
```

---

## 四、跨领域关注点

### 4.1 错误处理
- 全局异常拦截：继承 RVP 的 `GlobalExceptionHandler`
- 业务异常：RVP 的 `ServiceException` 体系
- 登录失败：`R.fail("用户名或密码不能为空")` / `"用户不存在"` / `"密码错误"` / `"用户已被停用"`

### 4.2 日志记录
- 操作日志：`@Log` 注解（RVP ruoyi-common-log）
- 登录日志：由 RVP 框架自动记录
- SQL 日志：开发环境 console 输出（`mybatis-plus.sql-log.enabled: true`）
- 应用日志：Logback（`logback.xml`），tpl-manage 包日志级别 DEBUG

### 4.3 数据权限（继承 RVP）
- 注解驱动：`@DataPermission` 标记需要数据权限的 Mapper
- 插件注入：`PlusDataPermissionInterceptor` 自动拼接 SQL 条件
- 范围类型：全部/自定义/本部门/本部门及以下/仅本人

### 4.4 缓存策略（继承 RVP）
- Spring Cache 注解：`@Cacheable`, `@CachePut`, `@CacheEvict`
- 字典缓存：`CacheNames.SYS_DICT`
- 参数缓存：`CacheNames.SYS_CONFIG`
- 缓存刷新：`/system/dict/type/refreshCache`、`/system/config/refreshCache`

---

## 五、构建与部署

### 5.1 构建

```bash
# 在 tpl-manage 根目录执行（级联编译 RVP + tpl-manage）
mvn clean install -P dev

# 仅打包 tpl-manage-admin（前提：RVP 已安装到本地仓库）
cd tpl-manage-admin
mvn package -P dev
# 输出：target/tpl-manage-admin.jar
```

### 5.2 启动

```bash
# 开发环境
java -jar tpl-manage-admin/target/tpl-manage-admin.jar --spring.profiles.active=dev

# Docker 环境
java -jar tpl-manage-admin/target/tpl-manage-admin.jar --spring.profiles.active=docker
```

### 5.3 Maven Profile

| Profile | 说明 | 数据库 | 日志级别 |
|---------|------|--------|----------|
| dev | 本地开发（默认激活） | PostgreSQL localhost:5432/tpl_manage | tpl 包 DEBUG |
| docker | Docker 环境 | 按配置 | 按配置 |

---

## 六、测试策略

### 6.1 测试范围
- tpl-manage 自定义代码（AuthController）需单元测试覆盖
- RVP 继承模块的测试由 RVP 工程负责
- 集成测试：PostgreSQL 数据库连接与 SQL 脚本验证

### 6.2 测试类型
- **单元测试：** `spring-boot-starter-test`（已引入）
- **API 测试：** SpringDoc + Swagger UI 手动验证
- **数据库测试：** SQL 脚本在 PostgreSQL 环境执行验证
