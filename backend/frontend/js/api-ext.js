/**
 * AI 学情分析 - API 扩展模块
 * 新增 AI 相关接口
 */
const API_AI = {
  BASE: API.BASE,

  async request(method, path, body = null) {
    const opts = {
      method,
      headers: { 'Content-Type': 'application/json' },
    };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(this.BASE + path, opts);
    return res.json();
  },

  // ===== 成绩管理 =====
  async addPerformance(data) {
    return this.request('POST', '/api/performance', data);
  },
  async getStudentPerformances(studentId) {
    return this.request('GET', `/api/performance/student/${studentId}`);
  },
  async predictPerformance(studentId) {
    return this.request('GET', `/api/performance/predict?studentId=${studentId}`);
  },

  // ===== 反馈管理 =====
  async addFeedback(data) {
    return this.request('POST', '/api/feedback', data);
  },
  async getStudentFeedbacks(studentId) {
    return this.request('GET', `/api/feedback/student/${studentId}`);
  },

  // ===== AI 报告 =====
  async getPerformancePrediction() {
    return this.request('GET', '/report/ai/performance-prediction');
  },
  async getClusterAnalysis(clazzId = 1) {
    return this.request('GET', `/report/ai/cluster-analysis?clazzId=${clazzId}`);
  },
  async getClassInsight(clazzId = 1) {
    return this.request('GET', `/report/ai/class-insight?clazzId=${clazzId}`);
  },

  // ===== 学习推荐 =====
  async getRecommendations(studentId) {
    return this.request('GET', `/api/recommend/student/${studentId}`);
  },
};
