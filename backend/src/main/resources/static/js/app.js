/**
 * Tlias 学生管理系统 - 主应用逻辑
 */
let clazzMap = {}; // id → name

// ===== 导航 =====
document.querySelectorAll('.nav-item').forEach(item => {
  item.addEventListener('click', () => {
    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
    item.classList.add('active');
    document.querySelectorAll('.page-section').forEach(s => s.classList.remove('active'));
    const target = document.getElementById(item.dataset.page);
    if (target) target.classList.add('active');
    document.getElementById('pageTitle').textContent = item.dataset.title || '';
    document.getElementById('pageTitleEmoji').textContent = item.dataset.emoji || '';
  });
});

// ===== Alert Helper =====
function showAlert(msg, type = 'success') {
  const el = document.getElementById('alert');
  el.textContent = msg;
  el.className = `alert show ${type}`;
  setTimeout(() => el.classList.remove('show'), 3000);
}

// ===== Modal Helper =====
function openModal(id) { document.getElementById(id).classList.add('show'); }
function closeModal(id) { document.getElementById(id).classList.remove('show'); }
document.querySelectorAll('.modal-overlay').forEach(m => {
  m.addEventListener('click', e => {
    if (e.target === m) m.classList.remove('show');
  });
});
document.querySelectorAll('.modal-close').forEach(b => {
  b.addEventListener('click', () => {
    b.closest('.modal-overlay').classList.remove('show');
  });
});

// ===== 加载班级列表（下拉选项用） =====
async function loadClazzOptions() {
  try {
    const res = await API.getAllClazzes();
    if (res.code === 1) {
      const list = res.data || [];
      list.forEach(c => clazzMap[c.id] = c.name);
      const selects = document.querySelectorAll('.clazz-select');
      selects.forEach(sel => {
        const current = sel.value;
        sel.innerHTML = '<option value="">全部班级</option>' +
          list.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
        if (current) sel.value = current;
      });
    }
  } catch (e) { /* ignore */ }
}

// ================================================================
// 仪表盘
// ================================================================
async function loadDashboard() {
  try {
    // 获取班级数
    const clazzRes = await API.getAllClazzes();
    const clazzCount = clazzRes.code === 1 ? (clazzRes.data || []).length : '—';

    // 获取学员数
    const stuRes = await API.getStudents({ page: 1, pageSize: 1 });
    const stuTotal = stuRes.code === 1 ? (stuRes.data?.total || 0) : '—';

    document.getElementById('statClazzCount').textContent = clazzCount;
    document.getElementById('statStudentCount').textContent = stuTotal;

    // 健康检查
    const health = await API.health();
    const badge = document.getElementById('serverStatus');
    if (health.code === 1 && health.data === 'UP') {
      badge.innerHTML = '<span class="dot"></span> 运行正常';
      badge.className = 'status-badge online';
    }
  } catch (e) {
    document.getElementById('serverStatus').innerHTML = '⚠️ 连接失败';
  }
}

// ================================================================
// 班级管理
// ================================================================
let clazzPage = 1;

async function loadClazzes() {
  const tbody = document.getElementById('clazzTableBody');
  tbody.innerHTML = '<tr><td colspan="7" class="loading"><div class="spinner"></div>加载中...</td></tr>';

  try {
    const name = document.getElementById('clazzSearch').value;
    const params = { page: clazzPage, pageSize: 10 };
    if (name) params.name = name;

    const res = await API.getClazzes(params);
    if (res.code !== 1) {
      tbody.innerHTML = `<tr><td colspan="7" class="empty-state">加载失败</td></tr>`;
      return;
    }

    const data = res.data;
    const rows = data.rows || [];
    if (rows.length === 0) {
      tbody.innerHTML = '<tr><td colspan="7"><div class="empty-state"><div class="icon">📭</div><p>暂无班级数据</p></div></td></tr>';
      return;
    }

    const subjectMap = { 1: 'Java', 2: '前端', 3: '大数据', 4: 'Python', 5: '其他' };
    tbody.innerHTML = rows.map(c => `
      <tr>
        <td>${c.id}</td>
        <td><strong>${c.name}</strong></td>
        <td>${c.room || '—'}</td>
        <td>${c.beginDate || '—'}</td>
        <td>${c.endDate || '—'}</td>
        <td><span class="tag ${c.status === '已开班' ? 'success' : 'warning'}">${c.status || '未开班'}</span></td>
        <td>
          <div class="btn-group">
            <button class="btn btn-outline btn-sm" onclick="editClazz(${c.id})">✏️ 编辑</button>
            <button class="btn btn-danger btn-sm" onclick="deleteClazz(${c.id})">🗑️ 删除</button>
          </div>
        </td>
      </tr>
    `).join('');

    document.getElementById('clazzPagination').innerHTML = `
      <span style="color:#6b7a8a;font-size:.85rem">共 ${data.total} 条</span>
      <div class="btn-group">
        <button class="btn btn-outline btn-sm" onclick="clazzPage--;loadClazzes()" ${clazzPage <= 1 ? 'disabled' : ''}>上一页</button>
        <button class="btn btn-outline btn-sm" onclick="clazzPage++;loadClazzes()" ${rows.length < 10 ? 'disabled' : ''}>下一页</button>
      </div>
    `;
  } catch (e) {
    tbody.innerHTML = '<tr><td colspan="7"><div class="empty-state" style="color:#c62828">❌ 请求失败，请确认后端已启动</div></td></tr>';
  }
}

function searchClazz() { clazzPage = 1; loadClazzes(); }

function openAddClazz() {
  document.getElementById('clazzFormTitle').textContent = '添加班级';
  document.getElementById('clazzForm').reset();
  document.getElementById('clazzId').value = '';
  openModal('clazzModal');
}

async function editClazz(id) {
  try {
    const res = await API.getClazz(id);
    if (res.code !== 1) { showAlert('获取班级信息失败', 'error'); return; }
    const c = res.data;
    document.getElementById('clazzFormTitle').textContent = '编辑班级';
    document.getElementById('clazzId').value = c.id;
    document.getElementById('c_name').value = c.name || '';
    document.getElementById('c_room').value = c.room || '';
    document.getElementById('c_beginDate').value = c.beginDate || '';
    document.getElementById('c_endDate').value = c.endDate || '';
    document.getElementById('c_subject').value = c.subject || 1;
    openModal('clazzModal');
  } catch (e) { showAlert('请求失败', 'error'); }
}

async function saveClazz() {
  const data = {
    name: document.getElementById('c_name').value,
    room: document.getElementById('c_room').value,
    beginDate: document.getElementById('c_beginDate').value,
    endDate: document.getElementById('c_endDate').value,
    subject: parseInt(document.getElementById('c_subject').value),
  };
  const id = document.getElementById('clazzId').value;

  if (!data.name) { showAlert('请输入班级名称', 'error'); return; }

  try {
    let res;
    if (id) {
      data.id = parseInt(id);
      res = await API.updateClazz(data);
    } else {
      res = await API.addClazz(data);
    }
    if (res.code === 1) {
      showAlert(id ? '✅ 班级已更新' : '✅ 班级已添加');
      closeModal('clazzModal');
      loadClazzes();
      loadClazzOptions();
    } else {
      showAlert(res.msg || '操作失败', 'error');
    }
  } catch (e) { showAlert('请求失败', 'error'); }
}

async function deleteClazz(id) {
  if (!confirm('确定要删除这个班级吗？')) return;
  try {
    const res = await API.deleteClazz(id);
    if (res.code === 1) {
      showAlert('✅ 班级已删除');
      loadClazzes();
      loadClazzOptions();
    } else {
      showAlert(res.msg || '删除失败', 'error');
    }
  } catch (e) { showAlert('请求失败', 'error'); }
}

// ================================================================
// 学员管理
// ================================================================
let stuPage = 1;

async function loadStudents() {
  const tbody = document.getElementById('studentTableBody');
  tbody.innerHTML = '<tr><td colspan="9" class="loading"><div class="spinner"></div>加载中...</td></tr>';

  try {
    const name = document.getElementById('stuSearch').value;
    const degree = document.getElementById('stuDegree').value;
    const clazzId = document.getElementById('stuClazz').value;
    const params = { page: stuPage, pageSize: 10 };
    if (name) params.name = name;
    if (degree) params.degree = degree;
    if (clazzId) params.clazzId = clazzId;

    const res = await API.getStudents(params);
    if (res.code !== 1) {
      tbody.innerHTML = `<tr><td colspan="9" class="empty-state">加载失败</td></tr>`;
      return;
    }

    const data = res.data;
    const rows = data.rows || [];
    if (rows.length === 0) {
      tbody.innerHTML = '<tr><td colspan="9"><div class="empty-state"><div class="icon">📭</div><p>暂无学员数据</p></div></td></tr>';
      return;
    }

    const degreeMap = { 1: '初中', 2: '高中', 3: '大专', 4: '本科', 5: '硕士', 6: '博士' };
    tbody.innerHTML = rows.map(s => `
      <tr>
        <td>${s.id}</td>
        <td><strong>${s.name}</strong></td>
        <td>${s.no || '—'}</td>
        <td>${s.gender === 1 ? '男' : '女'}</td>
        <td>${degreeMap[s.degree] || '—'}</td>
        <td>${clazzMap[s.clazzId] || '—'}</td>
        <td><span class="tag ${(s.violationScore || 0) > 0 ? 'danger' : 'success'}">${s.violationScore || 0} 分</span></td>
        <td>${s.phone || '—'}</td>
        <td>
          <div class="btn-group">
            <button class="btn btn-outline btn-sm" onclick="editStudent(${s.id})">✏️</button>
            <button class="btn btn-warning btn-sm" onclick="violationStudent(${s.id})">⚠️</button>
            <button class="btn btn-danger btn-sm" onclick="deleteStudent(${s.id})">🗑️</button>
          </div>
        </td>
      </tr>
    `).join('');

    document.getElementById('stuPagination').innerHTML = `
      <span style="color:#6b7a8a;font-size:.85rem">共 ${data.total} 条</span>
      <div class="btn-group">
        <button class="btn btn-outline btn-sm" onclick="stuPage--;loadStudents()" ${stuPage <= 1 ? 'disabled' : ''}>上一页</button>
        <button class="btn btn-outline btn-sm" onclick="stuPage++;loadStudents()" ${rows.length < 10 ? 'disabled' : ''}>下一页</button>
      </div>
    `;
  } catch (e) {
    tbody.innerHTML = '<tr><td colspan="9"><div class="empty-state" style="color:#c62828">❌ 请求失败，请确认后端已启动</div></td></tr>';
  }
}

function searchStudent() { stuPage = 1; loadStudents(); }

function openAddStudent() {
  document.getElementById('studentFormTitle').textContent = '添加学员';
  document.getElementById('studentForm').reset();
  document.getElementById('studentId').value = '';
  loadClazzOptions();
  openModal('studentModal');
}

async function editStudent(id) {
  try {
    await loadClazzOptions();
    const res = await API.getStudent(id);
    if (res.code !== 1) { showAlert('获取学员信息失败', 'error'); return; }
    const s = res.data;
    document.getElementById('studentFormTitle').textContent = '编辑学员';
    document.getElementById('studentId').value = s.id;
    document.getElementById('s_name').value = s.name || '';
    document.getElementById('s_no').value = s.no || '';
    document.getElementById('s_gender').value = s.gender || 1;
    document.getElementById('s_phone').value = s.phone || '';
    document.getElementById('s_degree').value = s.degree || '';
    document.getElementById('s_clazz').value = s.clazzId || '';
    openModal('studentModal');
  } catch (e) { showAlert('请求失败', 'error'); }
}

async function saveStudent() {
  const data = {
    name: document.getElementById('s_name').value,
    no: document.getElementById('s_no').value,
    gender: parseInt(document.getElementById('s_gender').value),
    phone: document.getElementById('s_phone').value,
    degree: parseInt(document.getElementById('s_degree').value) || null,
    clazzId: parseInt(document.getElementById('s_clazz').value) || null,
  };
  const id = document.getElementById('studentId').value;

  if (!data.name || !data.no) { showAlert('请填写姓名和学号', 'error'); return; }

  try {
    let res;
    if (id) {
      data.id = parseInt(id);
      res = await API.updateStudent(data);
    } else {
      res = await API.addStudent(data);
    }
    if (res.code === 1) {
      showAlert(id ? '✅ 学员已更新' : '✅ 学员已添加');
      closeModal('studentModal');
      loadStudents();
      loadDashboard();
    } else {
      showAlert(res.msg || '操作失败', 'error');
    }
  } catch (e) { showAlert('请求失败', 'error'); }
}

async function deleteStudent(id) {
  if (!confirm('确定要删除该学员吗？')) return;
  try {
    const res = await API.deleteStudents(id);
    if (res.code === 1) {
      showAlert('✅ 学员已删除');
      loadStudents();
      loadDashboard();
    } else {
      showAlert(res.msg || '删除失败', 'error');
    }
  } catch (e) { showAlert('请求失败', 'error'); }
}

function violationStudent(id) {
  document.getElementById('violationId').value = id;
  document.getElementById('violationScore').value = '';
  openModal('violationModal');
}

async function saveViolation() {
  const id = document.getElementById('violationId').value;
  const score = parseInt(document.getElementById('violationScore').value);
  if (!score || score < 1 || score > 100) { showAlert('请输入有效的扣分数值（1-100）', 'error'); return; }
  try {
    const res = await API.handleViolation(id, score);
    if (res.code === 1) {
      showAlert(`✅ 违纪已记录，扣 ${score} 分`);
      closeModal('violationModal');
      loadStudents();
    } else {
      showAlert(res.msg || '操作失败', 'error');
    }
  } catch (e) { showAlert('请求失败', 'error'); }
}

// ================================================================
// 数据统计
// ================================================================
let degreeChart = null, countChart = null;

async function loadReports() {
  try {
    const [degRes, cntRes] = await Promise.all([
      API.getDegreeReport(),
      API.getCountReport(),
    ]);

    // 学历分布
    if (degRes.code === 1 && degRes.data) {
      renderDegreeChart(degRes.data);
    }

    // 班级人数
    if (cntRes.code === 1 && cntRes.data) {
      renderCountChart(cntRes.data);
    }
  } catch (e) {
    document.getElementById('degreeChart').innerHTML =
      '<div class="empty-state" style="color:#c62828">❌ 数据加载失败，请确认 MySQL 已启动</div>';
    document.getElementById('countChart').innerHTML = '';
  }
}

function renderDegreeChart(data) {
  const canvas = document.getElementById('degreeChart');
  const ctx = canvas.getContext('2d');

  if (degreeChart) degreeChart.destroy();

  // 如果没有 Chart.js 就显示表格
  if (typeof Chart === 'undefined') {
    const html = '<table style="width:100%"><tr><th>学历</th><th>人数</th></tr>' +
      data.map(d => `<tr><td>${d.name}</td><td><strong>${d.count}</strong> 人</td></tr>`).join('') +
      '</table>';
    canvas.style.display = 'none';
    document.getElementById('degreeChartTable').innerHTML = html;
    return;
  }

  canvas.style.display = 'block';
  document.getElementById('degreeChartTable').innerHTML = '';

  degreeChart = new Chart(ctx, {
    type: 'doughnut',
    data: {
      labels: data.map(d => d.name),
      datasets: [{
        data: data.map(d => d.count),
        backgroundColor: ['#326ce5', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899'],
        borderWidth: 0,
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { position: 'bottom' } }
    }
  });
}

function renderCountChart(data) {
  const canvas = document.getElementById('countChart');
  const ctx = canvas.getContext('2d');

  if (countChart) countChart.destroy();

  if (typeof Chart === 'undefined') {
    const html = '<table style="width:100%"><tr><th>班级</th><th>人数</th></tr>' +
      (data.clazzList || []).map((name, i) =>
        `<tr><td>${name}</td><td><strong>${(data.dataList || [])[i] || 0}</strong> 人</td></tr>`
      ).join('') + '</table>';
    canvas.style.display = 'none';
    document.getElementById('countChartTable').innerHTML = html;
    return;
  }

  canvas.style.display = 'block';
  document.getElementById('countChartTable').innerHTML = '';

  countChart = new Chart(ctx, {
    type: 'bar',
    data: {
      labels: data.clazzList || [],
      datasets: [{
        label: '学员人数',
        data: data.dataList || [],
        backgroundColor: '#326ce5',
        borderRadius: 6,
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: { legend: { display: false } },
      scales: {
        y: {
          beginAtZero: true,
          ticks: { stepSize: 1 }
        }
      }
    }
  });
}

// ================================================================
// 初始化
// ================================================================
async function init() {
  await loadClazzOptions();
  await loadDashboard();
  loadClazzes();
  loadStudents();
  loadReports();
}

// 切换统计 tab
function switchReport(tab, chartId, tableId) {
  document.querySelectorAll('.tabs .tab').forEach(t => t.classList.remove('active'));
  tab.classList.add('active');
  const showChart = tab.dataset.chart === 'chart';
  document.getElementById(chartId).style.display = showChart ? 'block' : 'none';
  document.getElementById(tableId).style.display = showChart ? 'none' : 'block';
  if (showChart && chartId === 'degreeChartCanvas' && degreeChart) degreeChart.resize();
  if (showChart && chartId === 'countChartCanvas' && countChart) countChart.resize();
}

// 加载 Chart.js
const script = document.createElement('script');
script.src = 'https://cdn.jsdelivr.net/npm/chart.js@4/dist/chart.umd.min.js';
script.onload = () => { loadReports(); };
document.head.appendChild(script);

document.addEventListener('DOMContentLoaded', init);
