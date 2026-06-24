# ============================================================
# AI 学情分析服务 - Dockerfile
# 基于 Python 3.11 构建 Flask AI 服务
# ============================================================

FROM python:3.11-slim

# 设置工作目录
WORKDIR /ai-service

# 设置环境变量
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    PORT=5001

# 安装系统依赖
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    gcc \
    && rm -rf /var/lib/apt/lists/*

# 复制依赖文件并安装
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 复制应用代码
COPY . .

# 暴露端口
EXPOSE 5001

# 启动服务（使用 gunicorn 生产级 WSGI 服务器）
CMD ["gunicorn", "--bind", "0.0.0.0:5001", "--workers", "2", "--timeout", "120", "app:app"]
