"""
学员聚类分析模型（KMeans）
根据学员特征进行分组，发现不同学习群体
"""

import numpy as np
from sklearn.cluster import KMeans
from sklearn.preprocessing import StandardScaler


class ClusterAnalyzer:
    """
    基于 KMeans 的学员聚类分析器

    输入：学员特征数据（成绩、出勤、违纪等）
    输出：聚类分组结果
    """

    def __init__(self, n_clusters=3):
        self.n_clusters = n_clusters
        self.model = KMeans(n_clusters=n_clusters, random_state=42, n_init='auto')
        self.scaler = StandardScaler()
        self._labels = None
        self._centers = None

    def analyze(self, students_data):
        """
        对学员数据进行聚类分析

        students_data: [
            {
                "student_id": 1,
                "name": "张三",
                "scores": [85, 78, 72, 80, 88],  # 各科成绩
                "average_score": 80.6,
                "violation_count": 0,
                "attendance_rate": 0.95
            },
            ...
        ]

        返回: dict 包含聚类结果
        """
        if not students_data or len(students_data) < self.n_clusters:
            return self._simple_grouping(students_data)

        # 提取特征矩阵
        features = []
        student_info = []
        for s in students_data:
            scores = s.get('scores', [])
            avg_score = s.get('average_score', np.mean(scores) if scores else 0)
            violation = s.get('violation_count', 0)
            attendance = s.get('attendance_rate', 0.85)

            # 特征向量: [平均分, 最高分-最低分(波动), 违纪次数, 出勤率]
            score_std = np.std(scores) if len(scores) > 1 else 0
            features.append([avg_score, score_std, violation, attendance])
            student_info.append({
                "id": s.get('student_id'),
                "name": s.get('name', f"学员{s.get('student_id')}")
            })

        features = np.array(features)

        # 标准化
        features_scaled = self.scaler.fit_transform(features)

        # KMeans 聚类
        self.model.fit(features_scaled)
        self._labels = self.model.labels_
        self._centers = self.model.cluster_centers_

        # 整理结果
        cluster_names = {
            0: "优秀组 🌟",
            1: "中等组 📚",
            2: "提升组 💪"
        }

        clusters = {}
        for i in range(self.n_clusters):
            cluster_label = f"group_{i}"
            members_idx = np.where(self._labels == i)[0]
            members = [student_info[idx] for idx in members_idx]

            # 计算该组的平均特征
            group_features = features[members_idx]
            group_avg = np.mean(group_features, axis=0) if len(group_features) > 0 else [0, 0, 0, 0]

            clusters[cluster_label] = {
                "name": cluster_names.get(i, f"第{i+1}组"),
                "count": int(len(members)),
                "avg_score": round(float(group_avg[0]), 1),
                "avg_violation": round(float(group_avg[2]), 1),
                "avg_attendance": round(float(group_avg[3]), 2),
                "members": members,
                "characteristics": self._describe_group(group_avg)
            }

        # 排序：按平均分从高到低
        sorted_groups = sorted(clusters.items(), key=lambda x: x[1]['avg_score'], reverse=True)
        sorted_clusters = {}
        for i, (key, value) in enumerate(sorted_groups):
            new_key = f"group_{i}"
            value['rank'] = i + 1
            sorted_clusters[new_key] = value

        return {
            "total_students": len(students_data),
            "n_clusters": self.n_clusters,
            "clusters": sorted_clusters,
            "algorithm": "KMeans",
            "features_used": ["平均分", "成绩波动", "违纪次数", "出勤率"]
        }

    def _simple_grouping(self, students_data):
        """学员数量不足时的简单分组"""
        if not students_data:
            return {
                "total_students": 0,
                "n_clusters": 0,
                "clusters": {},
                "message": "无学员数据可供分析"
            }

        # 按平均分简单分组
        groups = {"优秀": [], "中等": [], "需提升": []}
        for s in students_data:
            scores = s.get('scores', [])
            avg = s.get('average_score', np.mean(scores) if scores else 0)
            name = s.get('name', f"学员{s.get('student_id')}")

            item = {"id": s.get('student_id'), "name": name, "avg_score": round(avg, 1)}
            if avg >= 85:
                groups["优秀"].append(item)
            elif avg >= 70:
                groups["中等"].append(item)
            else:
                groups["需提升"].append(item)

        return {
            "total_students": len(students_data),
            "n_clusters": 3,
            "clusters": {
                f"group_{i}": {
                    "name": name,
                    "count": len(members),
                    "members": members
                }
                for i, (name, members) in enumerate(groups.items())
            },
            "algorithm": "simple_grouping",
            "note": "学员数量较少，使用简单分组代替 KMeans 聚类"
        }

    @staticmethod
    def _describe_group(features):
        """根据特征向量描述一个群体"""
        avg_score, score_std, violation, attendance = features
        desc = []
        if avg_score >= 85:
            desc.append("成绩优秀")
        elif avg_score >= 70:
            desc.append("成绩中等")
        else:
            desc.append("成绩需提升")

        if score_std > 15:
            desc.append("成绩波动大")
        else:
            desc.append("成绩稳定")

        if violation > 1:
            desc.append("违纪较多")
        else:
            desc.append("纪律良好")

        if attendance < 0.9:
            desc.append("出勤率偏低")
        else:
            desc.append("出勤良好")

        return "，".join(desc)
