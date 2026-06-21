# 刷题系统 (Quiz Master)

基于 Vue3 + Spring Boot 的在线刷题系统，支持题库管理、答题练习、成绩统计等功能。

## 技术栈

### 前端
- Vue 3 + TypeScript
- Vite 6
- Vue Router 4
- Pinia (状态管理)
- Axios (HTTP请求)
- TailwindCSS 3 (样式)

### 后端
- Spring Boot 3.2
- MyBatis-Plus (ORM)
- MySQL 8.0 (数据库)
- EasyExcel (Excel导入)
- JWT (身份认证)
- Spring Security (安全)

### 部署
- Docker + Docker Compose

## 项目结构

```
shuati/
├── doc/                    # 文档目录
│   └── V4进度文档.md       # V4版本进度文档
├── java-back/              # 后端代码
│   ├── src/main/java/      # Java源码
│   └── src/main/resources/ # 配置文件
├── vue-front/              # 前端代码
│   ├── src/                # Vue源码
│   └── vite.config.ts      # Vite配置
├── docker-compose.yml      # Docker编排配置
├── mysql.cnf               # MySQL字符集配置
└── README.md               # 项目说明
```

## 快速开始

### 环境要求
- Docker 20+
- Docker Compose 2+

### 启动服务

```bash
# 构建并启动所有服务
docker compose up -d

# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f backend
```

### 服务端口

| 服务 | 端口 | 说明 |
|------|------|------|
| 前端 | 3000 | http://localhost:3000 |
| 后端 | 8081 | http://localhost:8081 |
| MySQL | 3307 | 数据库端口 |

## 功能特性

### 用户功能
- 用户注册/登录
- 答题练习
- 错题回顾
- 成绩统计

### 管理员功能
- 题库管理（增删改查）
- Excel批量导入题目
- 分类管理
- 用户管理

## API接口

### 公开接口
- `GET /api/question/categories` - 获取题目分类

### 用户接口
- `POST /api/user/login` - 用户登录
- `POST /api/user/register` - 用户注册
- `GET /api/user/profile` - 获取用户信息

### 题目接口
- `GET /api/question/list` - 获取题目列表
- `GET /api/question/{id}` - 获取题目详情

### 导入接口
- `POST /api/import/questions` - 导入Excel题库

## 数据库配置

数据库已通过Docker自动初始化，默认配置：

- 数据库名: `quiz_master`
- 用户名: `root`
- 密码: `root`
- 字符集: `utf8mb4`

## 默认管理员

- 用户名: `admin`
- 密码: `admin123`

## Excel导入

支持导入 `.xlsx` 格式的题库文件，包含以下字段：

- 题目分类
- 题目内容
- 选项A/B/C/D
- 正确答案
- 解析

## 开发模式

### 前端开发

```bash
cd vue-front
npm install
npm run dev
```

### 后端开发

```bash
cd java-back
# 使用Maven运行
mvn spring-boot:run
```

## 部署说明

### Docker构建

```bash
# 构建镜像
docker compose build

# 启动服务
docker compose up -d

# 停止服务
docker compose down
```

### 注意事项

1. 首次启动会自动初始化数据库和默认数据
2. 确保3000、8081、3307端口未被占用
3. 数据库数据持久化存储在 `mysql-data` 卷中

## 版本记录

| 版本 | 日期 | 说明 |
|------|------|------|
| V1 | - | 初始版本 |
| V2 | - | 功能完善 |
| V3 | - | 性能优化 |
| V4 | 2026-06-21 | Docker环境重建、EasyExcel导入、乱码修复 |

## 许可证

MIT License