# 第一阶段：项目初始化 - 开发记录

**完成时间**: 2026-06-21
**状态**: ✅ 已完成

---

## 一、前端项目初始化 (Vue 3 + Vite)

### 1.1 项目结构创建
```
vue-front/
├── src/
│   ├── views/
│   │   ├── LoginView.vue        # 登录页
│   │   ├── RegisterView.vue     # 注册页
│   │   ├── HomeView.vue         # 首页
│   │   ├── QuizView.vue         # 答题页
│   │   ├── HistoryView.vue      # 历史记录
│   │   ├── WrongBookView.vue    # 错题本
│   │   └── ProfileView.vue      # 个人中心
│   ├── router/
│   │   └── index.ts             # 路由配置
│   ├── App.vue
│   ├── main.ts
│   └── style.css
├── package.json
├── vite.config.ts
├── tsconfig.json
├── tailwind.config.js
├── postcss.config.js
└── index.html
```

### 1.2 技术栈
- **框架**: Vue 3.5 + TypeScript
- **构建工具**: Vite 6.0
- **路由**: Vue Router 4.5
- **状态管理**: Pinia 3.0
- **HTTP客户端**: Axios 1.7
- **CSS框架**: TailwindCSS 3.4
- **移动端适配**: viewport + rem适配

### 1.3 依赖安装
```bash
cd vue-front
npm install
```

---

## 二、后端项目初始化 (Spring Boot)

### 2.1 项目结构
```
java-back/
├── src/main/java/com/quizmaster/
│   ├── QuizMasterApplication.java
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── UserController.java
│   │   ├── QuestionController.java
│   │   └── AnswerController.java
│   ├── service/
│   │   ├── UserService.java
│   │   ├── QuestionService.java
│   │   └── AnswerService.java
│   ├── mapper/
│   │   ├── UserMapper.java
│   │   ├── QuestionMapper.java
│   │   ├── QuestionOptionMapper.java
│   │   ├── AnswerRecordMapper.java
│   │   └── CategoryMapper.java
│   ├── entity/
│   │   ├── User.java
│   │   ├── Question.java
│   │   ├── QuestionOption.java
│   │   ├── AnswerRecord.java
│   │   └── Category.java
│   ├── dto/
│   │   ├── LoginDTO.java
│   │   ├── RegisterDTO.java
│   │   ├── QuestionDTO.java
│   │   └── SubmitAnswerDTO.java
│   ├── common/
│   │   ├── Result.java
│   │   └── JwtUtils.java
│   └── security/
│       └── JwtAuthenticationFilter.java
├── src/main/resources/
│   ├── application.yml
│   └── schema.sql
└── pom.xml
```

### 2.2 技术栈
- **框架**: Spring Boot 3.2
- **安全**: Spring Security + JWT
- **ORM**: MyBatis-Plus 3.5
- **数据库**: MySQL 8.0
- **Java版本**: JDK 17

---

## 三、踩坑记录

### 3.1 PowerShell命令语法问题
**问题**: 使用 `&&` 连接符时报错 "标记'&&'不是此版本中的有效语句分隔符"
**解决**: 使用 `;` 分号分隔命令，或分开执行

### 3.2 npm create vite 交互问题
**问题**: `npm create vite@latest` 需要交互式输入
**解决**: 手动创建项目结构和配置文件

---

## 四、后续任务

- [ ] 初始化数据库 (执行 schema.sql)
- [ ] 安装前端依赖
- [ ] 配置数据库连接
- [ ] 测试后端启动

---

## 五、异常记录

| 日期 | 异常描述 | 解决方案 |
|------|---------|---------|
| 2026-06-21 | PowerShell && 语法错误 | 改用 ; 分号分隔 |
| 2026-06-21 | npm create 交互卡住 | 手动创建项目结构 |
