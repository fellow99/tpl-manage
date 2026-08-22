# 整体数据模型 (overall-data-model.md)

**版本：** 1.0.0-SNAPSHOT  
**最后更新：** 2026-08-11  
**项目：** tpl-manage（tpl-workspace管理后台）  
**关联工程：** RuoYi-Vue-Plus 6.0.0

> **说明：** tpl-manage 的核心数据模型完全继承 RuoYi-Vue-Plus，数据表通过 PostgreSQL DDL 脚本自行维护。本文档记录 tpl-manage 实际使用的数据实体及其与 RVP 的差异。

---

## 一、核心数据实体

### 1.1 实体分类

| 分类 | 实体 | 数据库表 | 说明 |
|------|------|----------|------|
| 用户相关 | SysUser | sys_user | 用户基本信息 |
| 角色相关 | SysRole | sys_role | 角色定义 |
| 菜单相关 | SysMenu | sys_menu | 菜单/按钮权限 |
| 部门相关 | SysDept | sys_dept | 组织架构 |
| 岗位相关 | SysPost | sys_post | 用户岗位 |
| 字典相关 | SysDictType, SysDictData | sys_dict_type, sys_dict_data | 系统字典 |
| 参数相关 | SysConfig | sys_config | 系统参数 |
| 日志相关 | SysOperLog, SysLoginInfo | sys_oper_log, sys_login_info | 操作/登录日志 |
| 通知相关 | SysNotice | sys_notice | 通知公告 |
| 文件相关 | SysOss, SysOssConfig | sys_oss, sys_oss_config | 文件存储与配置 |
| 客户端相关 | SysClient | sys_client | 多客户端管理 |
| 社交相关 | SysSocial | sys_social | 第三方登录 |
| 关联表 | SysUserRole, SysUserPost, SysRoleMenu, SysRoleDept | sys_user_role 等 | 多对多关联 |

---

## 二、tpl-manage 与 RVP 的数据模型差异

### 2.1 数据库方言：MySQL → PostgreSQL

tpl-manage 的 DDL 使用 PostgreSQL 方言，主要差异：

| 差异项 | RVP (MySQL) | tpl-manage (PostgreSQL) |
|--------|-------------|----------------------|
| 主键类型 | `bigint` | `int8` |
| 自增策略 | `AUTO_INCREMENT` | 雪花 ID（`ASSIGN_ID`） |
| 字符串类型 | `varchar` | `varchar`（兼容） |
| 布尔类型 | `tinyint(1)` | `bool` |
| 时间戳 | `datetime` | `timestamp` |
| 默认值字符串 | `''` | `''::varchar` |
| 字符类型 | `char(1)` | `char` 或 `bpchar` |
| 注释语法 | `COMMENT '...'` | `comment on table/column ... is '...'` |

### 2.2 表定义方式

- **RVP：** 使用 JPA 实体注解（`@TableName`）自动建表
- **tpl-manage：** 使用手动编写的 SQL DDL 脚本（`sql/001-init-sys.sql`）建表

### 2.3 种子数据差异

tpl-manage 自定义了种子数据，与 RVP 不同：

- **admin 用户** — 密码为自定义 BCrypt hash，部门/角色关联与 RVP 不完全一致
- **菜单树** — 简化版菜单结构（无代码生成、定时任务、工作流、AI 等模块菜单）
- **角色** — superadmin / test1（本部门及以下）/ test2（仅本人）
- **部门** — 根机构

### 2.4 表字段差异

tpl-manage 的部分表相比 RVP 标准定义有扩展字段：

| 表 | 扩展字段 | 类型 | 说明 |
|------|----------|------|------|
| sys_dept | dept_category | varchar(100) | 部门类别编码 |
| sys_post | dept_id | int8 | 岗位所属部门 |
| sys_post | post_category | varchar(100) | 岗位类别编码 |

---

## 三、核心实体关系

### 3.1 用户关系

```
SysUser (sys_user)
    │
    ├── 属于 → SysDept (多对一, dept_id)
    ├── 拥有 → SysPost (多对多, 关联表 sys_user_post)
    ├── 拥有 → SysRole (多对多, 关联表 sys_user_role)
    └── 绑定 → SysSocial (一对多, user_id)
```

### 3.2 角色关系

```
SysRole (sys_role)
    │
    ├── 分配 → SysUser (多对多, 关联表 sys_user_role)
    ├── 拥有 → SysMenu (多对多, 关联表 sys_role_menu)
    └── 可见 → SysDept (多对多, 关联表 sys_role_dept)
```

### 3.3 菜单关系

```
SysMenu (sys_menu) ─── 自引用树形结构 (parent_id)
```

### 3.4 部门关系

```
SysDept (sys_dept) ─── 自引用树形结构 (parent_id, ancestors)
```

### 3.5 字典关系

```
SysDictType (sys_dict_type) ─── SysDictData (sys_dict_data)
    1 : N (通过 dict_type 字段关联)
```

### 3.6 文件关系

```
SysOssConfig (sys_oss_config, 存储配置)
    1 : N
SysOss (sys_oss, 文件记录)
```

---

## 四、通用审计字段（BaseEntity）

所有核心实体继承 `BaseEntity`（来自 RVP）：

| 字段 | 类型 | 说明 |
|------|------|------|
| createDept | Long | 创建部门 |
| createBy | Long | 创建人 |
| createTime | LocalDateTime | 创建时间 |
| updateBy | Long | 更新人 |
| updateTime | LocalDateTime | 更新时间 |

逻辑删除标记 `delFlag`（String, `@TableLogic`）。

---

## 五、种子数据

### 5.1 管理员用户

| 字段 | 值 |
|------|------|
| user_id | 1761100000000000001 |
| user_name | admin |
| nick_name | Admin |
| password | `$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2` |
| dept_id | 1761000000000000100 |
| user_type | sys_user |

### 5.2 默认角色

| role_id | role_name | role_key | data_scope |
|---------|-----------|----------|------------|
| 1761300000000000001 | 超级管理员 | superadmin | 1（全部） |
| 1761300000000000003 | 本部门及以下 | test1 | 4 |
| 1761300000000000004 | 仅本人 | test2 | 5 |

### 5.3 默认部门

| dept_id | parent_id | dept_name |
|---------|-----------|-----------|
| 1761000000000000100 | 0 | 根机构 |

### 5.4 默认岗位

| post_id | post_code | post_name | dept_id |
|---------|-----------|-----------|---------|
| 1761200000000000001 | root | 根岗位 | 1761000000000000100 |
