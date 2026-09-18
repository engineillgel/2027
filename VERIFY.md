# 运行与验证指南

本文件用于在 **JDK 17 + Maven + MySQL 8 + Node 18+** 的机器上把系统完整跑通并验证。
推荐直接运行自动脚本 `bash verify.sh`（见文末），以下为逐命令手动清单，便于理解每一步在做什么。

---

## 0. 前置环境检查

```bash
java -version          # 需为 17.x
mvn -version           # 需存在
mysql --version        # 需为 8.x，且 MySQL 服务已启动（默认 3306）
node -v && npm -v      # 前端需要
```

> 本仓库源码位于 `backend/`（Spring Boot 3.1 / Java 17 / MySQL 8）、`frontend/`（Vue 3 + Vite）。
> 全部配置均可通过环境变量覆盖，无需改代码：
> `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` / `JWT_SECRET`。

---

## 1. 创建数据库

```bash
mysql -uroot -p -e "CREATE DATABASE IF NOT EXISTS job_mgt CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

---

## 2. 构建并启动后端

```bash
cd backend
mvn -DskipTests package

# 用环境变量传入 MySQL 密码（示例 root/root），后台启动
SPRING_DATASOURCE_URL='jdbc:mysql://localhost:3306/job_mgt?useUnicode=true&characterEncoding=utf8&serverTimezone=UTC' \
SPRING_DATASOURCE_USERNAME=root \
SPRING_DATASOURCE_PASSWORD=root \
nohup java -jar target/backend-0.0.1-SNAPSHOT.jar > ../backend.log 2>&1 &

# 等待就绪（Hibernate ddl-auto 会自动建表，含新增的 schedule 表），最多等 120 秒
# 用公开的登录接口探活（服务器就绪即返回 200，无效凭据返回 {"error":"invalid"}）；其余接口均已要求登录
for i in $(seq 1 60); do
  code=$(curl -s -o /dev/null -w '%{http_code}' -X POST 'http://localhost:8080/api/auth/login' \
    -H 'Content-Type: application/json' -d '{"username":"probe","password":"x"}')
  [ "$code" = "200" ] && echo "后端就绪 (${i}x2s)" && break
  sleep 2
done
```

**注意顺序**：必须先启动一次后端（自动建表），再导入种子数据。

---

## 3. 导入种子数据

```bash
cd ..   # 回到仓库根目录
mysql -uroot -proot job_mgt < seed-data.sql
```

种子内容：16 个岗位、50 名学生（密码均为 `123456`）、51 条申请、73 条排班。
> 前提：**全新空库**。若曾导入过，请先清空：`TRUNCATE users; TRUNCATE jobs; TRUNCATE applications; TRUNCATE schedule;`
> 演示账号：管理员 `admin`、学生 `student001`~`student050`，密码均 `123456`。

---

## 4. 后端接口冒烟测试（期望输出对照）

> 安全说明：除 `/api/auth/login`、`/api/auth/register` 外，所有接口均要求携带 Bearer token（未登录返回 401）。先登录拿 token，再访问业务接口。

```bash
# 0️⃣ 登录管理员拿 token
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}' | grep -o '"token":"[^"]*"' | head -1 | sed 's/.*"token":"//;s/"//')
AUTH="Authorization: Bearer $TOKEN"
echo "$TOKEN"

# ① 岗位列表分页：16 个岗位，每页 10 条 → 共 2 页
curl -s -H "$AUTH" 'http://localhost:8080/api/jobs?page=0&size=10'
#   期望包含 "totalElements":16  "totalPages":2  "numberOfElements":10

# ② 第 2 页：6 条
curl -s -H "$AUTH" 'http://localhost:8080/api/jobs?page=1&size=10'
#   期望 "numberOfElements":6

# ③ 关键词搜索：q=实验室 → 1 条（实验室管理员）
curl -s -H "$AUTH" 'http://localhost:8080/api/jobs?page=0&size=10&q=实验室'

# ④ 排班表：73 条
curl -s -H "$AUTH" 'http://localhost:8080/api/schedules'

# ⑤ 统计：16 岗位 / 50 学生 / 50 申请 / 状态分布 / 各岗位申请数
curl -s -H "$AUTH" 'http://localhost:8080/api/stats/overview'
#   期望 "totalJobs":16 "totalStudents":50 "totalApplications":51

# ⑥ 管理员的岗位（申请管理页数据源，可查全部岗位）
curl -s -H "$AUTH" http://localhost:8080/api/jobs/mine

# ⑦ 岗位 1 的申请列表（含学生姓名/班级）
curl -s -H "$AUTH" http://localhost:8080/api/apply/job/1

# ⑧ 审核：把岗位 1 的某条申请置为已录用（把 {ID} 换成上一步返回里的 id）
curl -s -X PUT -H "$AUTH" -H 'Content-Type: application/json' \
  -d '{"status":"ACCEPTED"}' http://localhost:8080/api/apply/{ID}
#   期望 {"message":"updated"}

# ⑨ 学生登录并查我的申请
STOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login -H 'Content-Type: application/json' \
  -d '{"username":"student001","password":"123456"}' | grep -o '"token":"[^"]*"' | head -1 | sed 's/.*"token":"//;s/"//')
curl -s -H "Authorization: Bearer $STOKEN" http://localhost:8080/api/apply/my

# ⑩ 学生申请岗位 3
curl -s -X POST -H "Authorization: Bearer $STOKEN" http://localhost:8080/api/apply/3
#   期望 {"message":"applied"}

# ⑪ CORS 预检：应返回 Access-Control-Allow-Origin
curl -s -D - -o /dev/null -X OPTIONS http://localhost:8080/api/jobs \
  -H 'Origin: http://localhost:5173' -H 'Access-Control-Request-Method: GET' | grep -i access-control-allow
```

---

## 5. 前端

```bash
cd frontend
npm install --legacy-peer-deps
npm run build        # 生产构建，产物在 dist/
npm run dev          # 开发模式，访问 http://localhost:5173
```

---

## 6. 浏览器人工核验清单（演示脚本）

| 页面 | 操作 | 预期 |
|---|---|---|
| `/` 岗位列表 | 翻页到第 2 页；搜索"实验室" | 分页正常、搜索命中 1 条 |
| `/schedule` 排班表 | 登录后访问 | 73 条排班，可搜索 |
| `/statistics` 统计看板 | 登录后访问 | 卡片数字 + 饼图 + 柱状图渲染 |
| `/login` | 用 `admin` / `123456` 登录 | 跳回列表页，右上角变"登出"，角色显示"管理员" |
| `/applications` 申请管理 | 登录管理员后访问，下拉选岗（默认"全部"） | 可见全部申请，可"录用/拒绝" |
| `/create` 发布岗位 | 管理员登录后访问 | 可发布新岗位 |
| 刷新 `/schedule`、`/applications` 等深层路由 | 直接按 F5 | 不再 404（nginx try_files 已配置） |

---

## 7. 自动验证

```bash
bash verify.sh
```

脚本会依次执行：环境检查 → 建库 → 后端构建 → 启动并等待就绪 → 导入种子 → 12 项 API 断言 → 前端构建 → 汇总（PASS/FAIL/SKIP），结束自动停止后端进程。可配置项（环境变量）：`MYSQL_USER` / `MYSQL_PASS` / `MYSQL_HOST` / `MYSQL_PORT` / `SEED_SQL`。

---

## 常见问题

- **后端启动报数据库连接失败**：确认 MySQL 已启动、`SPRING_DATASOURCE_PASSWORD` 与本地密码一致。
- **登录返回 `{"error":"invalid"}`**：确认种子数据已导入、且用 `123456` 登录；若此前注册过同名账号导致冲突，请用全新空库重导。
- **端口占用**：8080 被占用时改 `server.port`（application.yml）或停掉占用进程。
- **中文乱码**：确保导入时 `SET NAMES utf8mb4`（seed 文件已内置），且 MySQL 建库用 utf8mb4。
- **`seed-data.sql` 使用自增 ID 假设**：仅适用于全新空库；重复导入需先 TRUNCATE 上述 4 张表。
