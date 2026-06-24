/**
 * Tlias 学生管理系统 - API 调用模块
 */
const API = {
  BASE: 'http://localhost:8080',

  async request(method, path, body = null) {
    const opts = {
      method,
      headers: { 'Content-Type': 'application/json' },
    };
    if (body) opts.body = JSON.stringify(body);
    const res = await fetch(this.BASE + path, opts);
    return res.json();
  },

  // ===== 健康检查 =====
  async health() { return this.request('GET', '/health'); },

  // ===== 班级管理 =====
  async getClazzes(params = {}) {
    const qs = new URLSearchParams(params).toString();
    return this.request('GET', `/clazzs${qs ? '?' + qs : ''}`);
  },
  async getAllClazzes() { return this.request('GET', '/clazzs/list'); },
  async getClazz(id) { return this.request('GET', `/clazzs/${id}`); },
  async addClazz(data) { return this.request('POST', '/clazzs', data); },
  async updateClazz(data) { return this.request('PUT', '/clazzs', data); },
  async deleteClazz(id) { return this.request('DELETE', `/clazzs/${id}`); },

  // ===== 学员管理 =====
  async getStudents(params = {}) {
    const qs = new URLSearchParams(params).toString();
    return this.request('GET', `/students${qs ? '?' + qs : ''}`);
  },
  async getStudent(id) { return this.request('GET', `/students/${id}`); },
  async addStudent(data) { return this.request('POST', '/students', data); },
  async updateStudent(data) { return this.request('PUT', '/students', data); },
  async deleteStudents(ids) { return this.request('DELETE', `/students/${ids}`); },
  async handleViolation(id, score) {
    return this.request('PUT', `/students/violation/${id}/${score}`);
  },

  // ===== 数据统计 =====
  async getDegreeReport() { return this.request('GET', '/report/studentDegreeData'); },
  async getCountReport() { return this.request('GET', '/report/studentCountData'); },
};
