# 🚀 部署指南

> AI 学情分析平台部署文档

---

## 1. 环境要求

| 依赖 | 版本要求 | 说明 |
|------|----------|------|
| JDK | ≥ 17 | Spring Boot 3.2 需要 |
| Maven | ≥ 3.8 | 构建 Java 项目 |
| MySQL | ≥ 8.0 | 数据库 |
| Python | ≥ 3.9 | AI 服务 |
| Docker | ≥ 24.0 | 容器化部署（可选） |
| Docker Compose | ≥ 2.20 | 编排服务（可选） |

---

## 2. 数据库部署

### 2.1 手动部署 MySQL

```bash
# 使用 Docker 启动 MySQL
docker run -d \
  --name learn-mysql \
  -e MYSQL_ROOT_PASSWORD=123456 \
  -e MYSQL_DATABASE=student \
  -p 3307:3306 \
  -v mysql_data:/var/lib/mysql \
  mysql:8.0 \
  --character-set-server=utf8mb4 \
  --collation-server=utf8mb4_unicode_ci
```

### 2.2 初始化数据库

```bash
# 导入数据库表结构
docker cp backend/db/init.sql learn-mysql:/init.sql
docker exec -i learn-mysql mysql -uroot -p123456 student < backend/db/init.sql
```

---

## 3. 启动 Python AI 服务

### 3.1 安装依赖

```bash
cd ai-service
pip install -r requirements.txt
```

### 3.2 启动服务

```bash
# 开发模式
python app.py

# 生产模式（gunicorn）
pip install gunicorn
gunicorn --bind 0.0.0.0:5001 --workers 2 --timeout 120 app:app
```

### 3.3 验证服务

```bash
# 健康检查
curl http://localhost:5001/api/health

# 预期输出：
# {"status":"ok","message":"AI 学情分析服务运行正常","version":"1.0.0",...}
```

---

## 4. 启动 Spring Boot 后端

### 4.1 配置数据库

确保 `backend/src/main/resources/application.yml` 中的数据库配置正确：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/student?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
```

### 4.2 构建并启动

```bash
cd backend

# 清理构建
mvn clean package -DskipTests

# 启动
java -jar target/tlias-backend-1.0.0.jar
```

### 4.3 验证后端

```bash
# 健康检查
curl http://localhost:8080/health

# 测试成绩 API
curl http://localhost:8080/api/performance/student/1
```

---

## 5. Docker Compose 一键启动（推荐）

在项目根目录执行：

```bash
cd backend
docker-compose up -d --build
```

这会自动启动三个服务：

| 服务名 | 容器名 | 端口 | 说明 |
|--------|--------|------|------|
| mysql | learn-mysql | 3307 | 数据库 |
| app | learn-app | 8080 | Spring Boot 后端 |
| ai-service | learn-ai | 5001 | Python AI 服务 |

### 验证所有服务

```bash
# 1. 检查所有容器是否运行
docker ps

# 2. 验证数据库
docker exec learn-mysql mysql -uroot -p123456 -e "SHOW TABLES FROM student;"

# 3. 验证后端
curl http://localhost:8080/report/studentCountData

# 4. 验证AI服务
curl http://localhost:5001/api/health

# 5. 查看日志
docker-compose logs -f app
docker-compose logs -f ai-service
```

---

## 6. 一键启动脚本

创建 `start.sh`（Linux/Mac）或 `start.ps1`（Windows）：

### Windows (start.ps1)

```powershell
# 启动 MySQL
docker start learn-mysql 2>$null
if ($LASTEXITCODE -ne 0) {
    docker run -d --name learn-mysql -e MYSQL_ROOT_PASSWORD=123456 -e MYSQL_DATABASE=student -p 3307:3306 mysql:8.0
}

# 启动 AI 服务
$aiJob = Start-Job -ScriptBlock {
    Set-Location ai-service
    python app.py
}

# 启动 Spring Boot
Start-Process -WindowStyle Hidden -FilePath "java" -ArgumentList "-jar backend/target/tlias-backend-1.0.0.jar"

Write-Host "✅ 所有服务已启动！"
Write-Host "   📊 前端页面: http://localhost:8080/frontend/index.html"
Write-Host "   🤖 AI 仪表盘: http://localhost:8080/frontend/ai-dashboard.html"
Write-Host "   🔬 AI 服务: http://localhost:5001/api/health"
```

---

## 7. 访问方式

| 页面 | 地址 | 说明 |
|------|------|------|
| 学员管理系统 | `http://localhost:8080/frontend/index.html` | 基础的CRUD管理 |
| AI 学情分析 | `http://localhost:8080/frontend/ai-dashboard.html` | AI 增强功能入口 |
| 后端 API | `http://localhost:8080/api/**` | RESTful 接口 |
| AI 服务 API | `http://localhost:5001/api/**` | Python AI 服务 |

---

## 8. 常见问题

### Q: AI 服务连接失败怎么办？

1. 检查 AI 服务是否启动: `curl http://localhost:5001/api/health`
2. 检查 `application.yml` 中 `ai-service.base-url` 配置
3. Docker 环境下使用 `http://ai-service:5001`

### Q: 数据库连接失败？

1. 检查 MySQL 是否启动: `docker ps | grep mysql`
2. 检查端口映射: MySQL 外部端口 3307
3. 执行初始化: `docker exec -i learn-mysql mysql -uroot -p123456 student < backend/db/init.sql`

### Q: 端口被占用？

修改 `application.yml` 中的 `server.port` 或 Docker 的端口映射配置。
