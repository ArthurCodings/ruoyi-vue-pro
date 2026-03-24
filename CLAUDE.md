# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## AI 能力配置

### MiniMax MCP 工具（强制使用）

所有涉及以下任务时，**必须**使用 MiniMax MCP 工具：

| 任务类型 | MCP 工具 | 说明 |
|---------|---------|------|
| **识图/图像理解** | `mcp__MiniMax__understand_image` | 分析图片内容、提取信息、描述界面 |
| **联网搜索** | `mcp__MiniMax__web_search` | 搜索最新信息、技术文档、解决方案 |
| **图片转文字（OCR）** | `mcp__MiniMax__understand_image` | 从截图、UI 中提取文字 |

**使用规则**：
- 用户发送截图/图片时 → 使用 `mcp__MiniMax__understand_image` 分析
- 需要搜索最新信息时 → 使用 `mcp__MiniMax__web_search`
- 禁止使用其他联网/识图工具（如 WebSearch 通用工具）

### Everything Claude Code Skills（强制检查）

**所有问题回答前**，必须检查是否有对应的 everything-claude-code skill：

| 类别 | Skill |
|------|-------|
| Java/Spring Boot | `everything-claude-code:java-coding-standards`、`everything-claude-code:springboot-patterns` |
| 前端问题 | `everything-claude-code:frontend-patterns` |
| 安全相关 | `everything-claude-code:security-review` |
| 数据库/SQL | `everything-claude-code:postgres-patterns` |
| 架构设计 | `everything-claude-code:plan`、`everything-claude-code:backend-patterns` |
| 代码审查 | `everything-claude-code:coding-standards` |

---

## 项目概述

基于 **RuoYi-Vue-Pro + Flowable** 的 OA（办公自动化）系统后端，`master-jdk17` 分支采用 Spring Boot 3.x + JDK 17。

- **主框架**：Spring Boot 3.5.9 + Spring Security 6
- **持久层**：MyBatis Plus + MapStruct
- **工作流**：Flowable 7.0（`yudao-module-bpm`）
- **缓存**：Redis + Redisson
- **数据库**：MySQL（`ruoyi-vue-pro` 库，utf8mb4）
- **代码生成**：内置 CRUD 生成器（`yudao-module-infra`）

---

## 常用命令

```bash
# 编译安装（所有模块）
mvn clean install -DskipTests

# 只编译 server 及其依赖（更快）
mvn clean package -pl yudao-server -am -DskipTests

# 运行后端（使用 application-local.yaml）
mvn spring-boot:run -pl yudao-server

# 运行单测
mvn test -pl yudao-server

# 运行单个测试类
mvn test -pl yudao-server -Dtest=AttendanceScheduleServiceImplTest
```

### 数据库初始化

在 MySQL `ruoyi-vue-pro` 库中依次执行：
1. `sql/mysql/ruoyi-vue-pro.sql` — 主体表结构
2. `sql/mysql/quartz.sql` — 定时任务表
3. `sql/mysql/bpm-tables.sql` — BPM 业务表（必须执行，否则流程分类报错）
4. Flowable 表由 `flowable.database-schema-update=true` 启动时自动创建

### 启用 BPM 模块

首次使用需取消注释两处 `pom.xml`：
1. 根 `pom.xml` 的 `<modules>` 中取消 `<module>yudao-module-bpm</module>` 注释
2. `yudao-server/pom.xml` 中取消 `yudao-module-bpm` 依赖的注释

---

## 目录结构

```
ruoyi-vue-pro/
├── yudao-dependencies/      # Maven BOM，版本管理
├── yudao-framework/         # 公共基础设施
│   ├── yudao-common/       # 通用工具、PO/BO/DTO 基类
│   └── yudao-spring-boot-starter-*/  # 各 Starter（security、mybatis、redis 等）
├── yudao-server/           # 主应用入口（打包只打此模块）
│   └── src/main/resources/
│       ├── application.yaml         # 主配置
│       └── application-local.yaml   # 本地/部署配置（当前激活 profile）
└── yudao-module-*/         # 功能模块
    └── src/main/java/cn/iocoder/yudao/module/xxx/
        ├── api/            # 外部 API（供其他模块调用）
        │   ├── dept/       #   └─ DTO + ApiImpl
        │   ├── user/
        │   └── ...
        ├── controller/     # REST 接口
        │   ├── admin/      #   └─ 按业务分子目录（hr/、dept/ 等）
        │   └── app/        #   └─ APP 端接口
        ├── service/        # 业务逻辑（接口 + Impl）
        ├── dal/            # 数据访问层
        │   ├── dataobject/ #   └─ DO 实体类
        │   └── mysql/      #   └─ Mapper 接口
        └── convert/        # MapStruct 对象转换
```

---

## 模块规范

### 四层结构

每个 `yudao-module-xxx` 遵循标准四层：

```
controller  → service  →  dal  →  MySQL
  (REST)     (业务)    (Mapper)  (MyBatis)
```

**Controller 层**：`@Tag`、`@Operation` 注解完整，路径前缀 `/admin-api/模块/功能`，使用 `CommonResult<T>` 统一返回：
```java
@Tag(name = "管理后台 - HR 排班规则")
@RestController
@RequestMapping("/system/attendance-rule")
public class XxxController {
    @PreAuthorize("@ss.hasPermission('模块:功能:操作')")
    public CommonResult<T> get(@RequestParam("id") Long id) {
        return success(service.getXxx(id));
    }
}
```

**Service 层**：接口 + Impl 分离，使用 `@Resource` 注入。业务逻辑在 Impl 中实现。

**DAL 层**：`yudao-module-xxx/src/main/resources/mapper/` 下放 Mapper XML，Mapper 接口用 MyBatis-Plus 继承 `BaseMapper`。

**DO 命名**：如 `AttendanceRuleDO`、`HrEmployeeDO`，放在 `dal/dataobject/` 下。

### VO 命名规范

| 类型 | 后缀 | 示例 | 所在目录 |
|------|------|------|---------|
| 请求（分页） | `PageReqVO` | `AttendanceRulePageReqVO` | `vo/xxx/` |
| 请求（新增） | `SaveReqVO` | `AttendanceRuleSaveReqVO` | `vo/xxx/` |
| 请求（更新） | `SaveReqVO` | 同上，增删共用 | `vo/xxx/` |
| 响应 | `RespVO` | `AttendanceRuleRespVO` | `vo/xxx/` |
| 个人中心 | `xxxVO` | `PersonalCenterPanelVO` | `vo/profile/` |

### API 模块间调用

跨模块调用使用 `api/` 子包定义接口。例如 `yudao-module-system` 中定义 `UserApi`，`yudao-module-infra` 中通过 `DeptApi` 调用：
```java
// yudao-module-infra 的 pom.xml 引入依赖
// yudao-module-system 中定义 XxxApi 接口 + XxxApiImpl 实现
// 调用方注入 XxxApi 使用
```

### 权限注解

- `@PreAuthorize("@ss.hasPermission('system:xxx:query')")` — 需要特定权限
- `@PermitAll` — 所有已登录用户可访问（个人中心接口常用）
- 权限标识格式：`模块:功能:操作`（如 `system:attendance-rule:update`）

---

## 配置管理

- **配置文件**：`yudao-server/src/main/resources/application-*.yaml`
- **激活 profile**：`application.yaml` 中 `spring.profiles.active: local`
- **默认端口**：`48080`
- **后端 API 前缀**：`/admin-api`（Swagger 文档：`/admin-api/doc.html`）

---

## 关键注意事项

1. **JDK 版本**：必须 JDK 17+，否则编译报错
2. **BPM 模块**：需取消两处 pom 注释后重新 `mvn install`
3. **编译报错 "source 8"**：检查 `~/.m2/settings.xml` 是否有 `maven.compiler.source=1.8`
4. **IDEA "Command line is too long"**：运行配置 → Modify options → Shorten command line → JAR manifest
5. **菜单权限**：新增功能需在「菜单管理」分配权限，否则普通用户看不到
6. **代码生成器**：`yudao-module-infra` 提供在线代码生成，适合快速新增 CRUD

---

## 业务模块（当前已启用）

| 模块 | 路径 | 说明 |
|------|------|------|
| `yudao-module-system` | `cn.iocoder.yudao.module.system` | 系统管理 + **HR 人事** |
| `yudao-module-infra` | `cn.iocoder.yudao.module.infra` | 基础设施 + 在线文档 + 合同管理 |
| `yudao-module-bpm` | `cn.iocoder.yudao.module.bpm` | 工作流（Flowable） |

### HR 人事模块（system 模块下的 hr 子包）

已在 `yudao-module-system` 下扩展：
- **考勤**：`controller/admin/hr/attendance/`（排班规则、节假日、排班表、打卡记录）
- **薪资**：`controller/admin/hr/salary/`（薪资配置、员工薪资档案、月度薪资、绩效）
- **花名册**：`controller/admin/hr/employee/`（员工档案）
- **个人中心**：`controller/admin/hr/profile/`（工作台聚合面板）

详细设计见 [人事专栏-开发指南.md](人事专栏-开发指南.md)。

### 在线文档与合同（infra 模块）

- **文档**：`infra/controller/admin/doc/`（分类 + 文档，支持 docx/xlsx/pdf 预览）
- **合同**：`infra/controller/admin/contract/`（合同管理，含提成计算）

---

## 数据库表规范

- **通用字段**：`id`（主键）、`creator`/`create_time`/`updater`/`update_time`（审计）、`deleted`（逻辑删除）、`tenant_id`（租户）
- **Flowable 表前缀**：`ACT_`（自动创建）
- **BPM 业务表前缀**：`bpm_`（需执行 `bpm-tables.sql` 手动创建）
- **HR 表前缀**：`hr_`（如 `hr_attendance_rule`、`hr_employee`）

---

## 登录凭证

默认管理员账号：`admin / admin123`（数据库 `system_user` 表中修改）

---

## 相关文档

- 后端上手：[若依-OA-上手清单.md](若依-OA-上手清单.md)
- 部署指南：[部署指南.md](部署指南.md)
- HR 开发指南：[人事专栏-开发指南.md](人事专栏-开发指南.md)
- 消息开发指南：[我的消息-开发指南.md](我的消息-开发指南.md)
- 前端项目：[yudao-ui-admin-vue3](../yudao-ui-admin-vue3/)（独立克隆仓库）
