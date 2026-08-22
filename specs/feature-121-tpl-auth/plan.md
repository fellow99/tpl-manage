# feature-121-tpl-auth 技术方案 (As-Built)

> This document is a retrospective technical plan documenting the actual architecture,
> design decisions, and implementation strategies as built.
> Module: feature-121-tpl-auth
> Corresponding spec: [spec.md](./spec.md)
> Last Updated: 2026-08-11

## 1. 技术上下文

### 1.1 运行时环境
- **框架：** Spring Boot 4.1.0
- **认证：** Sa-Token 1.45.0 + JWT
- **密码算法：** BCrypt
- **包路径：** `org.fellow99.tpl.manage.controller`
- **扫描路径：** `scanBasePackages = {"org.fellow99.tpl", "org.dromara"}`

### 1.2 依赖

| 依赖 | 来源 | 用途 |
|------|------|------|
| cn.dev33.satoken:sa-token | RVP 传递 | Token 签发/校验/销毁 |
| cn.dev33.satoken:sa-token-jwt | RVP 传递 | JWT 格式 Token |
| org.dromara:ruoyi-common-satoken | RVP 依赖 | LoginHelper 工具类 |
| org.dromara:ruoyi-common-core | RVP 传递 | R<V> 统一响应、StringUtils |
| org.dromara:ruoyi-common-web | RVP 依赖 | CaptchaProperties |
| org.dromara:ruoyi-system | RVP 依赖 | ISysUserService, ISysPermissionService |
| org.dromara:ruoyi-api | RVP 依赖 | LoginUser 模型 |

---

## 2. 宪法合规

| 原则 | 状态 | 说明 |
|------|------|------|
| 不修改 RVP 源码 | ✅ | 自定义 Controller 在 org.fellow99.tpl 包下 |
| 最小化自定义代码 | ✅ | 仅 2 个 Controller 文件 |
| 命名空间隔离 | ✅ | 使用 org.fellow99.tpl.manage 包路径 |
| 配置覆盖 | ✅ | 验证码禁用通过配置 + 自定义 Controller 实现 |
| 安全优先 | ✅ | BCrypt 密码验证、JWT Token、@SaIgnore 合理使用 |

---

## 3. 研究结果

### 3.1 关键决策：自定义 AuthController vs 使用 RVP 内置

**选择：** 自定义 AuthController

**理由：**
- RVP 内置的 AuthController 支持多种登录方式（密码/短信/邮箱/社交/小程序），实现复杂，包含验证码校验、注册功能等
- tpl-manage 仅需用户名+密码登录，自定义实现更简洁、更易理解
- 使用独立的包路径 (`org.fellow99.tpl.manage`) 避免与 RVP 的 `org.dromara` 包冲突
- 后续可独立扩展（如对接 tpl-app-api 的用户体系）而不影响 RVP 代码

**风险点：** 如果 Spring 同时扫描到 RVP 的 AuthController 和 tpl-manage 的 AuthController，可能因 `@RequestMapping("/auth")` 路径冲突导致启动失败或行为不确定。当前通过确认 RVP 的 AuthController 存在路径冲突但实际验证发现 RVP 的 AuthController 位于 `org.dromara.web.controller`，路径映射为 `/auth`；tpl-manage 的 AuthController 也映射为 `/auth`，但由于 RVP 的 AuthController 使用了更具体的子路径（如 `/auth/login`、`/auth/register`），而 tpl-manage 仅定义了 `/auth/login` 和 `/auth/logout`，两者在一定程度上共存。

> [NEEDS CLARIFICATION]: 需要确认生产环境中 RVP AuthController 和 tpl-manage AuthController 是否同时生效，以及 `@SaIgnore` 在两者上的行为是否相互影响。

### 3.2 关键决策：客户端标识硬编码

**选择：** client_key 硬编码为 `"e5cd7e4891bf95d1d19206ce24a7b32e"`

**理由：**
- MVP 阶段仅有一个客户端（tpl-manage-ui），无需多客户端管理
- 简化开发和测试流程
- 后续可通过配置或数据库动态管理

### 3.3 关键决策：bcryptCheckpw 替代 passwordEncoder

**选择：** 使用 Sa-Token 内置的 `BCrypt.checkpw()` 进行密码验证

**理由：**
- 与种子数据中的 BCrypt hash 格式一致
- 减少对 Spring Security 的依赖

---

## 4. 数据模型

### 4.1 请求/响应模型

**LoginRequest：**
```java
public static class LoginRequest implements Serializable {
    private String username;    // 用户名
    private String password;    // 密码
    private String code;        // 验证码（预留，当前未使用）
    private String uuid;        // 验证码唯一标识（预留，当前未使用）
}
```

**CaptchaVo：**
```java
public record CaptchaVo(
    Boolean captchaEnabled,  // 验证码是否启用（当前固定 false）
    String uuid,             // 验证码唯一标识（当前固定 null）
    String img               // 验证码图片（当前固定 null）
) {}
```

**LoginUser（来自 RVP ruoyi-api）：**
```java
public class LoginUser {
    private Long userId;
    private String username;
    private String nickname;
    private Long deptId;
    private String userType;       // "sys_user"
    private String clientKey;      // 硬编码客户端标识
    private Set<String> rolePermission;
    private Set<String> menuPermission;
    // ... getters/setters
}
```

### 4.2 Token 存储

- **格式：** JWT（配置：`sa-token.jwt-secret-key: abcdefghijklmnopqrstuvwxyz`）
- **存储位置：** 客户端（前端 localStorage/sessionStorage）
- **传递方式：** HTTP Header `Authorization: Bearer <token>`
- **Token 名称配置：** `sa-token.token-name: Authorization`

### 4.3 会话管理

- `is-concurrent: true` — 允许同一账号多地登录
- `is-share: false` — 不同登录互不影响
- 登录设备标识：`device: "pc"`
- 额外参数：`LoginHelper.CLIENT_KEY`

---

## 5. 接口契约

### 5.1 提供的接口

| 接口 | 类 | 说明 |
|------|------|------|
| POST /auth/login | AuthController | 用户名+密码登录 |
| POST /auth/logout | AuthController | 退出登录 |
| GET /auth/code | CaptchaController | 验证码状态查询 |

### 5.2 消费的接口

| 接口 | 来源 | 说明 |
|------|------|------|
| ISysUserService.selectUserByUserName() | RVP ruoyi-system | 按用户名查询用户 |
| ISysPermissionService.getRolePermission() | RVP ruoyi-system | 获取角色权限集合 |
| ISysPermissionService.getMenuPermission() | RVP ruoyi-system | 获取菜单权限集合 |
| LoginHelper.login() | RVP ruoyi-common-satoken | Token 签发 |
| StpUtil.logout() | Sa-Token | Token 销毁 |
| StpUtil.getTokenValue() | Sa-Token | 获取当前 Token 值 |

---

## 6. 实现策略

### 6.1 架构模式

**MVC Controller 模式**

- AuthController 和 CaptchaController 是标准的 Spring MVC `@RestController`
- 认证逻辑直接在 Controller 层实现（未抽取独立的 Service），因为逻辑简单且在 MVP 范围

### 6.2 关键算法：登录流程

```java
@PostMapping("/login")
public R<Map<String, Object>> login(@RequestBody LoginRequest req) {
    // Step 1: 参数校验
    if (StringUtils.isBlank(req.getUsername()) || StringUtils.isBlank(req.getPassword())) {
        return R.fail("用户名或密码不能为空");
    }

    // Step 2: 查询用户
    SysUserVo user = userService.selectUserByUserName(req.getUsername());
    if (ObjectUtil.isNull(user)) {
        return R.fail("用户不存在");
    }

    // Step 3: 密码验证
    if (!BCrypt.checkpw(req.getPassword(), user.getPassword())) {
        return R.fail("密码错误");
    }

    // Step 4: 状态检查
    if ("1".equals(user.getStatus())) {
        return R.fail("用户已被停用");
    }

    // Step 5: 构建 LoginUser
    Long userId = user.getUserId();
    LoginUser loginUser = new LoginUser();
    loginUser.setUserId(userId);
    loginUser.setUsername(user.getUserName());
    loginUser.setNickname(user.getNickName());
    loginUser.setDeptId(user.getDeptId());
    loginUser.setUserType("sys_user");
    loginUser.setClientKey("e5cd7e4891bf95d1d19206ce24a7b32e");

    // Step 6: 加载权限
    loginUser.setRolePermission(permissionService.getRolePermission(userId));
    loginUser.setMenuPermission(permissionService.getMenuPermission(userId));

    // Step 7: 签发 Token
    SaLoginParameter param = new SaLoginParameter()
        .setDevice("pc")
        .setExtra(LoginHelper.CLIENT_KEY, "e5cd7e4891bf95d1d19206ce24a7b32e");
    LoginHelper.login(loginUser, param);

    // Step 8: 返回 Token
    Map<String, Object> result = new HashMap<>();
    result.put("access_token", StpUtil.getTokenValue());
    return R.ok(result);
}
```

### 6.3 错误处理

- 所有错误使用 `R.fail(msg)` 返回，HTTP 状态码统一为 200
- 错误消息为中文，直接面向前端展示
- 未使用全局异常处理，也不抛出异常

### 6.4 权限加载

- **角色权限：** `permissionService.getRolePermission(userId)` → 返回角色标识集合（如 `["superadmin"]`）
- **菜单权限：** `permissionService.getMenuPermission(userId)` → 返回权限标识集合（如 `["system:user:list"]`）

---

## 7. 测试考量

### 7.1 可测试项

- 正常登录 → 返回 access_token
- 空用户名/密码 → 返回错误消息
- 不存在的用户 → 返回"用户不存在"
- 错误密码 → 返回"密码错误"
- 已停用用户 → 返回"用户已被停用"
- 登出 → Token 失效
- 验证码接口 → 返回 captchaEnabled: false

### 7.2 边界情况

- 并发登录（同一账号多次登录，is-concurrent: true）
- Token 过期后的行为（JWT 过期时间由 Sa-Token 配置控制）
- 用户权限为空时的菜单加载行为

---

## 8. 文件清单

| 文件 | 用途 | 路径 |
|------|------|------|
| AuthController.java | 登录/登出接口实现 | tpl-manage-admin/src/main/java/org/fellow99/tpl/manage/controller/ |
| CaptchaController.java | 验证码状态接口 | tpl-manage-admin/src/main/java/org/fellow99/tpl/manage/controller/ |
| TplManageApplication.java | Spring Boot 启动类（包扫描配置） | tpl-manage-admin/src/main/java/org/fellow99/tpl/manage/ |
