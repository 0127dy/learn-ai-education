# 📋 API 接口文档

> AI 学情分析平台接口文档
> Base URL（开发环境）: `http://localhost:8080`

---

## 目录

- [1. 学员成绩管理](#1-学员成绩管理)
- [2. 学员反馈管理](#2-学员反馈管理)
- [3. AI 分析报告](#3-ai-分析报告)
- [4. 个性化推荐](#4-个性化推荐)
- [5. 原有数据统计（不变）](#5-原有数据统计不变)
- [6. AI 服务内部接口](#6-ai-服务内部接口)

---

## 1. 学员成绩管理

### 1.1 添加成绩

**URL:** `/api/performance`

**Method:** `POST`

**请求参数（JSON Body）：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| studentId | Integer | ✓ | 学员ID |
| courseName | String | ✓ | 课程名称 |
| score | Double | ✓ | 分数（0-100） |
| examDate | Datetime | ✗ | 考试日期 |

**请求示例：**

```json
{
    "studentId": 1,
    "courseName": "Spring Boot",
    "score": 88.5,
    "examDate": "2023-09-30T09:00:00"
}
```

**响应示例：**

```json
{
    "code": 1,
    "msg": "success",
    "data": null
}
```

### 1.2 查询学员成绩

**URL:** `/api/performance/student/{studentId}`

**Method:** `GET`

**路径参数：**

| 参数名 | 类型 | 说明 |
|--------|------|------|
| studentId | Integer | 学员ID |

**响应示例：**

```json
{
    "code": 1,
    "msg": "success",
    "data": [
        {
            "id": 1,
            "studentId": 1,
            "courseName": "Java基础",
            "score": 85.0,
            "examDate": "2023-05-10T09:00:00",
            "createTime": "2023-05-10T09:00:00"
        }
    ]
}
```

### 1.3 AI 成绩预测

**URL:** `/api/performance/predict?studentId=1`

**Method:** `GET`

**请求参数：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| studentId | Integer | ✓ | 学员ID |

**响应示例：**

```json
{
    "code": 1,
    "msg": "success",
    "data": {
        "student_id": 1,
        "prediction": {
            "predicted_score": 82.5,
            "confidence": 0.85,
            "trend": "上升 📈",
            "current_average": 80.6,
            "model_type": "linear_regression"
        }
    }
}
```

---

## 2. 学员反馈管理

### 2.1 提交反馈

**URL:** `/api/feedback`

**Method:** `POST`

**请求参数（JSON Body）：**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| studentId | Integer | ✓ | 学员ID |
| content | String | ✓ | 反馈内容 |
| sentimentScore | Double | ✗ | 情感得分（可由AI自动计算） |

**请求示例：**

```json
{
    "studentId": 1,
    "content": "课程内容很充实，老师讲解清晰"
}
```

**响应示例：**

```json
{
    "code": 1,
    "msg": "success",
    "data": null
}
```

### 2.2 查询学员反馈

**URL:** `/api/feedback/student/{studentId}`

**Method:** `GET`

**响应示例：**

```json
{
    "code": 1,
    "msg": "success",
    "data": [
        {
            "id": 1,
            "studentId": 1,
            "content": "课程内容很充实",
            "sentimentScore": 0.8,
            "createTime": "2023-10-01T10:00:00"
        }
    ]
}
```

### 2.3 班级情感分析

**URL:** `/api/feedback/sentiment/class/{clazzId}`

**Method:** `GET`

| 参数名 | 类型 | 说明 |
|--------|------|------|
| clazzId | Integer | 班级ID |

---

## 3. AI 分析报告

### 3.1 成绩预测报告

**URL:** `/report/ai/performance-prediction`

**Method:** `GET`

**说明：** 返回缓存的成绩预测分析报告，如无缓存可先调用 `/api/performance/predict` 生成数据。

### 3.2 学员聚类分析报告

**URL:** `/report/ai/cluster-analysis?clazzId=1`

**Method:** `GET`

**请求参数：**

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| clazzId | Integer | ✗ | 1 | 班级ID |

**响应示例（调用AI服务成功后）：**

```json
{
    "code": 1,
    "msg": "success",
    "data": {
        "clazz_id": 1,
        "total_students": 6,
        "n_clusters": 3,
        "clusters": {
            "group_0": {
                "name": "优秀组 🌟",
                "count": 2,
                "members": [{"id": 3, "name": "Lily"}, {"id": 2, "name": "李四"}]
            }
        }
    }
}
```

### 3.3 班级综合洞察

**URL:** `/report/ai/class-insight?clazzId=1`

**Method:** `GET`

---

## 4. 个性化推荐

### 4.1 获取学习推荐

**URL:** `/api/recommend/student/{studentId}`

**Method:** `GET`

**响应示例（AI服务开启时）：**

```json
{
    "code": 1,
    "msg": "success",
    "data": {
        "recommendations": [
            {
                "content": "建议深入学习 Spring Boot 微服务架构",
                "reason": "Spring Boot 是企业级开发核心框架",
                "type": "course"
            },
            {
                "content": "推荐使用番茄工作法进行学习",
                "reason": "番茄工作法能提升专注力",
                "type": "method"
            },
            {
                "content": "推荐阅读《Spring实战》第6版",
                "reason": "Spring 官方推荐读物",
                "type": "material"
            }
        ]
    }
}
```

**响应示例（AI服务未启动时）：**

```json
{
    "code": -1,
    "msg": "success",
    "data": {
        "message": "AI 服务调用失败: Connection refused"
    }
}
```

---

## 5. 原有数据统计（不变）

### 5.1 学员学历统计

**URL:** `/report/studentDegreeData`

**Method:** `GET`

### 5.2 班级人数统计

**URL:** `/report/studentCountData`

**Method:** `GET`

---

## 6. AI 服务内部接口

> 以下接口由 Python Flask 服务提供（端口 5001），Spring Boot 内部调用，不对外公开。

### 6.1 健康检查

**URL:** `http://localhost:5001/api/health`

**Method:** `GET`

### 6.2 成绩预测

**URL:** `http://localhost:5001/api/predict/performance`

**Method:** `POST`

### 6.3 聚类分析

**URL:** `http://localhost:5001/api/analyze/cluster`

**Method:** `POST`

### 6.4 情感分析

**URL:** `http://localhost:5001/api/analyze/sentiment`

**Method:** `POST`

### 6.5 学习推荐

**URL:** `http://localhost:5001/api/recommend/courses`

**Method:** `POST`

---

## 通用响应格式

```json
{
    "code": 1,        // 1=成功，0=业务错误，-1=系统异常
    "msg": "success",
    "data": {}        // 具体的返回数据
}
```
