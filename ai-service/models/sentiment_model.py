"""
情感分析模型（基于词典的简单情感分析 + TextBlob 辅助）
用于分析学员反馈文本的情感倾向
"""

import re
import math


class SentimentAnalyzer:
    """
    基于情感词典的情感分析器

    支持中文（基于 jieba 分词 + 情感词典）
    返回情感得分：-1.0（消极） ~ 1.0（积极）
    """

    # 积极情感词典
    POSITIVE_WORDS = set([
        '好', '优秀', '棒', '赞', '满意', '喜欢', '开心', '高兴', '厉害',
        '清晰', '充实', '精彩', '有趣', '有帮助', '实用', '丰富', '耐心',
        '认真', '负责', '专业', '细致', '易懂', '推荐', '进步', '提高',
        '有效', '不错', '很好', '非常好', '太好了', '点赞', '好评',
        '感谢', '谢谢', '牛', '强', '666', 'nice', 'great', 'excellent',
        'good', 'awesome', 'wonderful', '满意', '轻松', '愉快', '舒适'
    ])

    # 消极情感词典
    NEGATIVE_WORDS = set([
        '差', '烂', '糟糕', '失望', '不满意', '不喜欢', '无聊', '乏味',
        '混乱', '不清楚', '太难', '听不懂', '跟不上', '慢', '拖沓',
        '没用', '浪费', '垃圾', '敷衍', '不负责', '差评', '不好',
        '很差', '太差', '不行', '有问题', '错误', 'bug', '崩溃',
        '困难', '复杂', '累', '烦', '生气', '难过', '痛苦', '糟糕',
        'bad', 'terrible', 'awful', 'poor', 'horrible', 'worst'
    ])

    # 程度副词（加强情感强度）
    INTENSIFIERS = {
        '非常': 1.5, '很': 1.3, '十分': 1.5, '特别': 1.4, '极其': 1.6,
        '太': 1.4, '相当': 1.3, '有点': 0.7, '有些': 0.7, '稍微': 0.6,
        '比较': 0.8, '还算': 0.8, '超级': 1.6, '绝对': 1.5, '完全': 1.3,
        '真的': 1.2, '实在': 1.3, 'really': 1.3, 'very': 1.4, 'so': 1.2,
        'too': 1.2, 'extremely': 1.6, 'quite': 1.2
    }

    # 否定词（反转情感）
    NEGATORS = set([
        '不', '没', '无', '未', '别', '不要', '不是', '没有', '并非',
        '从不', '绝不', '毫不', 'not', 'no', 'never', "n't", 'don', "don't"
    ])

    def analyze(self, text):
        """
        分析一段文本的情感

        返回: dict 包含情感得分、极性、关键词等
        """
        if not text or not text.strip():
            return {
                "score": 0,
                "polarity": "neutral",
                "details": "空文本"
            }

        text_lower = text.lower()

        # 分词（简单按空格和标点分割，对英文友好）
        # 对中文使用字符级 + 词级混合分析
        words = self._tokenize(text_lower)

        # 情感分析
        positive_score = 0
        negative_score = 0
        intensifier = 1.0
        negated = False

        matched_positives = []
        matched_negatives = []
        matched_intensifiers = []

        for word in words:
            # 检查程度副词
            if word in self.INTENSIFIERS:
                intensifier = self.INTENSIFIERS[word]
                matched_intensifiers.append(word)
                continue

            # 检查否定词
            if word in self.NEGATORS:
                negated = not negated
                continue

            # 检查积极词
            if word in self.POSITIVE_WORDS:
                score = 1.0 * intensifier
                if negated:
                    score = -score * 0.5
                positive_score += score
                matched_positives.append(word)
                intensifier = 1.0  # 重置
                negated = False
                continue

            # 检查消极词
            if word in self.NEGATIVE_WORDS:
                score = 1.0 * intensifier
                if negated:
                    score = -score * 0.5
                negative_score += score
                matched_negatives.append(word)
                intensifier = 1.0
                negated = False
                continue

            # 重置程度副词（只影响下一个情感词）
            if word not in self.INTENSIFIERS:
                intensifier = 1.0

        # 计算总分
        total_score = positive_score - negative_score

        # 归一化到 -1 到 1
        max_possible = len(words) * 1.6  # 最大可能得分
        if max_possible > 0:
            normalized_score = max(-1.0, min(1.0, total_score / max_possible * 3))
        else:
            normalized_score = 0

        # 确定极性
        if normalized_score > 0.2:
            polarity = "positive"
        elif normalized_score < -0.2:
            polarity = "negative"
        else:
            polarity = "neutral"

        return {
            "score": round(normalized_score, 3),
            "polarity": polarity,
            "positive_words": matched_positives,
            "negative_words": matched_negatives,
            "details": {
                "total_words": len(words),
                "positive_hits": len(matched_positives),
                "negative_hits": len(matched_negatives),
                "intensifiers": matched_intensifiers
            }
        }

    def analyze_batch(self, texts):
        """
        批量分析多段文本的情感

        texts: list[str]

        返回: dict 包含每条的分析结果和整体统计
        """
        if not texts:
            return {"items": [], "statistics": {"total": 0}}

        results = []
        for i, text in enumerate(texts):
            result = self.analyze(text)
            result['index'] = i
            results.append(result)

        # 统计
        scores = [r['score'] for r in results]
        positive_count = sum(1 for r in results if r['polarity'] == 'positive')
        negative_count = sum(1 for r in results if r['polarity'] == 'negative')
        neutral_count = sum(1 for r in results if r['polarity'] == 'neutral')

        return {
            "items": results,
            "statistics": {
                "total": len(results),
                "positive_count": positive_count,
                "negative_count": negative_count,
                "neutral_count": neutral_count,
                "average_score": round(sum(scores) / len(scores), 3) if scores else 0,
                "positive_ratio": round(positive_count / len(results), 2) if results else 0,
                "negative_ratio": round(negative_count / len(results), 2) if results else 0,
                "overall_sentiment": "positive" if sum(scores) / len(scores) > 0.2 else (
                    "negative" if sum(scores) / len(scores) < -0.2 else "neutral"
                ) if scores else "neutral"
            },
            "algorithm": "dictionary_based"
        }

    @staticmethod
    def _tokenize(text):
        """分词处理（支持中英文混合）"""
        # 中文：按字符分，但保留常见双字词
        # 英文：按空格分词
        tokens = []

        # 匹配中文词和英文单词
        # 中文部分按2-gram提取常见词
        chinese_parts = re.findall(r'[\u4e00-\u9fff]+', text)
        english_parts = re.findall(r"[a-z]+[a-z']*", text)

        # 处理中文部分 - 拆成单个字和双字组合
        for part in chinese_parts:
            # 加单字
            for char in part:
                tokens.append(char)
            # 加双字组合
            for i in range(len(part) - 1):
                tokens.append(part[i:i + 2])

        # 处理英文部分
        tokens.extend(english_parts)

        return tokens
