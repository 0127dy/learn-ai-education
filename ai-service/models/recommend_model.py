"""
推荐模型（基于相似度的协同过滤）
根据学员成绩和历史行为生成个性化学习推荐
"""

import math
from collections import defaultdict


class RecommendModel:
    """
    基于内容的个性化推荐器

    根据学员成绩、学习进度等特征，生成个性化学习建议
    包括课程推荐、学习方法推荐和学习资料推荐
    """

    # 预定义的推荐内容库
    RECOMMENDATION_LIBRARY = {
        "course": [
            {
                "content": "建议深入学习 Spring Boot 微服务架构",
                "reason": "Spring Boot 是企业级开发核心框架，掌握微服务架构能显著提升就业竞争力",
                "type": "course",
                "min_score": 80,
                "target_course": "Spring Boot"
            },
            {
                "content": "建议复习 Java 基础核心知识",
                "reason": "Java 基础是后续学习的根基，扎实的基础能让你更快掌握高级框架",
                "type": "course",
                "min_score": 0,
                "max_score": 70,
                "target_course": "Java基础"
            },
            {
                "content": "建议学习 Redis 缓存技术",
                "reason": "掌握 Redis 可以大幅提升系统性能，是大厂面试高频考点",
                "type": "course",
                "min_score": 75,
                "target_course": "SSM框架"
            },
            {
                "content": "建议加强 MySQL 数据库优化训练",
                "reason": "SQL 优化和索引设计是后端开发必备技能",
                "type": "course",
                "min_score": 0,
                "max_score": 75,
                "target_course": "MySQL"
            },
            {
                "content": "建议探索微服务与容器化部署（Docker + K8s）",
                "reason": "容器化是现代 DevOps 的核心，掌握后能大幅提升部署效率",
                "type": "course",
                "min_score": 85,
                "target_course": "Spring Boot"
            }
        ],
        "method": [
            {
                "content": "推荐使用番茄工作法进行学习",
                "reason": "番茄工作法能提升专注力，每25分钟集中学习+5分钟休息，适合长时间编程学习",
                "type": "method"
            },
            {
                "content": "推荐使用费曼学习法巩固知识",
                "reason": "把自己学到的知识讲给别人听，能发现知识盲区，加深理解",
                "type": "method"
            },
            {
                "content": "建议增加代码练习量，每天至少手写100行代码",
                "reason": "编程是实践性很强的技能，多写代码是提升最快的方式",
                "type": "method",
                "max_score": 75
            },
            {
                "content": "推荐建立个人技术博客，记录学习笔记",
                "reason": "输出倒逼输入，写博客能系统化你的知识体系",
                "type": "method"
            },
            {
                "content": "建议参与开源项目或做个人项目",
                "reason": "项目经验是面试中最有说服力的部分，动手做项目能综合运用所学知识",
                "type": "method",
                "min_score": 80
            }
        ],
        "material": [
            {
                "content": "推荐阅读《Java核心技术》卷I",
                "reason": "Java 领域经典著作，内容全面且深入",
                "type": "material",
                "max_score": 70
            },
            {
                "content": "推荐阅读《Spring实战》第6版",
                "reason": "Spring 官方推荐读物，配合实战案例学习效果最佳",
                "type": "material",
                "min_score": 70
            },
            {
                "content": "推荐观看 B站『尚硅谷』Spring Boot 教程",
                "reason": "视频教程配合实操，适合视觉型学习者",
                "type": "material"
            },
            {
                "content": "推荐 LeetCode 刷题（每日一题）",
                "reason": "算法题是面试必考环节，持续刷题能锻炼编程思维",
                "type": "material"
            },
            {
                "content": "推荐阅读《深入理解Java虚拟机》",
                "reason": "JVM 调优是高级开发必备技能，面试高频考点",
                "type": "material",
                "min_score": 85
            }
        ]
    }

    def recommend(self, student_data):
        """
        根据学员数据生成个性化推荐

        student_data: {
            "student_id": 1,
            "scores": [{"course": "Java基础", "score": 85}, ...],
            "average_score": 80.6,
            "weak_courses": ["JavaWeb"],
            "strength_courses": ["Spring Boot"]
        }

        返回: dict 包含推荐列表
        """
        scores = student_data.get('scores', [])
        avg_score = student_data.get('average_score', self._calc_avg(scores))
        weak_courses = self._find_weak_courses(scores)
        strength_courses = self._find_strength_courses(scores)

        recommendations = []
        categories = {'course': [], 'method': [], 'material': []}

        # 逐类生成推荐
        for rec_type, rec_list in self.RECOMMENDATION_LIBRARY.items():
            for rec in rec_list:
                # 检查分数条件
                min_score = rec.get('min_score', 0)
                max_score = rec.get('max_score', 100)
                if min_score <= avg_score <= max_score:
                    # 检查课程针对性
                    target = rec.get('target_course', '')
                    if target:
                        # 如果推荐针对特定课程，检查是否匹配学员情况
                        if target in weak_courses:
                            categories[rec_type].append(rec)
                        elif target in strength_courses and avg_score >= 80:
                            categories[rec_type].append(rec)
                        elif not weak_courses and not strength_courses:
                            categories[rec_type].append(rec)
                    else:
                        categories[rec_type].append(rec)

        # 从每类中最多选2条
        for rec_type in ['course', 'method', 'material']:
            selected = categories[rec_type][:2]
            for rec in selected:
                rec_copy = dict(rec)
                recommendations.append({
                    "content": rec_copy['content'],
                    "reason": rec_copy['reason'],
                    "type": rec_copy['type']
                })

        # 如果推荐太少，补充通用推荐
        if len(recommendations) < 3:
            fallback = [
                {"content": "保持良好学习状态，持续进步！", "reason": "稳定的学习节奏是最好的学习策略", "type": "method"},
                {"content": "推荐多做项目练习，积累实战经验", "reason": "理论结合实践才能掌握真本领", "type": "course"},
                {"content": "多看优秀开源项目源码，学习设计思想", "reason": "阅读优秀代码是提升编程能力的高效途径", "type": "material"},
            ]
            for rec in fallback:
                if len(recommendations) < 5:
                    recommendations.append(rec)

        return {
            "student_id": student_data.get('student_id'),
            "average_score": round(avg_score, 1),
            "weak_courses": weak_courses,
            "strength_courses": strength_courses,
            "recommendations": recommendations,
            "total_recommendations": len(recommendations)
        }

    @staticmethod
    def _calc_avg(scores):
        """计算平均分"""
        if not scores:
            return 0
        return sum(s.get('score', 0) for s in scores) / len(scores)

    @staticmethod
    def _find_weak_courses(scores):
        """找出薄弱课程（低于平均分）"""
        if not scores:
            return []
        avg = sum(s.get('score', 0) for s in scores) / len(scores)
        return [s.get('course', '') for s in scores if s.get('score', 0) < avg]

    @staticmethod
    def _find_strength_courses(scores):
        """找出优势课程（高于平均分）"""
        if not scores:
            return []
        avg = sum(s.get('score', 0) for s in scores) / len(scores)
        return [s.get('course', '') for s in scores if s.get('score', 0) > avg]
