FROM docker.m.daocloud.io/library/python:3.11-slim

# 通用科学计算包，按团队实际算法需要增删
RUN pip install --no-cache-dir -i https://pypi.tuna.tsinghua.edu.cn/simple \
    numpy pandas scipy scikit-learn statsmodels joblib

# 数据库访问依赖：pymysql 驱动 + cryptography（MySQL 8/9 caching_sha2_password 认证）
RUN pip install --no-cache-dir -i https://pypi.tuna.tsinghua.edu.cn/simple \
    pymysql cryptography

# 非 root 运行，降低脚本越权风险
RUN useradd -m -u 10001 runner
WORKDIR /app
COPY runner.py /app/runner.py
COPY platform_db.py /app/platform_db.py
RUN chown -R runner:runner /app
USER runner

# 关键：容器不自动退出，靠 docker exec 驱动
CMD ["sleep", "infinity"]