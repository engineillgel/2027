#!/usr/bin/env bash
# 高校勤工助学岗位管理系统 · 自动验证脚本
# 前置：JDK 17 + Maven + MySQL 8（运行中）+ Node 18+
# 用法：bash verify.sh
# 可配置环境变量：MYSQL_USER / MYSQL_PASS / MYSQL_HOST / MYSQL_PORT / SEED_SQL / BACKEND_PORT
set -u

PASS=0; FAIL=0; SKIP=0
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASS="${MYSQL_PASS:-root}"
MYSQL_HOST="${MYSQL_HOST:-localhost}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
DB_NAME="job_mgt"
SEED_SQL="${SEED_SQL:-seed-data.sql}"
BACKEND_PORT="${BACKEND_PORT:-8080}"
BASE="http://localhost:${BACKEND_PORT}"
ROOT="$(cd "$(dirname "$0")" && pwd)"
BACKEND_PID=""

log(){ printf '[%s] %s\n' "$(date +%H:%M:%S)" "$*"; }
ok(){  PASS=$((PASS+1)); log "  ✅ $*"; }
bad(){ FAIL=$((FAIL+1)); log "  ❌ $*"; }
skip(){ SKIP=$((SKIP+1)); log "  ⏭️  $*"; }

cleanup(){
  [ -n "$BACKEND_PID" ] && kill "$BACKEND_PID" 2>/dev/null && log "已停止后端进程 (PID $BACKEND_PID)"
}
trap cleanup EXIT

# ---------- 0. 环境检查 ----------
log "== 0. 环境检查 =="
# 优先使用 JAVA_HOME 下的 Java，避免 PATH 上是旧版本
if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
  JAVA_CMD="$JAVA_HOME/bin/java"
else
  JAVA_CMD="java"
fi
_java_major() { "$1" -version 2>&1 | head -1 | sed 's/.*version "\([^"]*\)".*/\1/' | cut -d. -f1; }
jv=$(_java_major "$JAVA_CMD")
if [ "$jv" = "1" ]; then jv=$("$JAVA_CMD" -version 2>&1 | head -1 | sed 's/.*version "\([^"]*\)".*/\1/' | cut -d. -f2); fi
if command -v "$JAVA_CMD" >/dev/null && [ "${jv:-0}" -ge 17 ]; then ok "Java $jv (>=17)  [$JAVA_CMD]"; else bad "需要 Java 17+（当前: $("$JAVA_CMD" -version 2>&1 | head -1)）"; fi
if command -v mvn >/dev/null; then ok "Maven"; else bad "未找到 Maven"; fi
if command -v mysql >/dev/null; then ok "MySQL 客户端"; else bad "未找到 MySQL 客户端"; fi
if command -v node >/dev/null; then ok "Node $(node -v)"; else bad "未找到 Node"; fi
if command -v npm >/dev/null; then ok "npm $(npm -v)"; else bad "未找到 npm"; fi

# ---------- 1. 建库 ----------
log "== 1. 创建数据库 ${DB_NAME} =="
export MYSQL_PWD="$MYSQL_PASS"
if mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER" -e \
   "CREATE DATABASE IF NOT EXISTS ${DB_NAME} CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null; then
  ok "数据库就绪"
else
  bad "建库失败（请检查 MYSQL_USER/MYSQL_PASS，当前 user=$MYSQL_USER）"
  log "可指定：MYSQL_USER=xxx MYSQL_PASS=xxx bash verify.sh"
  exit 1
fi

# ---------- 2. 后端构建 ----------
log "== 2. 构建后端 =="
cd "$ROOT/backend" || exit 1
if mvn -q -DskipTests package >/dev/null 2>&1; then ok "mvn package 成功"; else bad "mvn package 失败（请安装 JDK17+Maven 并检查依赖）"; exit 1; fi

# ---------- 3. 启动后端并等待就绪 ----------
log "== 3. 启动后端并等待就绪 =="
SPRING_DATASOURCE_URL="jdbc:mysql://${MYSQL_HOST}:${MYSQL_PORT}/${DB_NAME}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&connectionTimeZone=Asia/Shanghai" \
SPRING_DATASOURCE_USERNAME="$MYSQL_USER" \
SPRING_DATASOURCE_PASSWORD="$MYSQL_PASS" \
nohup "$JAVA_CMD" -jar target/backend-0.0.1-SNAPSHOT.jar > "$ROOT/backend.log" 2>&1 &
BACKEND_PID=$!

ready=0
for i in $(seq 1 60); do
  # 登录为公开接口：服务器就绪即返回 200（无效凭据返回 {"error":"invalid"}）；其余接口均已要求登录
  code=$(curl -s -o /dev/null -w '%{http_code}' -X POST "$BASE/api/auth/login" \
    -H 'Content-Type: application/json' -d '{"username":"probe","password":"x"}' 2>/dev/null)
  if [ "$code" = "200" ]; then ready=1; ok "后端就绪（${i}次x2s）"; break; fi
  sleep 2
done
if [ "$ready" = "0" ]; then
  bad "后端 120s 内未就绪，查看 $ROOT/backend.log"; exit 1
fi

# ---------- 4. 导入种子数据 ----------
log "== 4. 导入种子数据 ${SEED_SQL} =="
cd "$ROOT"
if mysql -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER" "$DB_NAME" < "$SEED_SQL" 2>/dev/null; then ok "种子数据导入成功"; else bad "导入失败（请确认种子文件存在且为全新空库）"; exit 1; fi

# ---------- 5. API 冒烟测试 ----------
log "== 5. API 冒烟测试 =="

# 除登录/注册外，所有接口均需携带 token：先登录管理员（原雇主账号已并入管理员）
LOGIN=$(curl -s -X POST "$BASE/api/auth/login" -H 'Content-Type: application/json' \
  -d '{"username":"demo_employer","password":"123456"}')
TOKEN=$(echo "$LOGIN" | grep -o '"token":"[^"]*"' | head -1 | sed 's/"token":"//;s/"//')
if [ -n "$TOKEN" ]; then
  ok "管理员登录成功(123456, bcrypt校验通过)"
else
  bad "管理员登录失败: $(echo "$LOGIN" | head -c 120)"
  TOKEN="INVALID"
fi
AUTH="Authorization: Bearer $TOKEN"

J=$(curl -s -H "$AUTH" "$BASE/api/jobs?page=0&size=10")
echo "$J" | grep -q '"totalElements":16' && ok "岗位总数=16" || bad "岗位总数≠16: $(echo "$J" | head -c 120)"
echo "$J" | grep -q '"totalPages":2'    && ok "分页=2页"   || bad "分页≠2页"
echo "$J" | grep -q '"numberOfElements":10' && ok "第1页=10条" || bad "第1页≠10条"

J2=$(curl -s -H "$AUTH" "$BASE/api/jobs?page=1&size=10")
echo "$J2" | grep -q '"numberOfElements":6' && ok "第2页=6条" || bad "第2页≠6条: $(echo "$J2" | head -c 120)"

JQ=$(curl -s -H "$AUTH" "$BASE/api/jobs?page=0&size=10&q=%E5%AE%9E%E9%AA%8C%E5%AE%A4")
echo "$JQ" | grep -q '"totalElements":1' && ok "搜索[实验室]=1条" || bad "搜索失败: $(echo "$JQ" | head -c 120)"

SCH=$(curl -s -H "$AUTH" "$BASE/api/schedules")
SCH_N=$(echo "$SCH" | grep -o '"id":' | wc -l | tr -d ' ')
[ "$SCH_N" -ge 70 ] && ok "排班条数≥70（实际$SCH_N）" || bad "排班条数异常（$SCH_N）: $(echo "$SCH" | head -c 120)"

ST=$(curl -s -H "$AUTH" "$BASE/api/stats/overview")
echo "$ST" | grep -q '"totalJobs":16'   && ok "统计:岗位16"  || bad "统计:岗位≠16: $(echo "$ST" | head -c 120)"
echo "$ST" | grep -q '"totalStudents":50' && ok "统计:学生50" || bad "统计:学生≠50"
echo "$ST" | grep -q '"totalApplications":51' && ok "统计:申请51" || bad "统计:申请≠51"

# 管理员的岗位（可查全部岗位，含岗位发布）
MINE=$(curl -s -H "$AUTH" "$BASE/api/jobs/mine")
echo "$MINE" | grep -q '"办公室助理"' && ok "管理员可查看全部岗位" || bad "jobs/mine 异常: $(echo "$MINE" | head -c 120)"

# 岗位1的申请
BYJOB=$(curl -s -H "$AUTH" "$BASE/api/apply/job/1")
BYJOB_N=$(echo "$BYJOB" | grep -o '"studentName"' | wc -l | tr -d ' ')
[ "$BYJOB_N" -ge 2 ] && ok "岗位1申请列表=$BYJOB_N条" || bad "岗位1申请异常: $(echo "$BYJOB" | head -c 120)"

# 审核：取岗位1某条申请置为已录用
APP_ID=$(echo "$BYJOB" | grep -o '"id":[0-9]*' | head -1 | cut -d: -f2)
if [ -n "${APP_ID:-}" ]; then
  UPD=$(curl -s -X PUT -H "$AUTH" -H 'Content-Type: application/json' \
    -d '{"status":"ACCEPTED"}' "$BASE/api/apply/$APP_ID")
  echo "$UPD" | grep -q '"updated"' && ok "审核接口: 录用成功(id=$APP_ID)" || bad "审核接口异常: $UPD"
else
  bad "未能从岗位1申请中解析出 id"
fi

# 学生登录 + 我的申请 + 申请岗位
SLOGIN=$(curl -s -X POST "$BASE/api/auth/login" -H 'Content-Type: application/json' \
  -d '{"username":"student001","password":"123456"}')
STOKEN=$(echo "$SLOGIN" | grep -o '"token":"[^"]*"' | head -1 | sed 's/"token":"//;s/"//')
if [ -n "$STOKEN" ]; then
  ok "学生登录成功"
  MY=$(curl -s -H "Authorization: Bearer $STOKEN" "$BASE/api/apply/my")
  echo "$MY" | grep -q '"jobTitle"' && ok "学生可查我的申请" || bad "apply/my 异常: $(echo "$MY" | head -c 120)"
  AP=$(curl -s -X POST -H "Authorization: Bearer $STOKEN" "$BASE/api/apply/3")
  echo "$AP" | grep -q '"applied"' && ok "学生申请岗位3成功" || bad "apply 异常: $(echo "$AP" | head -c 120)"
else
  bad "学生登录失败: $(echo "$SLOGIN" | head -c 120)"
fi

# CORS 预检
CORS=$(curl -s -D - -o /dev/null -X OPTIONS "$BASE/api/jobs" \
  -H 'Origin: http://localhost:5173' -H 'Access-Control-Request-Method: GET')
echo "$CORS" | grep -qi 'access-control-allow-origin' && ok "CORS 预检通过" || bad "CORS 预检缺少 Allow-Origin 头"

# ---------- 6. 前端构建 ----------
log "== 6. 前端构建 =="
cd "$ROOT/frontend"
if [ ! -d node_modules ]; then
  log "首次运行，安装依赖（npm install --legacy-peer-deps）..."
  npm install --legacy-peer-deps >/dev/null 2>&1
fi
if npm run build >/dev/null 2>&1; then ok "前端 build 成功"; else bad "前端 build 失败"; fi

# ---------- 汇总 ----------
log ""
log "========== 验证汇总 =========="
log "PASS: $PASS   FAIL: $FAIL   SKIP: $SKIP"
if [ "$FAIL" = "0" ]; then
  log "🎉 全部通过，系统可运行。前端访问 http://localhost:5173（npm run dev）"
  exit 0
else
  log "存在失败项，请对照 VERIFY.md 排查。后端日志：$ROOT/backend.log"
  exit 1
fi
