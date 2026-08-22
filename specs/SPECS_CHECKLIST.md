# tpl-manage 规范文档检查清单

**工程：** tpl-manage  
**版本：** 1.0.0-SNAPSHOT  
**文档目录：** specs  
**开始时间：** 2026-08-11  
**最后更新：** 2026-08-11  
**关联工程：** RuoYi-Vue-Plus 6.0.0（specs 目录位于 `..\RuoYi-Vue-Plus\specs\`）  
**前端工程：** tpl-manage-ui（..\tpl-manage-ui\）

> **说明：** tpl-manage 是基于 RuoYi-Vue-Plus 6.0.0 的聚合构建项目，通过 Maven 模块聚合方式集成 RVP 全部模块。本工程的 specs 文档仅覆盖 tpl-manage 独有的模块和差异部分，RVP 已有的模块文档参见关联工程的 specs 目录。tpl-manage 独有模块使用 `feature-120` 起编号。

---

## 整体文档

| 文档 | 状态 | 完成时间 | 备注 |
|------|------|----------|------|
| README.md | ✅ | 2026-08-11 | 项目介绍和文档索引 |
| SPECS_CHECKLIST.md | ✅ | 2026-08-11 | 本检查清单 |
| STRUCTURE.md | ✅ | 2026-08-11 | 目录结构和模块清单 |
| TECH.md | ✅ | 2026-08-11 | 技术选型 |
| ARCHITECTURE.md | ✅ | 2026-08-11 | 整体架构 |
| constitution.md | ✅ | 2026-08-11 | 宪法原则 |
| overall-spec.md | ✅ | 2026-08-11 | 整体规格 |
| overall-plan.md | ✅ | 2026-08-11 | 整体方案 |
| overall-data-model.md | ✅ | 2026-08-11 | 数据模型 |
| overall-api.md | ✅ | 2026-08-11 | 接口模型 |
| overall-test-cases.md | ✅ | 2026-08-11 | 测试用例索引 |

---

## tpl-manage 独有模块（feature-120+）

| 模块 | 编号 | spec | plan | 状态 |
|------|------|------|------|------|
| 工程构建与部署 | feature-120 | ✅ | ✅ | 🟢 完成 |
| 自定义认证 | feature-121 | ✅ | ✅ | 🟢 完成 |

---

## RuoYi-Vue-Plus 继承模块（引用关联工程 specs）

以下模块由 tpl-manage 通过 Maven 聚合方式从 RuoYi-Vue-Plus 6.0.0 继承，模块文档位于 `..\RuoYi-Vue-Plus\specs\`：

### 系统管理模块（002 ~ 009）

| 模块 | 编号 | 状态 | 说明 |
|------|------|------|------|
| 用户管理 | 002 | ✅ 继承 | 引用 RVP specs/002-user/ |
| 角色管理 | 003 | ✅ 继承 | 引用 RVP specs/003-role/ |
| 部门管理 | 004 | ✅ 继承 | 引用 RVP specs/004-dept/ |
| 岗位管理 | 005 | ✅ 继承 | 引用 RVP specs/005-post/ |
| 菜单管理 | 006 | ✅ 继承 | 引用 RVP specs/006-menu/ |
| 字典管理 | 007 | ✅ 继承 | 引用 RVP specs/007-dict/ |
| 参数配置 | 008 | ✅ 继承 | 引用 RVP specs/008-config/ |
| 通知公告 | 009 | ✅ 继承 | 引用 RVP specs/009-notice/ |

### 系统监控模块（010 ~ 016）

| 模块 | 编号 | 状态 | 说明 |
|------|------|------|------|
| 操作日志 | 010 | ✅ 继承 | 引用 RVP specs/010-oper-log/ |
| 登录日志 | 011 | ✅ 继承 | 引用 RVP specs/011-login-log/ |
| 在线用户 | 012 | ✅ 继承 | 引用 RVP specs/012-online/ |
| 定时任务 | 013 | ⚠️ 未启用 | SnailJob 在 dev 环境 disabled |
| 缓存监控 | 014 | ✅ 继承 | 引用 RVP specs/014-cache/ |
| 服务监控 | 015 | ✅ 继承 | 引用 RVP specs/015-server/ |
| 连接池监控 | 016 | ✅ 继承 | 引用 RVP specs/016-pool/ |

### 系统工具模块（017 ~ 019）

| 模块 | 编号 | 状态 | 说明 |
|------|------|------|------|
| 代码生成 | 017 | ❌ 未包含 | 未引入 ruoyi-gen 依赖 |
| API 文档 | 018 | ✅ 继承 | 引用 RVP specs/018-swagger/ |
| 构建工具 | 019 | ✅ 继承 | 引用 RVP specs/019-build/ |

### Plus 增强 & v6 新增模块（feature-101 ~ feature-119）

tpl-manage 根据业务需求选择性引入 RVP 增强模块，具体以 `tpl-manage-admin/pom.xml` 中声明的依赖为准。当前依赖中包含的增强模块能力：

| 增强能力 | 对应模块 | 状态 | 说明 |
|----------|----------|------|------|
| 对象存储 | feature-103 | ✅ 继承 | OSS 菜单已配置，依赖 ruoyi-common-oss（间接） |
| 邮件服务 | feature-110 | ✅ 继承 | 直接依赖 ruoyi-common-mail |
| 日志记录 | — | ✅ 继承 | 直接依赖 ruoyi-common-log |
| API 文档 | 018 | ✅ 继承 | 直接依赖 ruoyi-common-doc |
| 安全/认证 | — | ✅ 继承 | 直接依赖 ruoyi-common-security |
| Web 配置 | — | ✅ 继承 | 直接依赖 ruoyi-common-web |
| 社交登录 | feature-109 | ✅ 继承 | sys_social 表已定义 |

---

## 完成情况统计

| 类别 | 总数 | 已完成 | 完成率 |
|------|------|--------|--------|
| 整体文档 | 11 | 11 | 100% |
| tpl-manage 独有模块 | 2 | 2 | 100% |
| 继承 RVP 模块 | 18+ | 引用 | — |
| **前端 tpl-manage-ui 模块** | **2** | **引用** | **—** |
| **总计** | **13+** | **13** | **100%** |

---

## tpl-manage-ui 前端独有模块（feature-123+，引用关联工程 specs）

以下模块属于 tpl-manage-ui 前端工程，文档位于 `../tpl-manage-ui/specs/`：

| 模块 | 编号 | spec | plan | 状态 | 说明 |
|------|------|------|------|------|------|
| 品牌定制与登录注册 | feature-123 | ✅ | ✅ | 🟢 完成 | 登录/注册/首页视觉、布局、国际化 |
| 消息推送布局集成 | feature-124 | ✅ | ✅ | 🟢 完成 | SSE/WebSocket 消息推送集成 |

> 前端模块完整清单和测试用例见 `../tpl-manage-ui/specs/SPECS_CHECKLIST.md`。

---

## 更新日志

| 日期 | 操作 | 说明 |
|------|------|------|
| 2026-08-11 | 创建 | 初始化检查清单 |
| 2026-08-11 | 完成 | 所有项目级文档和模块级文档生成完毕：11 份项目级文档 + 2 模块 × 2 份文档(spec+plan) + 1 份 README = 16 份文档 |
| 2026-08-11 | 补充 | 添加 tpl-manage-ui 前端工程交叉引用：feature-123(品牌定制)、feature-124(消息推送集成) |
