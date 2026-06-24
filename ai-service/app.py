"""
AI 学情分析服务 - Flask 应用入口

提供以下 API：
- POST /api/predict/performance    — 成绩预测
- POST /api/analyze/cluster        — 聚类分析
- POST /api/analyze/sentiment      — 情感分析
- POST /api/recommend/courses      — 学习推荐
- GET  /api/health                 — 健康检查
"""

import os
import json
from flask import Flask, request, jsonify
from flask_cors import CORS
from models.performance_model import PerformancePredictor
from models.cluster_model import ClusterAnalyzer
from models.sentiment_model import SentimentAnalyzer
from models.recommend_model import RecommendModel

# 创建 Flask 应用
app = Flask(__name__)
CORS(app)  # 允许跨域

# 初始化模型实例（单例，全局复用）
performance_predictor = PerformancePredictor()
cluster_analyzer = ClusterAnalyzer(n_clusters=3)
sentiment_analyzer = SentimentAnalyzer()
recommend_model = RecommendModel()

# ======================== API 路由 ========================


@app.route('/api/health', methods=['GET'])
def health_check():
    """
    健康检查接口
    ---
    GET /api/health
    返回服务状态和模型加载情况
    """
    return jsonify({
        "status": "ok",
        "message": "AI 学情分析服务运行正常",
        "version": "1.0.0",
        "models": {
            "performance_predictor": "linear_regression",
            "cluster_analyzer": "kmeans",
            "sentiment_analyzer": "dictionary_based",
            "recommend_model": "content_based"
        }
    })


@app.route('/api/predict/performance', methods=['POST'])
def predict_performance():
    """
    成绩预测接口
    ---
    POST /api/predict/performance
    请求体：
    {
        "student_id": 1,
        "scores": [
            {"course": "Java基础", "score": 85.0, "exam_date": "2023-05-10"},
            ...
        ]
    }
    返回：
    {
        "code": 1,
        "data": { ... 预测结果 ... }
    }
    """
    try:
        data = request.get_json()
        if not data:
            return jsonify({"code": 0, "message": "请求体不能为空"})

        scores = data.get('scores', [])
        if not scores:
            return jsonify({"code": 0, "message": "未提供成绩数据"})

        # 执行预测
        result = performance_predictor.predict(scores)

        return jsonify({
            "code": 1,
            "data": {
                "student_id": data.get('student_id'),
                "prediction": result
            }
        })

    except Exception as e:
        return jsonify({
            "code": -1,
            "message": f"预测失败: {str(e)}"
        })


@app.route('/api/analyze/cluster', methods=['POST'])
def cluster_analysis():
    """
    学员聚类分析接口
    ---
    POST /api/analyze/cluster
    请求体：
    {
        "clazz_id": 1,
        "students": [
            {
                "student_id": 1,
                "name": "张三",
                "scores": [85, 78, ...],
                "average_score": 80.6,
                "violation_count": 0,
                "attendance_rate": 0.95
            },
            ...
        ]
    }
    """
    try:
        data = request.get_json()
        if not data:
            return jsonify({"code": 0, "message": "请求体不能为空"})

        students = data.get('students', [])
        clazz_id = data.get('clazz_id')

        # 如果没有提供学员数据，生成模拟数据用于演示
        if not students:
            students = _generate_demo_students(clazz_id or 1)

        # 执行聚类分析
        result = cluster_analyzer.analyze(students)

        return jsonify({
            "code": 1,
            "data": {
                "clazz_id": clazz_id or 1,
                **result
            }
        })

    except Exception as e:
        return jsonify({
            "code": -1,
            "message": f"聚类分析失败: {str(e)}"
        })


@app.route('/api/analyze/sentiment', methods=['POST'])
def analyze_sentiment():
    """
    情感分析接口
    ---
    POST /api/analyze/sentiment
    请求体：
    {
        "texts": ["课程内容很好", "太难了听不动", ...]
    }
    返回：
    {
        "code": 1,
        "data": { ... 分析结果 ... }
    }
    """
    try:
        data = request.get_json()
        if not data:
            return jsonify({"code": 0, "message": "请求体不能为空"})

        texts = data.get('texts', [])
        if not texts:
            return jsonify({"code": 0, "message": "未提供文本内容"})

        # 执行情感分析
        result = sentiment_analyzer.analyze_batch(texts)

        return jsonify({
            "code": 1,
            "data": result
        })

    except Exception as e:
        return jsonify({
            "code": -1,
            "message": f"情感分析失败: {str(e)}"
        })


@app.route('/api/recommend/courses', methods=['POST'])
def recommend_courses():
    """
    个性化学习推荐接口
    ---
    POST /api/recommend/courses
    请求体：
    {
        "student_id": 1,
        "scores": [
            {"course": "Java基础", "score": 85},
            ...
        ],
        "average_score": 80.6
    }
    """
    try:
        data = request.get_json()
        if not data:
            return jsonify({"code": 0, "message": "请求体不能为空"})

        scores = data.get('scores', [])
        avg_score = data.get('average_score')

        # 如果未提供平均分，自动计算
        if avg_score is None and scores:
            avg_score = sum(s.get('score', 0) for s in scores) / len(scores)

        student_data = {
            "student_id": data.get('student_id'),
            "scores": scores,
            "average_score": avg_score or 0
        }

        # 执行推荐
        result = recommend_model.recommend(student_data)

        return jsonify({
            "code": 1,
            "data": result
        })

    except Exception as e:
        return jsonify({
            "code": -1,
            "message": f"推荐生成失败: {str(e)}"
        })


# ======================== 辅助函数 ========================


def _generate_demo_students(clazz_id):
    """
    生成演示用的学员数据
    当 Spring Boot 未提供学员详细信息时使用
    """
    import numpy as np

    # 基于班级ID生成不同分布的学员
    np.random.seed(clazz_id)

    students = []
    names = ["张三", "李四", "Lily", "王五", "赵六", "孙七",
             "周八", "吴九", "郑十", "陈晓", "林琳", "黄伟"]

    for i, name in enumerate(names[:6]):  # 每个班级6名学员
        base_score = 60 + np.random.rand() * 30  # 60-90基础分
        n_scores = 5  # 5门课程
        scores = [base_score + np.random.randn() * 8 for _ in range(n_scores)]
        scores = [max(30, min(100, s)) for s in scores]

        students.append({
            "student_id": i + 1,
            "name": name,
            "scores": [round(s, 1) for s in scores],
            "average_score": round(np.mean(scores), 1),
            "violation_count": int(np.random.choice([0, 0, 0, 0, 1, 2])),
            "attendance_rate": round(0.8 + np.random.rand() * 0.2, 2)
        })

    return students


# ======================== 主入口 ========================

if __name__ == '__main__':
    port = int(os.environ.get('PORT', 5001))
    debug = os.environ.get('DEBUG', 'false').lower() == 'true'
    print(f"🚀 AI 学情分析服务启动中... 端口: {port}")
    print(f"   API 文档:")
    print(f"   - GET  /api/health                健康检查")
    print(f"   - POST /api/predict/performance   成绩预测")
    print(f"   - POST /api/analyze/cluster       聚类分析")
    print(f"   - POST /api/analyze/sentiment     情感分析")
    print(f"   - POST /api/recommend/courses     学习推荐")
    print(f"\n📡 监听地址: http://0.0.0.0:{port}")
    app.run(host='0.0.0.0', port=port, debug=debug)
