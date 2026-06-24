# Tlias 学生管理系统 - 课程设计

> 基于 Spring Boot + MyBatis-Plus 的学生管理系统
> 支持 Docker Desktop / Docker Compose 一键部署

---

## 📋 项目功能

| 模块 | API | 说明 |
|------|-----|------|
| 首页 | `GET /` | 返回 `Hello K8S！`，验证部署 |
| 健康检查 | `GET /health` | 返回 `UP`，用于存活探针 |
| 班级管理 | `GET/POST/PUT/DELETE /clazzs/**` | 班级 CRUD + 分页查询 |
| 学员管理 | `GET/POST/PUT/DELETE /students/**` | 学员 CRUD + 违纪处理 |
| 数据统计 | `GET /report/studentDegreeData` | 学员学历分布 |
| | `GET /report/studentCountData` | 各班级人数 |

---

## 🚀 快速运行（Docker Desktop 一键启动）

### 前置条件
- ✅ **Docker Desktop**（已安装）
- 无需本地安装 JDK 17！多阶段 Docker 构建自动处理

### 运行步骤

```bash
# 1️⃣ 进入项目目录
cd D:\.openclaw\workspace\tlias-backend

# 2️⃣ 一键构建+启动
docker compose up --build -d
```

> **注意**：第一次运行需要下载 Maven、JDK 17、MySQL 镜像，耗时 2-5 分钟

### 验证是否成功

```bash
# 查看容器状态
docker ps

# 查看日志
docker compose logs -f app

# 访问首页
curl http://localhost:8080/
```

浏览器打开 `http://localhost:8080/`，看到 `Hello K8S！` 即部署成功 ✅

### 测试 API

```bash
# 查询所有班级
curl http://localhost:8080/clazzs/list

# 查询所有学员
curl http://localhost:8080/students?page=1&pageSize=10

# 按名称搜索学员
curl "http://localhost:8080/students?name=张三&page=1&pageSize=10"

# 查看统计报表
curl http://localhost:8080/report/studentDegreeData
```

### 停止与清理

```bash
# 停止但不删除数据
docker compose stop

# 完全停止并删除
docker compose down -v
```

---

## 🛠️ 本地开发（需要 JDK 17）

```bash
# 1. 修改 application.yml 数据库连接为 localhost:3306
# 2. 先在本地启动 MySQL
# 3. 运行 maven 打包
mvn clean package -DskipTests

# 4. 运行
java -jar target/tlias-backend-1.0.0.jar
```

---

## 🌐 API 接口一览

### 班级管理 `/clazzs`

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/clazzs` | 分页+条件查询（name, begin, end, page, pageSize） |
| `GET` | `/clazzs/list` | 所有班级 |
| `GET` | `/clazzs/{id}` | 按 ID 查询 |
| `POST` | `/clazzs` | 添加班级 `{"name":"Java班","room":"101","beginDate":"2024-01-01","endDate":"2024-06-30","masterId":1,"subject":1}` |
| `PUT` | `/clazzs` | 修改班级 |
| `DELETE` | `/clazzs/{id}` | 删除班级 |

### 学员管理 `/students`

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/students` | 分页+条件查询（name, degree, clazzId, page, pageSize） |
| `GET` | `/students/{id}` | 按 ID 查询 |
| `POST` | `/students` | 添加学员 |
| `PUT` | `/students` | 修改学员 |
| `DELETE` | `/students/{ids}` | 批量删除，逗号分隔 |
| `PUT` | `/students/violation/{id}/{score}` | 违纪处理 |

### 数据统计 `/report`

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/report/studentDegreeData` | 学历分布 |
| `GET` | `/report/studentCountData` | 班级人数统计 |

---

## 📁 项目结构

```
tlias-backend/
├── Dockerfile                 # 多阶段构建（无需本地JDK）
├── docker-compose.yml         # MySQL + App 一键启动
├── pom.xml                    # Maven 配置
├── .gitignore
├── README.md
├── db/
│   └── init.sql               # 数据库初始化脚本
├── deploy/                    # K8S 部署文件（扩展用）
│   ├── 00-namespace.yaml
│   ├── 01-mysql.yaml
│   └── 02-app.yaml
└── src/main/java/com/itheima/
    ├── TliasApplication.java
    ├── common/                 # Result, PageResult, 查询参数
    ├── config/                 # CORS, MyBatisPlus, WebMvc
    ├── entity/                 # Clazz, Student
    ├── mapper/                 # MyBatis-Plus Mapper
    ├── service/                # 业务逻辑
    └── controller/             # REST API
```

---

## ⚙️ 技术栈

| 技术 | 版本 |
|------|------|
| Java | 17 |
| Spring Boot | 3.2.0 |
| MyBatis-Plus | 3.5.5 |
| MySQL | 8.0 |
| Docker | 24+ |
