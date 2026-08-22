# feature-120-project-build 技术方案 (As-Built)

> This document is a retrospective technical plan documenting the actual architecture,
> design decisions, and implementation strategies as built.
> Module: feature-120-project-build
> Corresponding spec: [spec.md](./spec.md)
> Last Updated: 2026-08-11

## 1. 技术上下文

### 1.1 运行时环境 — 代码运行的位置
- **JDK：** 21
- **构建工具：** Maven 3.9+
- **数据库：** PostgreSQL 15+
- **缓存：** Redis 7.0+
- **文件存储：** MinIO

### 1.2 依赖 — 直接和间接

| 依赖 | GroupId | 版本 | 用途 |
|------|---------|------|------|
| ruoyi-vue-plus | org.dromara | 6.0.0 | RVP 父 POM（dependencyManagement import） |
| ruoyi-common-doc | org.dromara | 继承 | SpringDoc API 文档 |
| ruoyi-common-mail | org.dromara | 继承 | 邮件发送 |
| ruoyi-common-log | org.dromara | 继承 | 操作日志 AOP |
| ruoyi-common-security | org.dromara | 继承 | 安全配置 |
| ruoyi-common-web | org.dromara | 继承 | Web 配置（CORS/XSS） |
| ruoyi-api | org.dromara | 继承 | 跨模块 RPC 接口 |
| ruoyi-system | org.dromara | 继承 | 系统管理业务模块 |
| postgresql | org.postgresql | — | PostgreSQL JDBC 驱动 |
| spring-boot-admin-starter-client | de.codecentric | 继承 | SBA 监控客户端 |
| spring-boot-starter-test | org.springframework.boot | 继承 | 单元测试 |

---

## 2. 宪法合规

| 原则 | 状态 | 说明 |
|------|------|------|
| 不修改 RVP 源码 | ✅ | 通过 Maven 聚合引用，无物理复制 |
| 聚合构建原则 | ✅ | 根 POM modules 聚合 RVP + tpl-manage-admin |
| PostgreSQL 优先 | ✅ | 全部 DDL 使用 PostgreSQL 方言 |
| 配置覆盖而非修改 | ✅ | application.yml 覆盖 RVP 默认值 |
| 命名空间隔离 | ✅ | groupId=org.fellow99.tpl |

---

## 3. 研究结果

### 3.1 关键决策：Maven 聚合 vs 代码复制

**选择：** Maven 聚合工程

**理由：**
- RVP 代码零侵入，升级只需改版本号
- 代码审查只需关注 3 个 Java 文件
- Maven 原生支持，无需额外工具

**替代方案（未采用）：**
- Git Submodule — 管理复杂度高，CI/CD 集成困难
- 代码复制 — 维护成本高，升级困难

### 3.2 关键决策：PostgreSQL 替代 MySQL

**选择：** PostgreSQL

**理由：**
- 「tpl-workspace」产品线统一数据库选型
- 更好的 JSON 支持和复杂查询能力
- 许可证更友好

### 3.3 关键决策：雪花 ID vs 自增 ID

**选择：** 雪花 ID（继承 RVP 默认策略）

**配置：** `mybatis-plus.global-config.dbConfig.idType: ASSIGN_ID`

---

## 4. 数据模型

### 4.1 POM 结构

```
tpl-manage/pom.xml（根 POM）
├── groupId: org.fellow99.tpl
├── artifactId: tpl-manage
├── version: 1.0.0-SNAPSHOT
├── packaging: pom
├── <properties>
│   └── ruoyi-vue-plus.version: 6.0.0
├── <dependencyManagement>
│   └── org.dromara:ruoyi-vue-plus:6.0.0 (import)
└── <modules>
    ├── ../RuoYi-Vue-Plus/
    └── tpl-manage-admin/

tpl-manage/tpl-manage-admin/pom.xml（启动模块 POM）
├── parent: org.dromara:ruoyi-vue-plus:6.0.0
├── groupId: org.fellow99.tpl
├── artifactId: tpl-manage-admin
├── version: 1.0.0-SNAPSHOT
├── packaging: jar
└── <dependencies>
    ├── postgresql (runtime)
    ├── ruoyi-common-doc
    ├── ruoyi-common-mail
    ├── ruoyi-common-log
    ├── ruoyi-common-security
    ├── ruoyi-common-web
    ├── ruoyi-api
    ├── ruoyi-system
    ├── spring-boot-admin-starter-client
    └── spring-boot-starter-test (test)
```

### 4.2 配置覆盖关系

```
RVP 默认值                         → tpl-manage 覆盖值
server.port: 8080                 → 8081
spring.application.name: ruoyi-admin → tpl-manage
captcha.enable: true              → false
minio.bucketName: ruoyi-vue-plus  → tpl-manage
springdoc.info.title: RuoYi-Vue-Plus → tpl-workspace管理后台
spring.datasource.driver: MySQL   → PostgreSQL
```

### 4.3 数据库初始化脚本结构

```
sql/001-init-sys.sql
├── sys_social        (第三方登录绑定表)
├── sys_dept          (部门表 + 种子数据)
├── sys_user          (用户表 + admin 种子数据)
├── sys_post          (岗位表 + 种子数据)
├── sys_role          (角色表 + superadmin/test1/test2 种子数据)
├── sys_menu          (菜单表 + 完整菜单树种子数据)
├── sys_user_role     (用户-角色关联 + 种子数据)
├── sys_role_menu     (角色-菜单关联 + test1 授权种子数据)
├── sys_role_dept     (角色-部门关联)
├── sys_user_post     (用户-岗位关联 + 种子数据)
├── sys_oper_log      (操作日志表)
├── sys_dict_type     (字典类型表 + 种子数据)
└── sys_dict_data     (字典数据表 + 种子数据)
```

---

## 5. 接口契约

### 5.1 对外接口

本模块不提供 REST API。作为工程基础设施，它定义的是构建和部署契约：

- **构建入口：** tpl-manage 根目录 `mvn clean install`
- **产物：** `tpl-manage-admin/target/tpl-manage-admin.jar`
- **启动命令：** `java -jar tpl-manage-admin.jar --spring.profiles.active=dev`

### 5.2 消费接口

- **RuoYi-Vue-Plus Maven 模块** — 通过 `<modules>` 和 `<dependency>` 引用
- **PostgreSQL JDBC** — 数据库连接
- **Redis / MinIO** — 通过配置连接

---

## 6. 实现策略

### 6.1 架构模式

**Maven 聚合工程（Multi-Module Aggregator）**

根 POM 通过 `<modules>` 标签定义子模块，Maven Reactor 按依赖关系确定编译顺序。RuoYi-Vue-Plus 作为第一个子模块被编译，tpl-manage-admin 依赖 RVP 的构建产物。

### 6.2 级联编译原理

```
mvn clean install（在 tpl-manage 根目录执行）
  → Maven Reactor 分析模块依赖
  → 先编译 ../RuoYi-Vue-Plus/ (所有子模块)
  → install RVP 模块到本地仓库
  → 再编译 tpl-manage-admin (可引用本地仓库中的 RVP 模块)
  → install tpl-manage-admin 到本地仓库
```

### 6.3 配置文件层次

```
application.yml          (全局默认配置，所有环境共享)
    │
    ├── application-dev.yml      (开发环境，覆盖数据源/Redis/MinIO)
    └── application-docker.yml   (Docker 环境)
```

通过 `spring.profiles.active` 激活特定环境配置，使用 `---` 分隔符实现多文档 YAML。

### 6.4 PostgreSQL DDL 方言适配

| MySQL 语法 | PostgreSQL 语法 |
|------------|-----------------|
| `bigint AUTO_INCREMENT` | `int8`（配合雪花 ID） |
| `datetime` | `timestamp` |
| `COMMENT 'xxx'` | `comment on table/column ... is 'xxx'` |
| `char(1) DEFAULT '0'` | `char DEFAULT '0'::bpchar` |
| `tinyint(1)` | `bool` |
| `` `backtick` `` quoting | 无 quoting |

---

## 7. 测试考量

### 7.1 可测试项

- **Maven 构建验证** — `mvn clean install` 是否成功
- **SQL 脚本验证** — 在空 PostgreSQL 库中执行脚本是否无错误
- **JAR 启动验证** — 打包后 JAR 能否正常启动
- **配置加载验证** — 不同 profile 下配置是否正确覆盖

### 7.2 边界情况

- RVP 版本升级后是否仍有兼容性问题
- PostgreSQL 版本不匹配时 SQL 脚本是否报错
- 网络不畅时 Maven 依赖下载失败的处理

---

## 8. 文件清单

| 文件 | 用途 | 位置 |
|------|------|------|
| pom.xml | Maven 聚合工程 POM（根） | tpl-manage/ |
| tpl-manage-admin/pom.xml | 启动模块依赖管理 | tpl-manage/tpl-manage-admin/ |
| application.yml | 全局配置 | tpl-manage-admin/src/main/resources/ |
| application-dev.yml | 开发环境配置 | tpl-manage-admin/src/main/resources/ |
| application-docker.yml | Docker 环境配置 | tpl-manage-admin/src/main/resources/ |
| logback.xml | 日志配置 | tpl-manage-admin/src/main/resources/ |
| banner.txt | 启动横幅 | tpl-manage-admin/src/main/resources/ |
| 001-init-sys.sql | PostgreSQL 初始化脚本 | sql/ |
| .editorconfig | 代码格式化配置 | tpl-manage/ |
| mvnw / mvnw.cmd | Maven Wrapper | tpl-manage/ |
