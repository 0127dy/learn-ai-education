# 📚 AI 学情分析平台

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen)
![Java](https://img.shields.io/badge/Java-17-orange)
![Python](https://img.shields.io/badge/Python-3.10-blue)
![Flask](https://img.shields.io/badge/Flask-2.3-lightgrey)
![License](https://img.shields.io/badge/License-MIT-green)

> 面向教育培训机构的智能学情分析系统。提供学员管理、班级管理、成绩追踪等基础功能，并融合 AI 能力实现成绩预测、学员聚类分层、情感分析和个性化学习推荐。

---

## 📋 目录

- [系统架构](#系统架构)
- [技术栈](#技术栈)
- [功能模块](#功能模块)
- [AI 模型详解](#ai-模型详解)
- [快速启动](#快速启动)
- [API 文档](#api-文档)
- [项目结构](#项目结构)

---

## 🏗 系统架构

```
┌──────────────────────────────────────────────────────────────┐
│                    前端 Web 管理页面                           │
└──────────────────────┬───────────────────────────────────────┘
                       │ HTTP / RESTful API
┌──────────────────────▼───────────────────────────────────────┐
│              Spring Boot 后端（端口 8080）                     │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────────┐  │
│  │ 学员管理  │  │ 班级管理  │  │ 成绩管理  │  │ 反馈管理   │  │
│  └──────────┘  └──────────┘  └──────────┘  └────────────┘  │
│  ┌────────────┐  ┌──────────┐                                │
│  │ 分析报告    │  │ 学习推荐  │                                │
│  └────────────┘  └────┬─────┘                                │
└────────────────────────┼─────────────────────────────────────┘
                         │ WebFlux 异步调用
┌────────────────────────▼─────────────────────────────────────┐
│                Python Flask AI 服务（端口 5001）               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐   │
│  │ 成绩预测模型  │  │ 聚类分析模型  │  │  情感分析模型    │   │
│  │（线性回归）  │  │  (KMeans)    │  │（词典法）       │   │
│  └──────────────┘  └──────────────┘  └────────┬─────────┘   │
│  ┌────────────────────────────────────────────┘              │
│  │  ┌──────────────────┐                                     │
│  └─►│ 学习推荐模型      │                                     │
│      │（内容协同过滤）   │                                     │
│      └──────────────────┘                                     │
└──────────────────────────────────────────────────────────────┘
```

---

## 💻 技术栈

### 后端（Java）

| 技术 | 用途 |
|------|------|
| **Spring Boot 3.2** | 应用框架 |
| **MyBatis-Plus** | ORM 持久层 |
| **MySQL** | 关系型数据库 |
| **Spring WebFlux** | 异步调用 AI 微服务 |
| **Spring AOP** | 统一日志切面 |
| **SpringDoc OpenAPI** | 接口文档自动生成 |
| **HuTool** | 工具库 |
| **Maven** | 依赖管理 |
| **Docker** | 容器化部署 |

### AI 服务（Python）

| 技术 | 用途 |
|------|------|
| **Flask** | Web 框架 |
| **scikit-learn** | 机器学习算法 |
| **NumPy / pandas** | 数据处理 |
| **jieba** | 中文分词 |
| **Matplotlib** | 数据可视化 |

---

## ✨ 功能模块

### 👥 学员管理
- 学员信息 CRUD（增删改查）
- 学员列表分页查询
- 多条件筛选（姓名、班级、学历等）

### 🏫 班级管理
- 班级信息管理
- 班级人数统计

### 📊 成绩管理
- 学员各科成绩录入
- 成绩历史查询
- **AI 成绩趋势预测** — 基于历史成绩预测未来表现

### 💬 反馈管理
- 学员反馈提交与查询
- **AI 情感分析** — 自动分析学员反馈的情感倾向（积极/中性/消极）

### 📈 AI 分析报告
- **成绩预测报告** — 基于线性回归的未来成绩预测
- **学员聚类分析报告** — KMeans 聚类分组（优秀组/中等组/待提升组）
- **班级综合洞察报告** — 综合分析班级整体学情

### 🎯 个性化学习推荐
- 基于学员成绩与学习特征的内容推荐
- 推荐课程方向、学习方法、学习资料

---

## 🤖 AI 模型详解

### 1️⃣ 成绩预测模型

```
算法：线性回归 (Linear Regression)
输入：学员历史各科成绩序列
输出：未来成绩预测值 + 置信度 + 趋势判断（上升/下降/平稳）
```

### 2️⃣ 学员聚类模型

```
算法：KMeans 聚类
输入：学员特征（平均分、违纪次数、出勤率等）
输出：学员分群（优秀组🌟 / 中等组📘 / 待提升组📈）
```

### 3️⃣ 情感分析模型

```
算法：基于词典的情感分析（正向/负向词典匹配）
输入：学员反馈文本
输出：情感得分 (-1~1) + 情感分类（积极/中性/消极）
```

### 4️⃣ 学习推荐模型

```
算法：基于内容的推荐
输入：学员成绩特征
输出：个性化的课程推荐、学习方法和资料建议
```

---

## 🚀 快速启动

### 前置要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Python 3.9+

### 1️⃣ 克隆项目

```bash
git clone https://github.com/0127dy/learn-ai-education.git
cd learn-ai-education
```

### 2️⃣ 配置数据库

创建 MySQL 数据库 `tlias`：

```sql
CREATE DATABASE tlias DEFAULT CHARACTER SET utf8mb4;
```

### 3️⃣ 启动后端

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

后端运行在 `http://localhost:8080`

### 4️⃣ 启动 AI 服务

```bash
cd ai-service
pip install -r requirements.txt
python app.py
```

AI 服务运行在 `http://localhost:5001`

### 5️⃣ （可选）Docker 部署

```bash
docker-compose up --build
```

---

## 📖 API 文档

启动后端后访问：`http://localhost:8080/swagger-ui.html`

### 主要接口一览

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/performance/student/{id}` | 查询学员成绩 |
| POST | `/api/performance` | 添加成绩 |
| GET | `/api/performance/predict?studentId={id}` | AI 成绩预测 |
| POST | `/api/feedback` | 提交反馈 |
| GET | `/api/feedback/student/{id}` | 查询学员反馈 |
| GET | `/api/feedback/sentiment/class/{id}` | 班级情感分析 |
| GET | `/api/recommend/student/{id}` | 个性化学习推荐 |
| GET | `/report/ai/performance-prediction` | 成绩预测报告 |
| GET | `/report/ai/cluster-analysis?clazzId={id}` | 聚类分析报告 |
| GET | `/report/ai/class-insight?clazzId={id}` | 班级综合洞察 |

---

## 📁 项目结构

```
learn-ai-education/
├── backend/                     # Spring Boot 后端
│   ├── src/main/java/com/itheima/
│   │   ├── aspect/              # AOP 切面
│   │   ├── common/              # 通用类和查询参数
│   │   ├── config/              # 配置类
│   │   ├── controller/          # 控制器
│   │   ├── dto/                 # 数据传输对象
│   │   ├── entity/              # 实体类
│   │   ├── mapper/              # MyBatis Mapper
│   │   └── service/             # 业务逻辑层
│   ├── src/main/resources/
│   │   ├── static/              # 前端静态页面
│   │   └── application.yml      # 配置文件
│   └── pom.xml
├── ai-service/                  # Python AI 微服务
│   ├── models/
│   │   ├── performance_model.py # 成绩预测模型
│   │   ├── cluster_model.py     # 聚类分析模型
│   │   ├── sentiment_model.py   # 情感分析模型
│   │   └── recommend_model.py   # 学习推荐模型
│   ├── app.py                   # Flask 入口
│   └── requirements.txt         # Python 依赖
├── docs/                        # 文档
├── docker-compose.yml           # Docker 编排
└── README.md
```

---

## 📧 联系

项目作者：端木 · [GitHub](https://github.com/0127dy)
