# 整体接口模型 (overall-api.md)

**版本：** 1.0.0-SNAPSHOT  
**最后更新：** 2026-08-11  
**项目：** tpl-manage（tpl-workspace管理后台）  
**关联工程：** RuoYi-Vue-Plus 6.0.0  
**前端消费方：** tpl-manage-ui（`../tpl-manage-ui/`，API 对接见 `../tpl-manage-ui/specs/overall-api.md`）

> **说明：** tpl-manage 的绝大部分 REST API 继承自 RuoYi-Vue-Plus（ruoyi-system 模块），仅认证相关接口为 tpl-manage 自定义。完整 API 清单见关联工程 `../RuoYi-Vue-Plus/specs/overall-api.md`。tpl-manage-ui 前端通过 Vite proxy 将 `/dev-api/*` 代理到 `localhost:8081`，消费本文档列出的全部接口。

---

## 一、API 规范

### 1.1 认证机制
- **认证方式：** Sa-Token + JWT，请求头 `Authorization: Bearer <token>`
- **公开接口：** 标注 `@SaIgnore` 的接口无需认证（登录、验证码）
- **Token 名称：** `Authorization`（配置项 `sa-token.token-name`）
- **JWT Secret：** 配置项 `sa-token.jwt-secret-key`

### 1.2 统一响应格式
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

### 1.3 基础路径
- 服务端口：**8081**
- Context Path：`/`

---

## 二、tpl-manage 自定义 API

tpl-manage 仅自定义了认证相关接口，位于 `org.fellow99.tpl.manage.controller` 包：

### 2.1 AuthController — 认证接口

| 路径 | Method | 用途 | 认证 | 来源 |
|------|--------|------|------|------|
| `/auth/login` | POST | 用户名+密码登录，返回 access_token | 无需 | tpl-manage 自定义 |
| `/auth/logout` | POST | 退出登录，销毁 Token | 需要 | tpl-manage 自定义 |

**POST /auth/login 请求体：**
```json
{
  "username": "admin",
  "password": "***",
  "code": "",
  "uuid": ""
}
```

**POST /auth/login 响应体：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "access_token": "eyJhbG..."
  }
}
```

**登录错误响应：**
- 用户名或密码为空：`R.fail("用户名或密码不能为空")`
- 用户不存在：`R.fail("用户不存在")`
- 密码错误：`R.fail("密码错误")`
- 用户已停用：`R.fail("用户已被停用")`

### 2.2 CaptchaController — 验证码接口

| 路径 | Method | 用途 | 认证 | 来源 |
|------|--------|------|------|------|
| `/auth/code` | GET | 获取图片验证码（已禁用） | 无需 | tpl-manage 自定义 |

**GET /auth/code 响应体（验证码禁用状态）：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "captchaEnabled": false,
    "uuid": null,
    "img": null
  }
}
```

---

## 三、继承 RuoYi-Vue-Plus 的 API

以下 API 继承自 RVP 的 `ruoyi-system` 模块。详细的 API 清单、请求参数和响应格式见关联工程文档：`../RuoYi-Vue-Plus/specs/overall-api.md`。

### 3.1 用户管理 (002-user)

| 路径 | Method | 用途 |
|------|--------|------|
| `/system/user/list` | GET | 分页查询用户列表 |
| `/system/user/export` | POST | 导出用户列表 |
| `/system/user/{userId}` | GET | 获取用户详情 |
| `/system/user` | POST | 新增用户 |
| `/system/user` | PUT | 修改用户 |
| `/system/user/{userIds}` | DELETE | 删除用户 |
| `/system/user/resetPwd` | PUT | 重置用户密码 |
| `/system/user/changeStatus` | PUT | 修改用户状态 |
| `/system/user/authRole/{userId}` | GET | 获取用户授权角色 |
| `/system/user/authRole` | PUT | 用户授权角色 |
| `/system/user/deptTree` | GET | 获取部门树 |

### 3.2 角色管理 (003-role)

| 路径 | Method | 用途 |
|------|--------|------|
| `/system/role/list` | GET | 分页查询角色列表 |
| `/system/role/{roleId}` | GET | 获取角色详情 |
| `/system/role` | POST | 新增角色 |
| `/system/role` | PUT | 修改角色基础信息 |
| `/system/role/permission` | PUT | 修改角色权限 |
| `/system/role/changeStatus` | PUT | 修改角色状态 |
| `/system/role/{roleIds}` | DELETE | 删除角色 |
| `/system/role/optionselect` | GET | 角色选择列表 |
| `/system/role/deptTree/{roleId}` | GET | 角色部门树 |

### 3.3 菜单管理 (006-menu)

| 路径 | Method | 用途 |
|------|--------|------|
| `/system/menu/getRouters` | GET | 获取前端路由 |
| `/system/menu/list` | GET | 查询菜单列表 |
| `/system/menu/treeselect` | GET | 菜单下拉树 |
| `/system/menu/roleMenuTreeselect/{roleId}` | GET | 角色菜单树 |
| `/system/menu` | POST | 新增菜单 |
| `/system/menu` | PUT | 修改菜单 |
| `/system/menu/{menuId}` | DELETE | 删除菜单 |

### 3.4 部门管理 (004-dept)

| 路径 | Method | 用途 |
|------|--------|------|
| `/system/dept/list` | GET | 查询部门列表 |
| `/system/dept/{deptId}` | GET | 获取部门详情 |
| `/system/dept` | POST | 新增部门 |
| `/system/dept` | PUT | 修改部门 |
| `/system/dept/{deptId}` | DELETE | 删除部门 |

### 3.5 岗位管理 (005-post)

| 路径 | Method | 用途 |
|------|--------|------|
| `/system/post/list` | GET | 分页查询岗位列表 |
| `/system/post/{postId}` | GET | 获取岗位详情 |
| `/system/post` | POST | 新增岗位 |
| `/system/post` | PUT | 修改岗位 |
| `/system/post/{postIds}` | DELETE | 删除岗位 |
| `/system/post/optionselect` | GET | 岗位选择列表 |

### 3.6 字典管理 (007-dict)

| 路径 | Method | 用途 |
|------|--------|------|
| `/system/dict/type/list` | GET | 字典类型列表 |
| `/system/dict/type/{dictId}` | GET | 字典类型详情 |
| `/system/dict/type` | POST | 新增字典类型 |
| `/system/dict/type` | PUT | 修改字典类型 |
| `/system/dict/type/{dictIds}` | DELETE | 删除字典类型 |
| `/system/dict/type/refreshCache` | DELETE | 刷新字典缓存 |
| `/system/dict/data/list` | GET | 字典数据列表 |
| `/system/dict/data/type/{dictType}` | GET | 按类型查字典数据 |
| `/system/dict/data` | POST | 新增字典数据 |
| `/system/dict/data` | PUT | 修改字典数据 |
| `/system/dict/data/{dictCodes}` | DELETE | 删除字典数据 |

### 3.7 参数配置 (008-config)

| 路径 | Method | 用途 |
|------|--------|------|
| `/system/config/list` | GET | 参数列表 |
| `/system/config/configKey/{configKey}` | GET | 按键名查参数值 |
| `/system/config` | POST | 新增参数 |
| `/system/config` | PUT | 修改参数 |
| `/system/config/{configIds}` | DELETE | 删除参数 |
| `/system/config/refreshCache` | DELETE | 刷新参数缓存 |

### 3.8 通知公告 (009-notice)

| 路径 | Method | 用途 |
|------|--------|------|
| `/system/notice/list` | GET | 通知列表 |
| `/system/notice/{noticeId}` | GET | 通知详情 |
| `/system/notice` | POST | 新增通知 |
| `/system/notice` | PUT | 修改通知 |
| `/system/notice/{noticeIds}` | DELETE | 删除通知 |

### 3.9 操作日志 (010-oper-log)

| 路径 | Method | 用途 |
|------|--------|------|
| `/monitor/operlog/list` | GET | 操作日志列表 |
| `/monitor/operlog/export` | POST | 导出操作日志 |
| `/monitor/operlog/{operIds}` | DELETE | 删除操作日志 |
| `/monitor/operlog/clean` | DELETE | 清空操作日志 |

### 3.10 登录日志 (011-login-log)

| 路径 | Method | 用途 |
|------|--------|------|
| `/monitor/loginInfo/list` | GET | 登录日志列表 |
| `/monitor/loginInfo/export` | POST | 导出登录日志 |
| `/monitor/loginInfo/{infoIds}` | DELETE | 删除登录日志 |
| `/monitor/loginInfo/clean` | DELETE | 清空登录日志 |
| `/monitor/loginInfo/unlock/{userName}` | GET | 解锁用户 |

### 3.11 在线用户 (012-online)

| 路径 | Method | 用途 |
|------|--------|------|
| `/monitor/online/list` | GET | 在线用户列表 |
| `/monitor/online/{tokenId}` | DELETE | 强制用户下线 |

### 3.12 缓存监控 (014-cache)

| 路径 | Method | 用途 |
|------|--------|------|
| `/monitor/cache` | GET | Redis 缓存监控信息 |

### 3.13 文件管理 (feature-103)

| 路径 | Method | 用途 |
|------|--------|------|
| `/system/oss/list` | GET | 文件列表 |
| `/system/oss/upload` | POST | 文件上传 |
| `/system/oss/download/{ossId}` | GET | 文件下载 |
| `/system/oss/{ossIds}` | DELETE | 删除文件 |

### 3.14 客户端管理

| 路径 | Method | 用途 |
|------|--------|------|
| `/system/client/list` | GET | 客户端列表 |
| `/system/client` | POST | 新增客户端 |
| `/system/client` | PUT | 修改客户端 |
| `/system/client/{clientIds}` | DELETE | 删除客户端 |

---

## 四、未启用的 API 端点

以下 RVP 端点配置存在但未启用或 disabled：

| 路径 | 原因 |
|------|------|
| `/tool/gen/**` | 未引入 ruoyi-gen 模块 |
| `/workflow/**` | 未引入 ruoyi-workflow 模块 |
| `/demo/**` | 未引入 ruoyi-demo 模块 |
| `/snail-ai/**` | snail-ai enabled: false |
| `/resource/message` | message 功能已配置，SSE 推送通道 |

---

## 五、全局错误码

| HTTP Code | 说明 |
|-----------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 / Token 失效 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |
