"""
成绩预测模型（线性回归）
根据学员历史成绩预测期末表现
"""

import numpy as np
from sklearn.linear_model import LinearRegression


class PerformancePredictor:
    """
    基于线性回归的成绩预测器

    输入：学员各科历史成绩（按时间排序）
    输出：预测的期末成绩和置信度
    """

    def __init__(self):
        self.model = LinearRegression()
        self._trained = False

    def _prepare_features(self, scores_data):
        """
        将成绩数据转为特征矩阵

        scores_data: [{'course': 'Java基础', 'score': 85.0, 'exam_date': '2023-05-10'}, ...]

        返回: X (特征矩阵), y (目标值)
        """
        if not scores_data:
            return None, None

        scores = [s.get('score', 0) for s in scores_data]
        n = len(scores)

        # 特征工程：课程序号（时间顺序）、前序课程平均分
        X = []
        y = []
        for i in range(1, n):
            # 特征1: 课程序号（表示学习进度）
            # 特征2: 到当前为止的平均分
            # 特征3: 最近一次成绩
            avg_prev = np.mean(scores[:i])
            last_score = scores[i - 1]
            X.append([i, avg_prev, last_score])
            y.append(scores[i])

        if len(X) < 2:
            return None, None

        return np.array(X), np.array(y)

    def predict(self, scores_data):
        """
        预测学员成绩趋势

        返回: dict 包含预测分数和置信度
        """
        X, y = self._prepare_features(scores_data)

        if X is None or len(X) < 2:
            # 数据不足时返回简单的趋势分析
            return self._simple_trend_analysis(scores_data)

        # 训练模型
        self.model.fit(X, y)
        self._trained = True

        # 预测下一门课程的成绩
        n = len(scores_data)
        avg_all = np.mean([s.get('score', 0) for s in scores_data])
        last_score = scores_data[-1].get('score', 0)
        next_features = np.array([[n, avg_all, last_score]]).reshape(1, -1)
        predicted_score = self.model.predict(next_features)[0]
        predicted_score = max(0, min(100, predicted_score))  # 限制在0-100

        # 计算R²作为置信度
        r2_score = self.model.score(X, y)

        # 计算趋势（上升/下降/波动）
        trend = self._analyze_trend([s.get('score', 0) for s in scores_data])

        return {
            "predicted_score": round(predicted_score, 1),
            "confidence": round(r2_score, 3),
            "trend": trend,
            "total_courses": n,
            "current_average": round(avg_all, 1),
            "model_type": "linear_regression"
        }

    def _simple_trend_analysis(self, scores_data):
        """数据不足时的简单趋势分析"""
        scores = [s.get('score', 0) for s in scores_data]
        if not scores:
            return {
                "predicted_score": None,
                "confidence": 0,
                "trend": "unknown",
                "total_courses": 0,
                "current_average": 0,
                "model_type": "insufficient_data",
                "message": "成绩数据不足，请至少提供2门课程成绩"
            }

        avg = np.mean(scores)
        return {
            "predicted_score": round(avg, 1),
            "confidence": 0.3,
            "trend": self._analyze_trend(scores),
            "total_courses": len(scores),
            "current_average": round(avg, 1),
            "model_type": "simple_average",
            "message": "数据有限，使用简单平均估算"
        }

    @staticmethod
    def _analyze_trend(scores):
        """分析成绩趋势"""
        if len(scores) < 2:
            return "stable"

        recent = scores[-3:] if len(scores) >= 3 else scores
        if len(recent) < 2:
            return "stable"

        # 简单比较最后两个数据点
        if recent[-1] > recent[-2] + 2:
            return "上升 📈"
        elif recent[-1] < recent[-2] - 2:
            return "下降 📉"
        else:
            return "平稳 ➡️"
