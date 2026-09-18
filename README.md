# 高校勤工助学岗位管理系统（Work-Study）

> 本科毕业设计配套的**可运行系统 Demo** —— 基于 Spring Boot 3 + Vue 3 + MySQL 8 的前后端分离实现，
> 覆盖勤工助学岗位「发布 → 申请 → 审核 → 排班 → 统计」的完整业务闭环，内置 50 名学生的演示数据，开箱即可演示。

![系统预览](frontend/public/preview.jpg)

---

## 目录

- [功能概览](#功能概览)
- [技术栈](#技术栈)
- [系统架构](#系统架构)
- [快速开始](#快速开始)
  - [方式一：Docker 一键启动](#方式一docker-一键启动推荐)
  - [方式二：本地手动启动](#方式二本地手动启动)
- [演示账号与数据](#演示账号与数据)
- [API 接口一览](#api-接口一览)
- [数据库设计](#数据库设计)
- [运行验证](#运行验证)
- [常见问题](#常见问题)

---

## 功能概览

系统设置 **学生（STUDENT）** 与 **管理员（ADMIN）** 两种角色，登录后按角色动态呈现菜单并做路由级权限拦截。

### 学生端

| 功能 | 说明 |
|---|---|
| 岗位浏览 | 岗位列表分页展示，支持按标题关键词搜索 |
| 在线申请 | 一键申请岗位，重复申请与已录用状态做后端校验 |
| 我的申请 | 查看本人全部申请记录及实时审核状态（已申请 / 已录用 / 已拒绝） |
| 个人排班 | 查看本人被安排的班次（星期 / 起止时间 / 关联岗位） |
| 系统通知 | 所申请岗位被管理员删除时自动收到站内通知，弹窗提醒后自动清除 |

### 管理端

| 功能 | 说明 |
|---|---|
| 岗位管理 | 发布新岗位、删除岗位（删除时联动通知相关学生） |
| 申请审核 | 按岗位筛选查看全部学生申请（含姓名、班级），执行「录用 / 拒绝」 |
| 排班管理 | 对已录用学生增删改排班记录 |
| 用户管理 | 查看学生名单，启用 / 禁用账号（禁用后无法登录） |
| 统计看板 | 岗位总数、学生人数、申请总数、已录用数四张指标卡 + 申请状态饼图 + 各岗位申请人数柱状图 |

### 通用

- **JWT 无状态认证**：登录签发 token，前端 Axios 拦截器自动注入 `Authorization` 头。
- **统一登录弹窗**：未登录访问任意页面均弹出登录/注册模态框，登录后回到原路径；token 失效（401）自动登出并重新唤起弹窗。
- **角色路由守卫**：直接敲 URL 访问越权页面时，前端守卫与后端 `SecurityConfig` 双重拦截。
- **响应式布局**：Element Plus + 自定义全局样式，适配常见桌面分辨率。

---

## 技术栈

**后端**

| 组件 | 版本 / 说明 |
|---|---|
| Spring Boot | 3.1.4 |
| Java | 17 |
| Spring Security | JWT 无状态鉴权 + BCrypt 密码加密 |
| JJWT | 0.11.5（`jjwt-api` / `jjwt-impl` / `jjwt-jackson`） |
| Spring Data JPA | Hibernate，`ddl-auto: update` 自动建表 |
| MySQL | 8.0（utf8mb4） |
| Lombok | 简化实体类样板代码 |
| 构建 | Maven |

**前端**

| 组件 | 版本 / 说明 |
|---|---|
| Vue | 3.3（Composition API） |
| Vite | 5.x 构建与开发服务器 |
| Vue Router | 4.x（`createWebHistory` + 全局前置守卫） |
| Pinia | 2.x 状态管理（`store/auth.js` 统一管理登录态） |
| Element Plus | 2.3 组件库 |
| ECharts | 5.4 统计图表 |
| Axios | 请求封装与 JWT 拦截器 |
| Phosphor Icons | 导航图标 |

**部署**：Docker / docker-compose（MySQL + 后端 + Nginx 三容器），Nginx 配置 SPA `try_files` 回退。

---

## 系统架构

```text
┌──────────────────────────┐        ┌──────────────────────────┐        ┌──────────────┐
│  浏览器 (Vue 3 SPA)       │        │  Spring Boot 3 后端       │        │   MySQL 8    │
│  Vite Dev :5173          │  HTTP  │  REST API :8080          │  JPA   │   job_mgt    │
│  / Nginx :5173 (Docker)  │ ─────► │  Security + JWT Filter   │ ─────► │  utf8mb4     │
└──────────────────────────┘        └──────────────────────────┘        └──────────────┘
```

后端采用经典分层：`Controller`（路由与参数）→ `Service`（业务逻辑）→ `Repository`（JPA 数据访问）→ `Model`（实体）；
`security/` 包集中存放 JWT 工具、认证过滤器、用户主体与安全配置。

### 目录结构

```text
Work-Study/
├── backend/                          # Spring Boot 后端
│   ├── src/main/java/edu/example/backend/
│   │   ├── BackendApplication.java   # 启动类
│   │   ├── controller/               # Auth / Job / Apply / Schedule / Notification / Stats / Admin
│   │   ├── service/                  # 对应业务逻辑
│   │   ├── repository/               # Spring Data JPA 仓储
│   │   ├── model/                    # User / Job / Application / Schedule / Notification
│   │   └── security/                 # JwtUtil、JwtAuthenticationFilter、SecurityConfig
│   ├── src/main/resources/application.yml
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                         # Vue 3 前端
│   ├── src/
│   │   ├── views/                    # JobList / JobCreate / Applications / MyApplications
│   │   │                             # Schedule / Statistics / UserManagement
│   │   ├── components/AuthModal.vue  # 登录 / 注册弹窗
│   │   ├── store/auth.js             # Pinia 登录态
│   │   ├── router/index.js           # 路由与角色守卫
│   │   ├── api.js                    # Axios 实例与拦截器
│   │   ├── assets/global.css
│   │   ├── App.vue
│   │   └── main.js
│   ├── public/preview.jpg
│   ├── nginx.conf                    # SPA 回退 + 静态资源
│   ├── Dockerfile
│   └── package.json
├── database/job_mgt.sql              # 数据库完整备份（含全部演示数据）
├── seed-data.sql                     # 种子数据（仅用于全新空库）
├── docker-compose.yml                # 三容器编排
├── .env.example                      # 环境变量样例
├── verify.sh                         # 一键自动化验证脚本
├── VERIFY.md                         # 详细的运行与验证指南
└── README.md
```

---

## 快速开始

### 前置条件

| 方式 | 需要准备 |
|---|---|
| Docker | Docker Desktop（含 compose） |
| 本地运行 | JDK 17+、Maven 3.8+、MySQL 8.0、Node.js 18+ |

### 方式一：Docker 一键启动（推荐）

```bash
# 可选：自定义密码等配置
cp .env.example .env

docker-compose up --build
```

启动后三个容器分别为：

| 服务 | 容器 | 宿主机端口 | 说明 |
|---|---|---|---|
| db | mysql:8.0 | 3306 | 数据库 `job_mgt`，数据持久化在 `db_data` 卷 |
| backend | Spring Boot | 8080 | REST API |
| frontend | Nginx | 5173 | 提供构建后的前端静态资源 |

访问 **http://localhost:5173** 即可使用。

> 停止服务：`docker-compose down`；连带清空数据卷：`docker-compose down -v`。

### 方式二：本地手动启动

**1. 创建数据库**

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS job_mgt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

**2. 启动后端**（首次启动会自动建表）

```bash
cd backend
mvn -DskipTests package

# 数据库连接与 JWT 密钥均可通过环境变量覆盖，无需改代码
SPRING_DATASOURCE_USERNAME=root \
SPRING_DATASOURCE_PASSWORD=root \
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

**3. 导入演示数据**（须在后端首次启动建表之后执行）

```bash
# 回到仓库根目录
mysql -uroot -p job_mgt < seed-data.sql
```

> 也可直接导入完整备份 `mysql -uroot -p < database/job_mgt.sql`（含 16 岗位 / 50 学生 / 51 申请 / 73 排班）。

**4. 启动前端**

```bash
cd frontend
npm install          # 依赖冲突时可加 --legacy-peer-deps
npm run dev          # http://localhost:5173
```

**可配置的环境变量**

| 变量 | 默认值 | 说明 |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3306/job_mgt?...` | 数据库连接串 |
| `SPRING_DATASOURCE_USERNAME` | `root` | 数据库用户名 |
| `SPRING_DATASOURCE_PASSWORD` | `root` | 数据库密码 |
| `JWT_SECRET` | `ChangeThisSecretKeyForDemo_ChangeMe` | JWT 签名密钥，**生产环境务必修改** |

---

## 演示账号与数据

| 角色 | 用户名 | 密码 |
|---|---|---|
| 管理员 | `admin` | `123456` |
| 学生 | `student001` ~ `student050` | `123456` |

内置演示数据规模：

| 数据 | 数量 |
|---|---|
| 岗位 | 16 条（办公室助理、实验室管理员、图书管理员、机房值班员、校报编辑助理……） |
| 学生 | 50 名（覆盖 2024–2026 级多个专业班级） |
| 申请记录 | 51 条 |
| 排班记录 | 73 条 |

---

## API 接口一览

除标注为**公开**的接口外，其余均需在请求头携带 `Authorization: Bearer <token>`，未登录返回 `401`；
`/api/admin/**` 额外要求 `ADMIN` 角色。

### 认证 `/api/auth`

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/auth/register` | 注册（公开） |
| POST | `/api/auth/login` | 登录，返回 JWT（公开） |
| GET | `/api/auth/me` | 获取当前登录用户信息 |

### 岗位 `/api/jobs`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/jobs?page=&size=&q=` | 岗位分页列表，`q` 为标题关键词 |
| GET | `/api/jobs/mine` | 我发布的岗位 |
| POST | `/api/jobs` | 发布岗位（管理员） |
| GET | `/api/jobs/{id}` | 岗位详情 |
| DELETE | `/api/jobs/{id}` | 删除岗位（管理员，联动通知申请过的学生） |

### 申请 `/api/apply`

| 方法 | 路径 | 说明 |
|---|---|---|
| POST | `/api/apply/{jobId}` | 申请岗位（学生） |
| GET | `/api/apply/my` | 我的申请（学生） |
| GET | `/api/apply/job/{jobId}` | 某岗位的申请列表（管理员，含学生姓名与班级） |
| GET | `/api/apply/all` | 全部申请（管理员） |
| PUT | `/api/apply/{id}` | 审核申请，体 `{"status":"ACCEPTED"\|"REJECTED"}`（管理员） |

### 排班 `/api/schedules`

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/schedules` | 全部排班 |
| GET | `/api/schedules/my` | 我的排班 |
| POST | `/api/schedules` | 新增排班（管理员） |
| PUT | `/api/schedules/{id}` | 修改排班（管理员） |
| DELETE | `/api/schedules/{id}` | 删除排班（管理员） |

### 统计 / 通知 / 用户管理

| 方法 | 路径 | 说明 |
|---|---|---|
| GET | `/api/stats/overview` | 统计看板数据（总数、状态分布、各岗位申请数） |
| GET | `/api/notifications/my` | 我的通知（学生） |
| DELETE | `/api/notifications/my` | 清空我的通知 |
| GET | `/api/admin/users` | 学生用户列表（管理员） |
| PUT | `/api/admin/users/{id}` | 启用 / 禁用用户（管理员） |

---

## 数据库设计

库名 `job_mgt`，字符集 `utf8mb4`，共 5 张表：

| 表 | 对应实体 | 关键字段 |
|---|---|---|
| `users` | User | `username`、`password`(BCrypt)、`role`(STUDENT/ADMIN)、`realname`、`class_name`、`enabled` |
| `jobs` | Job | `employer_id`、`title`、`description`、`location`、`created_at` |
| `applications` | Application | `job_id`、`student_id`、`status`(APPLIED/ACCEPTED/REJECTED)、`applied_at` |
| `schedule` | Schedule | `student_id`、`job_id`、`day_of_week`、`start_time`、`end_time` |
| `notifications` | Notification | `student_id`、`content`、`created_at` |

- 建表方式：Hibernate `ddl-auto: update`，后端首次启动自动创建/更新表结构。
- 数据只存在于 MySQL 实例中，独立于源码与运行进程，重装代码不影响数据。
- **备份**：
  ```bash
  mysqldump -uroot -p --databases job_mgt --default-character-set=utf8mb4 \
    --set-gtid-purged=OFF --result-file="database/job_mgt.sql"
  ```

---

## 运行验证

仓库提供一键验证脚本，依次执行：环境检查 → 建库 → 后端构建 → 启动并等待就绪 → 导入种子数据 →
**12 项 API 断言** → 前端构建 → 输出 PASS/FAIL/SKIP 汇总，结束后自动停止后端进程。

```bash
bash verify.sh
```

可覆盖的环境变量：`MYSQL_USER` / `MYSQL_PASS` / `MYSQL_HOST` / `MYSQL_PORT` / `SEED_SQL`。

逐命令的手动验证清单、接口期望输出对照以及浏览器人工核验脚本（演示流程），详见 **[VERIFY.md](VERIFY.md)**。

---

## 常见问题

| 现象 | 排查方向 |
|---|---|
| 后端启动报数据库连接失败 | 确认 MySQL 已启动，且 `SPRING_DATASOURCE_PASSWORD` 与本地密码一致 |
| 登录返回 `{"error":"invalid"}` | 确认种子数据已导入；若曾注册过同名账号造成冲突，请用全新空库重导 |
| 8080 端口被占用 | 修改 `application.yml` 中的 `server.port`，或停掉占用进程 |
| 中文乱码 | 建库使用 `utf8mb4`，导入时确保 `SET NAMES utf8mb4`（seed 文件已内置） |
| `seed-data.sql` 重复导入报错 | 该脚本假设自增 ID 从 1 开始，仅适用于全新空库；重导前先清空 `users`、`jobs`、`applications`、`schedule` 四张表 |
| 刷新深层路由出现 404 | 已由 `frontend/nginx.conf` 的 `try_files` 处理；开发模式请使用 `http://localhost:5173` |

---

## 说明

本项目为毕业设计配套演示系统，用于功能验证与答辩演示。
默认配置中的数据库密码与 `JWT_SECRET` 均为演示值，部署到公网前请务必替换。
