# V5版本开发计划 - 题目管理分页功能完善

## 项目概述
- **项目名称**: QuizMaster - 单选题刷题系统 (V5)
- **开发目标**: 完善题目管理模块的分页功能，实现高性能、用户友好的分页体验
- **创建时间**: 2026-06-21
- **当前状态**: 📋 规划阶段

---

## 一、当前现状分析

### 1.1 现有代码分析

#### 后端现状 (`AdminQuestionController.java`)
- 已使用 MyBatis-Plus `Page` 对象接收分页参数
- 已实现 `selectPage()` 进行数据库分页查询
- 已实现 DTO 转换（含分类名称、选项关联查询）
- **缺失**: MyBatis-Plus 分页插件 Bean 配置 (`PaginationInnerInterceptor`)

#### 前端现状 (`AdminQuestions.vue`)
- 已发送 `page` / `size` 参数请求后端
- 已显示分页按钮
- **缺陷1**: 使用 `v-for="page in Math.ceil(total / pageSize)"` 无条件展示所有页码，1000条数据时展示100个按钮
- **缺陷2**: 无上一页/下一页按钮
- **缺陷3**: 无页码省略机制
- **缺陷4**: 无每页条数选择器
- **缺陷5**: 总记录数信息未展示

### 1.2 其他管理页面分页情况

| 页面 | 后端 | 前端 | 状态 |
|------|------|------|------|
| 题目管理 | 有分页(待完善) | 有分页(需优化) | 🔧 需完善 |
| 用户管理 | 待检查 | 待检查 | ❓ |
| 分类管理 | 少量数据，无需分页 | - | ✅ |

---

## 二、需求分析

### 2.1 功能需求

| 需求ID | 需求描述 | 优先级 | 说明 |
|--------|---------|-------|------|
| V5-REQ-001 | 后端配置 MyBatis-Plus 分页插件 | 高 | 确保 `Page` 对象正确执行分页SQL |
| V5-REQ-002 | 后端分页API返回完整分页信息 | 高 | total、pages、current、size 等字段 |
| V5-REQ-003 | 前端优化页码展示（省略号、页码范围） | 高 | 避免页码过多 |
| V5-REQ-004 | 添加上一页/下一页导航按钮 | 中 | 提升用户体验 |
| V5-REQ-005 | 添加每页条数选择器 | 中 | 支持 10/20/50/100 |
| V5-REQ-006 | 显示当前页范围和总记录数 | 低 | 如"共 100 条，第 1-10 条" |
| V5-REQ-007 | 用户管理列表加分页 | 中 | 与题目管理统一 |

### 2.2 分页组件设计

```
┌──────────────────────────────────────────────────────────────┐
│  共 100 条记录，第 1-10 条          每页 10 条 ▼             │
│                                                              │
│  [首页] [上一页] 1  2  3  4  5 ... 10 [下一页] [末页]        │
└──────────────────────────────────────────────────────────────┘
```

**分页规则**:
- 当前页前后各显示 2 个页码
- 总页数 <= 7 时全部显示
- 总页数 > 7 时使用省略号
- 始终显示第一页和最后一页

---

## 三、技术方案

### 3.1 后端方案

#### 3.1.1 添加 MyBatis-Plus 分页插件

**文件**: `MyBatisPlusConfig.java` (新建)

```java
@Configuration
public class MyBatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

#### 3.1.2 优化 AdminQuestionController 分页

当前代码已基本可用，无需大改，但需要：
1. 确保 `Page` 的分页属性正确设置
2. 确保 `Result` 包装 `Page` 对象时能序列化完整分页信息

#### 3.1.3 用户管理列表加分页

**文件**: `AdminUserController.java`
- 当前用户管理列表API没有分页参数
- 需要添加 `page`、`size`、`keyword` 参数
- 使用 `Page<User>` + `selectPage()` 实现分页

### 3.2 前端方案

#### 3.2.1 创建分页通用组件

**文件**: `Pagination.vue` (新建)

功能：
- 接收 `total`、`current`、`size` 属性
- 发射 `page-change`、`size-change` 事件
- 实现页码省略算法
- 上一页/下一页/首页/末页按钮
- 每页条数选择器

#### 3.2.2 优化 AdminQuestions.vue

- 使用通用分页组件替换原有分页代码
- 添加分页信息展示

#### 3.2.3 优化 AdminUsers.vue

- 检查当前代码，添加分页支持

### 3.3 分页数据流

```
前端点击页码
    ↓
Pagination.vue 发射 page-change 事件
    ↓
AdminQuestions.vue 更新 currentPage
    ↓
fetchQuestions() 发送 GET /api/admin/questions?page=2&size=10
    ↓
AdminQuestionController 接收参数 → Page<Question>
    ↓
MyBatis-Plus 拦截器 → SELECT COUNT(*) + SELECT * LIMIT 10 OFFSET 10
    ↓
返回 Page<AdminQuestionDTO> (含 total, pages, current, records)
    ↓
前端更新展示
```

---

## 四、开发任务清单

### 4.1 后端任务

| 任务ID | 任务描述 | 涉及文件 | 优先级 | 状态 |
|--------|---------|---------|-------|------|
| V5-BE-001 | 创建 MyBatis-Plus 分页插件配置 | `MyBatisPlusConfig.java` | 高 | ⏳ 待开始 |
| V5-BE-002 | 检查并优化题目管理分页API | `AdminQuestionController.java` | 高 | ⏳ 待开始 |
| V5-BE-003 | 用户管理列表添加分页支持 | `AdminUserController.java` | 中 | ⏳ 待开始 |

### 4.2 前端任务

| 任务ID | 任务描述 | 涉及文件 | 优先级 | 状态 |
|--------|---------|---------|-------|------|
| V5-FE-001 | 创建通用分页组件 | `Pagination.vue` | 高 | ⏳ 待开始 |
| V5-FE-002 | 优化题目管理页面分页 | `AdminQuestions.vue` | 高 | ⏳ 待开始 |
| V5-FE-003 | 优化用户管理页面分页 | `AdminUsers.vue` | 中 | ⏳ 待开始 |

### 4.3 测试任务

| 任务ID | 任务描述 | 优先级 | 状态 |
|--------|---------|-------|------|
| V5-TEST-001 | 测试分页插件配置正确性 | 高 | ⏳ 待开始 |
| V5-TEST-002 | 测试分页API返回数据正确性 | 高 | ⏳ 待开始 |
| V5-TEST-003 | 测试前端分页交互 | 高 | ⏳ 待开始 |
| V5-TEST-004 | 测试大数量分页性能 | 中 | ⏳ 待开始 |

---

## 五、踩坑预防清单

### 5.1 历史踩坑经验（V1-V4）

| 问题类型 | 问题描述 | 解决方案 |
|---------|---------|---------|
| 字符编码 | MySQL字符集不一致导致乱码 | 统一使用utf8mb4，配置characterSetResults |
| PowerShell | &&语法错误 | 使用分号分隔命令 |
| Docker | MySQL启动慢导致后端连接失败 | 使用healthcheck等待 |
| 端口冲突 | 3306/8080端口被占用 | 修改映射端口 |
| Excel解析 | POI列索引从0开始 | 注意Excel第B列对应索引1 |
| 事务处理 | 批量操作需要事务管理 | 添加@Transactional(rollbackFor = Exception.class) |
| MyBatis-Plus批量 | BaseMapper没有insertBatch方法 | 使用ServiceImpl.saveBatch方法代替 |
| 密码加密 | schema.sql中密码hash不正确 | 使用DataInitializer在启动时初始化 |

### 5.2 V5潜在风险

| 风险点 | 预防措施 |
|--------|---------|
| MyBatis-Plus分页插件未配置导致分页不生效 | 必须显式配置 `PaginationInnerInterceptor` Bean |
| 大数量下COUNT查询性能 | 确保查询条件有索引（category_id, content） |
| 前端页码过多卡顿 | 使用省略号算法，限制渲染的页码数量 |
| 分页参数类型不匹配（int vs long） | 统一使用 `long` 类型，MyBatis-Plus Page 的 total 是 long |
| 前端请求参数名与后端不一致 | 统一使用 page/size 参数名 |

---

## 六、开发计划

### 阶段一：后端分页基础设施
1. 创建 `MyBatisPlusConfig.java` 配置分页插件
2. 验证分页插件生效

### 阶段二：后端分页API优化
1. 检查 `AdminQuestionController` 分页返回数据完整性
2. 为 `AdminUserController` 添加分页支持
3. 编译验证

### 阶段三：前端通用分页组件
1. 创建 `Pagination.vue` 通用分页组件
2. 实现页码省略算法
3. 实现上一页/下一页/首页/末页/尺寸选择

### 阶段四：前端页面集成
1. 在 `AdminQuestions.vue` 中使用新分页组件
2. 在 `AdminUsers.vue` 中使用新分页组件
3. 前端构建验证

### 阶段五：测试验证
1. 后端API测试（分页参数、边界值）
2. 前端交互测试
3. Docker环境验证

---

## 七、文档版本

**版本**: V5.0
**创建时间**: 2026-06-21
**最后更新**: 2026-06-21
**更新内容**: 初始化V5开发计划
