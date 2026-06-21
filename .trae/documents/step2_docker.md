# 第二阶段：Docker配置 - 开发记录

**完成时间**: 2026-06-21
**状态**: ✅ 已完成

---

## 一、Docker配置文件创建

### 1.1 后端Dockerfile
**路径**: `java-back/Dockerfile`

```dockerfile
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/target/quiz-master-1.0.0.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**说明**:
- 使用多阶段构建，减小最终镜像体积
- 第一阶段：Maven编译打包
- 第二阶段：JRE运行环境（Alpine版本更小）

### 1.2 前端Dockerfile
**路径**: `vue-front/Dockerfile`

```dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

### 1.3 Nginx配置
**路径**: `vue-front/nginx.conf`

```nginx
server {
    listen 80;
    location /api/ {
        proxy_pass http://backend:8080/api/;
        ...
    }
}
```

**关键配置**: 
- API请求自动转发到后端容器
- Vue Router路由回退到index.html

### 1.4 docker-compose.yml
**路径**: `docker-compose.yml`

```yaml
services:
  mysql:
    image: mysql:8.0
    ...
  backend:
    build: ./java-back
    depends_on:
      mysql:
        condition: service_healthy
  frontend:
    build: ./vue-front
    depends_on:
      backend:
        condition: service_healthy
```

---

## 二、启动方式

### 2.1 一键启动
```bash
cd e:\dev\trace-test
docker-compose up -d
```

### 2.2 停止服务
```bash
docker-compose down
```

### 2.3 查看日志
```bash
docker-compose logs -f
```

### 2.4 服务访问
- **前端**: http://localhost:3000
- **后端API**: http://localhost:8081/api
- **数据库**: localhost:3307 (root/root)

---

## 三、环境变量配置

### 3.1 application.yml更新
```yaml
spring:
  datasource:
    url: ${SPRING_DATASOURCE_URL:jdbc:mysql://localhost:3306/quiz_master...}
    username: ${SPRING_DATASOURCE_USERNAME:root}
    password: ${SPRING_DATASOURCE_PASSWORD:root}
```

### 3.2 Docker网络配置
- 容器网络名称: `quizmaster_default`
- 服务名解析: 
  - `mysql`: MySQL数据库
  - `backend`: Spring Boot后端
  - `frontend`: Nginx前端

---

## 四、踩坑记录

### 4.1 MySQL连接问题
**问题**: Docker内部连接MySQL失败
**原因**: MySQL容器未完全启动就尝试连接
**解决**: 使用 `depends_on` + `service_healthy` 条件等待

### 4.2 MySQL 8.0连接报错
**问题**: `Public Key Retrieval is not allowed`
**解决**: 添加 `allowPublicKeyRetrieval=true` 参数

### 4.3 Spring Boot启动超时
**问题**: 后端启动快于MySQL就绪
**解决**: healthcheck配置重试机制

---

## 五、数据库初始化

### 5.1 SQL脚本位置
`java-back/src/main/resources/schema.sql`

### 5.2 初始化流程
1. MySQL容器启动时自动执行 `/docker-entrypoint-initdb.d/init.sql`
2. 脚本已映射到容器内部
3. 包含8道示例题目和6个分类

---

## 六、异常记录

| 日期 | 异常描述 | 解决方案 |
|------|---------|---------|
| 2026-06-21 | MySQL连接失败 | 添加service_healthy依赖 |
| 2026-06-21 | Public Key Retrieval错误 | URL添加allowPublicKeyRetrieval=true |
| 2026-06-21 | 端口3306被占用 | 修改MySQL映射端口为3307 |
| 2026-06-21 | 端口8080被占用 | 修改后端映射端口为8081 |
| 2026-06-21 | 健康检查失败(403) | 使用nc检测端口代替curl |
| 2026-06-21 | TypeScript编译错误 | 修复未使用变量警告 |
