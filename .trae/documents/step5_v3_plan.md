# V3版本开发计划 - 后台管理功能

## 项目概述
**项目名称**: QuizMaster - 单选题刷题系统 (V3)
**开发目标**: 添加后台管理功能，支持管理员账号管理题目、用户、数据统计
**创建时间**: 2026-06-21
**当前状态**: 📋 规划阶段

---

## 一、功能需求分析

### 1.1 用户角色体系

| 角色 | 权限说明 |
|------|---------|
| 普通用户 (USER) | 刷题、查看历史、错题本、个人中心 |
| 管理员 (ADMIN) | 用户管理、题目管理、分类管理、数据统计、题库导入 |

### 1.2 后台管理功能模块

#### 1.2.1 用户管理模块
- [ ] 用户列表（分页、搜索）
- [ ] 用户详情查看
- [ ] 用户禁用/启用
- [ ] 用户删除
- [ ] 用户答题统计

#### 1.2.2 题目管理模块
- [ ] 题目列表（分页、搜索、按分类筛选）
- [ ] 题目新增
- [ ] 题目编辑
- [ ] 题目删除
- [ ] 批量删除

#### 1.2.3 分类管理模块
- [ ] 分类列表
- [ ] 分类新增
- [ ] 分类编辑
- [ ] 分类删除
- [ ] 分类题目统计

#### 1.2.4 数据统计模块
- [ ] 用户总数统计
- [ ] 题目总数统计
- [ ] 答题次数统计
- [ ] 正确率统计
- [ ] 活跃用户统计

#### 1.2.5 系统设置模块
- [ ] 管理员账号管理
- [ ] 系统配置

### 1.3 默认管理员账号

| 字段 | 值 |
|------|-----|
| 用户名 | admin |
| 密码 | admin123 |
| 角色 | ADMIN |

---

## 二、数据库变更方案

### 2.1 用户表变更

**变更内容**: 添加角色字段

```sql
-- 添加角色字段
ALTER TABLE `user` ADD COLUMN `role` VARCHAR(20) DEFAULT 'USER' COMMENT '角色: USER-普通用户, ADMIN-管理员' AFTER `password`;

-- 创建默认管理员账号
INSERT INTO `user` (`username`, `password`, `role`) VALUES ('admin', '$2a$10$...', 'ADMIN');
```

**注意**: 密码需要使用BCrypt加密存储

### 2.2 数据库迁移脚本

**文件位置**: `java-back/src/main/resources/db/migration/V3__add_user_role.sql`

---

## 三、后端开发计划

### 3.1 实体层修改

| 文件 | 修改内容 |
|------|---------|
| `User.java` | 添加role字段 |
| `Category.java` | 无需修改 |

### 3.2 新增DTO

| 文件名 | 用途 |
|--------|------|
| `AdminUserDTO.java` | 管理端用户信息 |
| `AdminQuestionDTO.java` | 管理端题目信息 |
| `StatisticsDTO.java` | 统计数据 |
| `CategoryDTO.java` | 分类信息 |

### 3.3 新增Controller

| 文件名 | API路径 | 功能 |
|--------|---------|------|
| `AdminController.java` | `/api/admin/*` | 管理后台入口 |
| `AdminUserController.java` | `/api/admin/users/*` | 用户管理 |
| `AdminQuestionController.java` | `/api/admin/questions/*` | 题目管理 |
| `AdminCategoryController.java` | `/api/admin/categories/*` | 分类管理 |
| `AdminStatisticsController.java` | `/api/admin/statistics/*` | 数据统计 |

### 3.4 API设计

#### 用户管理API
```
GET    /api/admin/users          # 获取用户列表
GET    /api/admin/users/{id}     # 获取用户详情
PUT    /api/admin/users/{id}     # 更新用户状态
DELETE /api/admin/users/{id}     # 删除用户
```

#### 题目管理API
```
GET    /api/admin/questions          # 获取题目列表
POST   /api/admin/questions          # 新增题目
PUT    /api/admin/questions/{id}     # 更新题目
DELETE /api/admin/questions/{id}     # 删除题目
DELETE /api/admin/questions/batch    # 批量删除
```

#### 分类管理API
```
GET    /api/admin/categories         # 获取分类列表
POST   /api/admin/categories         # 新增分类
PUT    /api/admin/categories/{id}    # 更新分类
DELETE /api/admin/categories/{id}    # 删除分类
```

#### 统计API
```
GET    /api/admin/statistics/overview    # 总览统计
GET    /api/admin/statistics/users       # 用户统计
GET    /api/admin/statistics/questions   # 题目统计
```

### 3.5 权限控制

**SecurityConfig.java修改**:
```java
// 管理员API需要ADMIN角色
.requestMatchers("/api/admin/**").hasRole("ADMIN")
```

---

## 四、前端开发计划

### 4.1 新增页面

| 页面文件 | 路由路径 | 功能 |
|----------|---------|------|
| `AdminLayout.vue` | - | 管理后台布局组件 |
| `AdminDashboard.vue` | `/admin` | 管理后台首页 |
| `AdminUsers.vue` | `/admin/users` | 用户管理 |
| `AdminQuestions.vue` | `/admin/questions` | 题目管理 |
| `AdminCategories.vue` | `/admin/categories` | 分类管理 |
| `AdminStatistics.vue` | `/admin/statistics` | 数据统计 |

### 4.2 路由守卫

```typescript
// 检查用户角色，非管理员跳转到首页
router.beforeEach((to, from, next) => {
  if (to.path.startsWith('/admin')) {
    const userRole = localStorage.getItem('userRole')
    if (userRole !== 'ADMIN') {
      next('/')
      return
    }
  }
  next()
})
```

### 4.3 UI组件

- 管理后台侧边栏导航
- 数据表格组件（分页、搜索、排序）
- 表单弹窗组件
- 统计图表组件

---

## 五、开发任务清单

### 5.1 后端任务

| 任务ID | 任务描述 | 状态 | 优先级 |
|--------|---------|------|--------|
| V3-BE-001 | 修改User实体添加role字段 | ⏳ 待开始 | 高 |
| V3-BE-002 | 创建数据库迁移脚本 | ⏳ 待开始 | 高 |
| V3-BE-003 | 创建默认管理员账号 | ⏳ 待开始 | 高 |
| V3-BE-004 | 修改SecurityConfig添加角色验证 | ⏳ 待开始 | 高 |
| V3-BE-005 | 创建AdminUserController | ⏳ 待开始 | 中 |
| V3-BE-006 | 创建AdminQuestionController | ⏳ 待开始 | 中 |
| V3-BE-007 | 创建AdminCategoryController | ⏳ 待开始 | 中 |
| V3-BE-008 | 创建AdminStatisticsController | ⏳ 待开始 | 中 |
| V3-BE-009 | 创建相关DTO类 | ⏳ 待开始 | 中 |
| V3-BE-010 | 修改UserService支持角色 | ⏳ 待开始 | 中 |
| V3-BE-011 | 修改登录接口返回角色信息 | ⏳ 待开始 | 高 |

### 5.2 前端任务

| 任务ID | 任务描述 | 状态 | 优先级 |
|--------|---------|------|--------|
| V3-FE-001 | 创建AdminLayout布局组件 | ⏳ 待开始 | 高 |
| V3-FE-002 | 创建AdminDashboard页面 | ⏳ 待开始 | 中 |
| V3-FE-003 | 创建AdminUsers页面 | ⏳ 待开始 | 中 |
| V3-FE-004 | 创建AdminQuestions页面 | ⏳ 待开始 | 中 |
| V3-FE-005 | 创建AdminCategories页面 | ⏳ 待开始 | 中 |
| V3-FE-006 | 创建AdminStatistics页面 | ⏳ 待开始 | 低 |
| V3-FE-007 | 添加路由配置和守卫 | ⏳ 待开始 | 高 |
| V3-FE-008 | 修改登录逻辑存储角色 | ⏳ 待开始 | 高 |
| V3-FE-009 | 添加管理后台入口 | ⏳ 待开始 | 中 |

---

## 六、开发进度跟踪

### 6.1 当前进度

**当前阶段**: 规划阶段
**当前任务**: 创建V3开发计划文档
**下一步**: 开始后端开发

### 6.2 进度日志

| 日期 | 完成内容 | 状态 |
|------|---------|------|
| 2026-06-21 | 创建V3开发计划文档 | ✅ 完成 |

---

## 七、踩坑预防清单

### 7.1 历史踩坑经验（V1/V2）

| 问题类型 | 问题描述 | 解决方案 |
|---------|---------|---------|
| 字符编码 | MySQL字符集不一致导致乱码 | 统一使用utf8mb4，配置characterSetResults |
| PowerShell | &&语法错误 | 使用分号分隔命令 |
| Docker | MySQL启动慢导致后端连接失败 | 使用healthcheck等待 |
| 端口冲突 | 3306/8080端口被占用 | 修改映射端口 |

### 7.2 V3潜在风险

| 风险点 | 预防措施 |
|--------|---------|
| 密码加密 | 使用BCrypt加密，不要明文存储 |
| 权限绕过 | 所有管理API必须验证ADMIN角色 |
| SQL注入 | 使用MyBatis-Plus参数绑定 |
| XSS攻击 | 前端输入过滤，后端输出转义 |

---

## 八、测试计划

### 8.1 功能测试

- [ ] 管理员登录测试
- [ ] 普通用户无法访问管理后台测试
- [ ] 用户管理CRUD测试
- [ ] 题目管理CRUD测试
- [ ] 分类管理CRUD测试
- [ ] 数据统计准确性测试

### 8.2 安全测试

- [ ] 权限控制测试
- [ ] SQL注入测试
- [ ] XSS攻击测试
- [ ] CSRF防护测试

---

## 九、文档版本

**版本**: V3.0
**创建时间**: 2026-06-21
**最后更新**: 2026-06-21
**更新内容**: 初始化V3开发计划