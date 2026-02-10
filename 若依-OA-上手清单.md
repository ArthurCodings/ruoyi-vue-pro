# 若依 RuoYi-Vue-Pro + Flowable 上手清单

按顺序做完下面步骤，即可把项目跑起来并具备 OA 审批能力。**本清单针对 `master-jdk17` 分支**（JDK 17 + Spring Boot 3.x）。项目复制到本机后，直接按此清单执行即可。

---

## 一、环境准备（在复制项目前先装好）

| 项 | 版本建议 | 说明 |
|----|----------|------|
| JDK | **17** 或 21 | 当前为 `master-jdk17` 分支，需 JDK 17+（项目 `pom.xml` 中为 17） |
| Maven | 3.8+ | 后端构建 |
| Node.js | 18+ / 20 LTS | 管理后台前端构建，建议 LTS |
| pnpm | 8+ | 推荐，用于前端依赖管理（也可用 npm） |
| MySQL | 5.7 / 8.0+ | 单库 `ruoyi-vue-pro`，存业务表 + Flowable 表 |
| Redis | 6+ | 登录态、缓存 |
| 数据库工具 | Navicat / DBeaver / 命令行 | 执行 SQL、建库 |

---

## 二、获取项目

- 仓库：<https://github.com/YunaiV/ruoyi-vue-pro>（或 Gitee 镜像）
- **使用 JDK 17 版本**：克隆后切换到 `master-jdk17` 分支：
  ```bash
  git clone https://github.com/YunaiV/ruoyi-vue-pro.git
  cd ruoyi-vue-pro
  git checkout master-jdk17
  ```
- 把项目放在你本地目录（如 `e:\Work\OA\ruoyi-vue-pro`）即可。

---

## 三、数据库准备

1. **新建数据库**  
   - 建一个库：`ruoyi-vue-pro`（字符集建议 `utf8mb4`，排序规则 `utf8mb4_general_ci` 或官方文档推荐值）。  

2. **执行 SQL 脚本**（在项目根目录 `sql/mysql` 目录下）  
   - 在 `ruoyi-vue-pro` 库中执行 `ruoyi-vue-pro.sql`。  
   - 同样在 `ruoyi-vue-pro` 库中执行 `quartz.sql`（初始化定时任务相关表）。  

3. **关于 Flowable 表**  
   - Flowable 表会在启动开启 `yudao-module-bpm` 后，由配置 `flowable.database-schema-update=true` 自动建表，无需单独建库。  
   - 启动成功后，在 `ruoyi-vue-pro` 库中可以看到以 `ACT_` 等前缀开头的工作流相关表。  

---

## 四、后端配置与启动

1. **启用工作流 BPM 模块（只做一次）**  
   - 在根 `pom.xml` 中取消 `yudao-module-bpm` 模块的注释（`<modules>` 列表里）。  
   - 在 `yudao-server/pom.xml` 中取消 `yudao-module-bpm` 依赖的注释。  
   - 重新执行一次 `mvn clean install -DskipTests`，让 BPM 模块参与编译。  

2. **改配置文件**  
   - 在 `yudao-server/src/main/resources` 下找到 `application-local.yaml` 和 `application.yaml`。  
   - 核对 / 修改：  
     - 数据库 URL、用户名、密码（连接到上面建好的 `ruoyi-vue-pro` 库）。  
     - Redis 的 host、port、password（如有）。  
     - 端口号默认是 `48080`，一般保持不变即可。  

3. **安装依赖并启动后端**  
   ```bash
   cd <项目根目录>
   mvn clean install -DskipTests
   # 只启动后端主工程（使用 local 配置）
   mvn spring-boot:run -pl yudao-server
   ```  

4. **验证**  
   - 控制台无报错、有 "Started YudaoServerApplication" 类似日志。  
   - 浏览器访问：`http://localhost:48080` 能打开后台登录页（实际端口以 `application-local.yaml` 为准）。

---

## 五、前端选择与启动（管理后台）

1. **选择前端项目**  
   - 推荐：`yudao-ui-admin-vue3`（Vue3 + element-plus）。  
   - 在本项目的 `yudao-ui/yudao-ui-admin-vue3/README.md` 里有对应 Gitee / GitHub 仓库地址。

2. **克隆前端代码**  
   ```bash
   cd e:\Work\OA
   git clone https://gitee.com/yudaocode/yudao-ui-admin-vue3.git
   # 或使用 README 中给出的 GitHub 地址
   ```

3. **安装依赖**  
   ```bash
   cd yudao-ui-admin-vue3
   pnpm install
   # 或 npm install
   ```

4. **配置后端接口地址**  
   - 按前端仓库 README 找到 `.env.*` 或 `vite.config.*` 配置，把接口 base URL 改成 `http://localhost:48080`（与你后端端口一致）。  

5. **启动前端**  
   ```bash
   pnpm dev
   # 或 npm run dev
   ```  

6. **验证**  
   - 浏览器打开前端开发地址（例如终端输出的 `http://localhost:xxxx` 之类），能看到登录页。  
   - 使用默认管理员账号登录（参考前端/后端 README，一般是 `admin / admin123`），能进入后台、看到左侧菜单。

---

## 六、必看/必配的模块（和 OA 直接相关）

| 模块 | 位置/入口 | 建议先做 |
|------|-----------|----------|
| 组织架构 | 系统管理 → 部门管理、用户管理、岗位管理 | 先建好公司部门、岗位，再建用户并分配角色 |
| 角色与权限 | 系统管理 → 角色管理、菜单管理 | 确认「普通员工」「部门领导」「人事」等角色，菜单按角色分配 |
| 工作流（Flowable） | 工作流 或 流程管理 菜单 | 进「流程分类」「流程定义」，先部署 1 个示例流程（如请假） |
| 表单/流程表单 | 工作流 → 流程表单 或 表单设计 | 给示例流程绑定一个简单表单，再发起一次测试 |

---

## 七、第一次完整流程验证（建议必做）

1. **部署一个请假流程**  
   - 在工作流里用「流程设计」或导入 BPMN，做一个：发起 → 部门领导审批 → 结束。  
   - 保存并「部署」该流程。

2. **绑定表单**  
   - 建一个简单表单：请假类型、开始日期、结束日期、原因。  
   - 在流程里把「发起」节点绑定该表单。

3. **发起与审批**  
   - 用普通用户登录，在「我的申请」或「发起流程」里选请假流程，填表单提交。  
   - 用部门领导账号登录，在「待办」里看到该任务，审批通过。  
   - 再确认「已办」「我发起的」里能看到记录。

做到这里，说明 Flowable 和若依已打通，后续只需复制类似方式做报销、用印等流程。

---

## 八、可选：精简与收敛

- **菜单**：在「菜单管理」里隐藏或删除不需要的菜单（如商城、CRM 等），只保留：工作台、流程相关、组织架构、系统管理。  
- **依赖**：若确定不用某些模块（如支付、某第三方登录），可在父 POM 或对应模块里注释掉，减少依赖和启动时间。  

先能跑、能审一条流程，再慢慢精简更稳妥。

---

## 九、后续你可以按这个顺序扩展

1. 增加 2～3 个常用流程：报销、出差、用印等（复制请假流程改表单和节点即可）。  
2. 工作台：若依若带「工作台」或「待办汇总」，配置到首页；没有就自己在菜单里加一个「待办列表」入口。  
3. 通知：审批结果站内信/邮件（若依一般有站内信或通知表，可对接 Flowable 的流程事件）。  
4. 再往后：接 IM（如融云/环信）、公告、简单文档等（对应你之前说的分阶段计划）。

---

## 十、常见问题速查

| 现象 | 可能原因 | 处理 |
|------|----------|------|
| 编译报「-source 8 不支持 instanceof 可具体化类型」等 | Maven 实际用了 `-source 8`，常见原因：本机 `~/.m2/settings.xml` 里某 profile 设置了 `maven.compiler.source=1.8`，覆盖了项目里的 JDK 17 | 根 `pom.xml` 已在 compiler 插件里显式指定 `<source>${java.version}</source>`，一般可解决；若仍有问题，检查并去掉 settings 里对 `maven.compiler.source/target` 的 1.8 配置 |
| IDE 运行报「Command line is too long」 | Windows 下命令行长度有限制，依赖多时 classpath 超长 | 在 IDEA：运行配置 → 勾选「Modify options」→「Shorten command line」→ 选 **JAR manifest** 或 **classpath file**，保存后重跑 |
| 后端启动报数据库连接错误 | 库名/账号密码/端口不对 | 核对 `yudao-server` 下 `application*.yaml` 里 datasource 与 MySQL 实际配置 |
| 前端请求 404 / 跨域 | 后端未启动或端口不一致 | 确认后端已启、前端环境变量里接口地址和端口正确 |
| 登录后没有「工作流」菜单 | 角色未分配工作流菜单权限 | 在「菜单管理」给对应角色勾选工作流相关菜单 |
| 流程部署失败 / 找不到表 | Flowable 表未自动创建或 BPM 未启用 | 确认已启用 `yudao-module-bpm` 并启动过；Flowable 表由 `database-schema-update=true` 自动建在 `ruoyi-vue-pro` 库 |
| 待办列表为空 | 流程未部署或未绑定表单 | 先部署流程并绑定表单，再发起一条测试 |

---

把项目复制下来后，从「二、获取项目」开始，按顺序做到「七、第一次完整流程验证」即可完成首轮上手。也可以对照官方文档的「快速开始」教程（`https://doc.iocoder.cn/quick-start/`）一起看。  
如有某一步报错，把报错信息和你当前执行到的步骤发出来，我可以按步骤帮你排查。
