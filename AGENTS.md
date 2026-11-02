# AGENTS.md — tpl-manage（管理后台后端）

> 本文件为本子工程（`tpl-manage/`）的 AI Agent 工作规范，是父工程 `tpl-workspace/AGENTS.md` 在本工程的具体化。
> 所有规范均基于本工程实际存在的技能、README 与 specs 编写。

---

## 一、项目简介

tpl-manage 是「tpl-workspace」产品的**管理后台服务端**，基于 **RuoYi-Vue-Plus 6.0.0** 构建
（Spring Boot 4.1 + JDK 21），为管理后台前端（tpl-manage-ui）提供服务。端口 **8081**。

核心定位：
- **用户管理**：注册用户查看/禁用/解锁
- **系统管理**：角色权限、菜单配置、操作日志等（继承 RuoYi-Vue-Plus）

## 二、技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| 后端框架 | Spring Boot 4.1 + JDK 21 | RVP 依赖管理统一版本 |
| 认证授权 | Sa-Token 1.45.0 + JWT | 无状态认证、注解式权限 |
| ORM | MyBatis-Plus 3.5.17 | CRUD、分页、数据权限插件 |
| 数据库 | PostgreSQL 15+ | 与核心业务统一选型 |
| 缓存 | Redis 7.0+ (Redisson 4.6.1) | 缓存、分布式锁 |
| 文件存储 | MinIO | Bucket: `tpl-manage` |
| Web 容器 | Jetty 12 (Netty) | 高性能异步容器 |
| 接口文档 | SpringDoc (javadoc) | 自动生成 |

## 三、代码结构

基于 RVP 的插件化多模块：`tpl-manage-admin`（启动入口）与若干业务/框架模块
（详见 `pom.xml` 与 `README.md`）。

## 四、规格文档（specs）

`specs/` 下存放规格：系统文档（`ARCHITECTURE.md`、`TECH.md`、`STRUCTURE.md`、`overall-api.md`、
`overall-data-model.md`、`constitution.md`）及功能规格 `feature-120-project-build`、`feature-121-tpl-auth`。

## 五、常用开发命令

```bash
./mvnw spring-boot:run     # 启动（默认 8081）
./mvnw clean package       # 打包
```

## 六、代码库探索规范（codegraph）

本工程已配置 codegraph 代码地图（`.codegraph/` 索引），并在 git `post-commit` 钩子中
自动增量更新（`codegraph sync`）。

- **提交后**无需手动操作，索引自动与 HEAD 保持同步。
- **探索代码**优先使用 codegraph，而非 grep/Read 循环：
  - `codegraph status` — 检查索引状态
  - `codegraph context "<任务>"` — 生成任务相关代码简报
  - `codegraph query <符号>` — 查找符号定义
  - `codegraph callers <符号>` / `codegraph callees <符号>` — 调用关系
  - `codegraph impact <符号>` — 变更影响范围
  - `codegraph files --filter <目录>` — 索引文件树
- 索引过期（切换分支/外部 pull）时手动 `codegraph sync` 增量更新。
- `codegraph` 查询不到时先 `sync` 再重查；确属 codegraph 不支持的语法才回退 grep。

> 索引目录 `.codegraph/` 已在 `.gitignore` 排除，不入库。

## 七、Git 提交规范

遵循父工程 `tpl-workspace/AGENTS.md` 的 Conventional Commits 规范，提交前加载 `git-commit` 技能。

## 八、开发流程与技能

- Java/Spring + RVP 开发遵循父工程开发流程（`specs-based-devflow`、`requesting-code-review`、`systematic-debugging`）。
- 代码库探索一律走 **codegraph**（见第六节）。

---

> 维护者：fellow99 · 属「tpl-workspace 多端业务应用框架」
