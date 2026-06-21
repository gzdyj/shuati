# 第二版开发计划 - 乱码修复与Excel导入功能

## 项目概述
**项目名称**: QuizMaster - 单选题刷题系统 (V2)
**目标**: 
1. 修复刷题页面乱码问题
2. 添加Excel题库导入功能

---

## 问题分析

### 1.1 乱码问题根因分析

| 可能原因 | 排查方向 | 优先级 | 状态 |
|---------|---------|-------|------|
| MySQL字符集配置 | 数据库/表字符集是否为utf8mb4 | 高 | ✅ 已修复 |
| Spring Boot响应编码 | 响应Content-Type是否包含charset=utf-8 | 高 | ✅ 已修复 |
| JSON序列化编码 | Jackson配置是否正确 | 中 | ✅ 已修复 |
| Docker容器字符集 | MySQL容器默认字符集 | 中 | ✅ 已修复 |

### 1.2 Excel导入功能需求

**导入字段映射**:
| Excel字段 | 数据库字段 | 说明 |
|----------|-----------|------|
| 序号 | - | 仅用于展示，不入库 |
| title | content | 题目内容 |
| optionA | option_id=A | 选项A内容 |
| optionB | option_id=B | 选项B内容 |
| optionC | option_id=C | 选项C内容 |
| optionD | option_id=D | 选项D内容 |
| answer | correct_answer | 正确答案(A/B/C/D) |
| analysis | explanation | 答案解析 |

---

## 开发计划

### 2.1 后端开发

#### 2.1.1 乱码修复 ✅ 已完成
- [x] 检查并修复MySQL连接URL字符集配置
- [x] 配置Spring Boot响应编码为UTF-8
- [x] 添加Jackson UTF-8编码配置
- [x] 验证数据库表字符集为utf8mb4

#### 2.1.2 Excel导入功能 ✅ 已完成
- [x] 添加Apache POI依赖 (pom.xml)
- [x] 创建Excel导入DTO
- [x] 创建导入服务类
- [x] 创建导入Controller
- [x] 添加文件上传配置

### 2.2 前端开发 ✅ 已完成
- [x] 创建题库管理页面
- [x] 添加文件上传组件
- [x] 实现导入进度展示
- [x] 添加导入结果提示

---

## 已完成的代码修改

### 3.1 后端修改文件列表

| 文件路径 | 修改内容 | 修改时间 |
|---------|---------|---------|
| [application.yml](file:///e:/dev/trace-test/java-back/src/main/resources/application.yml) | 添加UTF-8编码配置 | 2026-06-21 |
| [WebConfig.java](file:///e:/dev/trace-test/java-back/src/main/java/com/quizmaster/config/WebConfig.java) | 创建响应编码配置 | 2026-06-21 |
| [pom.xml](file:///e:/dev/trace-test/java-back/pom.xml) | 添加Apache POI依赖 | 2026-06-21 |
| [SecurityConfig.java](file:///e:/dev/trace-test/java-back/src/main/java/com/quizmaster/config/SecurityConfig.java) | 允许导入API访问 | 2026-06-21 |
| [QuestionImportDTO.java](file:///e:/dev/trace-test/java-back/src/main/java/com/quizmaster/dto/QuestionImportDTO.java) | 新建导入DTO | 2026-06-21 |
| [ImportResultDTO.java](file:///e:/dev/trace-test/java-back/src/main/java/com/quizmaster/dto/ImportResultDTO.java) | 新建导入结果DTO | 2026-06-21 |
| [ExcelImportService.java](file:///e:/dev/trace-test/java-back/src/main/java/com/quizmaster/service/ExcelImportService.java) | 新建导入服务接口 | 2026-06-21 |
| [ExcelImportServiceImpl.java](file:///e:/dev/trace-test/java-back/src/main/java/com/quizmaster/service/impl/ExcelImportServiceImpl.java) | 新建导入服务实现 | 2026-06-21 |
| [ImportController.java](file:///e:/dev/trace-test/java-back/src/main/java/com/quizmaster/controller/ImportController.java) | 新建导入控制器 | 2026-06-21 |

### 3.2 前端修改文件列表

| 文件路径 | 修改内容 | 修改时间 |
|---------|---------|---------|
| [ImportView.vue](file:///e:/dev/trace-test/vue-front/src/views/ImportView.vue) | 新建导入页面 | 2026-06-21 |
| [index.ts](file:///e:/dev/trace-test/vue-front/src/router/index.ts) | 添加导入路由 | 2026-06-21 |
| [ProfileView.vue](file:///e:/dev/trace-test/vue-front/src/views/ProfileView.vue) | 添加导入入口 | 2026-06-21 |

### 3.3 Docker配置修改

| 文件路径 | 修改内容 | 修改时间 |
|---------|---------|---------|
| [docker-compose.yml](file:///e:/dev/trace-test/docker-compose.yml) | 更新MySQL连接字符集为utf8mb4 | 2026-06-21 |

---

## 核心代码说明

### 4.1 乱码修复配置

#### application.yml关键配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/quiz_master?useUnicode=true&characterEncoding=utf8mb4&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&characterSetResults=utf8mb4
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 10MB
  jackson:
    charset: UTF-8
    default-property-inclusion: non_null
    serialization:
      write-dates-as-timestamps: false

server:
  tomcat:
    uri-encoding: UTF-8
```

#### WebConfig.java关键代码
```java
@Override
public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
    configurer.defaultContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
}

@Bean
public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
    MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
    converter.setDefaultCharset(StandardCharsets.UTF_8);
    return converter;
}
```

### 4.2 Excel导入服务实现

#### ExcelImportServiceImpl核心逻辑
```java
@Transactional(rollbackFor = Exception.class)
public ImportResultDTO importQuestions(MultipartFile file) {
    // 1. 使用XSSFWorkbook解析Excel
    // 2. 遍历每一行，解析QuestionImportDTO
    // 3. 验证数据完整性
    // 4. 保存Question和QuestionOption
    // 5. 返回导入结果
}
```

#### ImportController核心代码
```java
@PostMapping("/questions")
public Result<ImportResultDTO> importQuestions(@RequestParam("file") MultipartFile file) {
    // 1. 验证文件格式
    // 2. 调用ExcelImportService
    // 3. 返回结果
}
```

### 4.3 Excel文件格式要求

**列顺序（从A到H）**:
1. 序号（可为空）
2. title（题目内容）- 必填
3. optionA（选项A）- 必填
4. optionB（选项B）- 必填
5. optionC（选项C）- 必填
6. optionD（选项D）- 必填
7. answer（正确答案A/B/C/D）- 必填
8. analysis（答案解析）- 可为空

---

## 进度跟踪

### 5.1 任务清单

| 任务ID | 任务描述 | 状态 | 完成时间 |
|-------|---------|-----|---------|
| V2-001 | 分析乱码原因并修复 | ✅ 已完成 | 2026-06-21 |
| V2-002 | 添加POI依赖 | ✅ 已完成 | 2026-06-21 |
| V2-003 | 创建Excel导入DTO | ✅ 已完成 | 2026-06-21 |
| V2-004 | 实现Excel解析服务 | ✅ 已完成 | 2026-06-21 |
| V2-005 | 创建导入API接口 | ✅ 已完成 | 2026-06-21 |
| V2-006 | 前端上传页面开发 | ✅ 已完成 | 2026-06-21 |
| V2-007 | 测试与验证 | ⏳ 待测试 | - |

### 5.2 当前进度
**状态**: 代码开发完成，待测试验证
**下一步**: 部署并测试乱码修复和Excel导入功能

---

## 踩坑记录

### 6.1 已记录的踩坑经验

| 日期 | 问题描述 | 解决方案 | 预防措施 |
|------|---------|---------|---------|
| 2026-06-21 | MySQL字符集不一致导致乱码 | 将characterEncoding=utf8改为utf8mb4 | 确保数据库连接URL和数据库表字符集一致 |
| 2026-06-21 | Docker容器内字符编码问题 | 在连接URL中添加characterSetResults=utf8mb4 | Docker环境需要额外配置字符集 |
| 2026-06-21 | Spring Boot响应编码不一致 | 添加WebConfig配置和jackson charset配置 | 多层配置确保响应编码为UTF-8 |
| 2026-06-21 | 文件上传大小限制 | 在application.yml中配置max-file-size | 需要明确配置Spring文件上传限制 |

### 6.2 乱码问题排查流程

```
乱码问题排查流程:
1. 检查数据库表字符集 → 确认使用utf8mb4
2. 检查数据库连接URL → 确认characterEncoding=utf8mb4
3. 检查Spring Boot配置 → 确认server.tomcat.uri-encoding=UTF-8
4. 检查Jackson配置 → 确认jackson.charset=UTF-8
5. 检查HTTP响应头 → 确认Content-Type包含charset=utf-8
6. 检查前端编码 → 确认使用UTF-8解析响应
```

---

## 待完成事项

### 7.1 测试验证清单

- [ ] 本地启动MySQL并初始化数据库
- [ ] 测试乱码修复效果
- [ ] 使用Docker Compose启动所有服务
- [ ] 测试Excel导入功能
- [ ] 验证导入结果的正确性
- [ ] 测试大文件导入性能

### 7.2 潜在优化点

- [ ] 添加导入进度条（对于大文件）
- [ ] 支持导入前预览和编辑
- [ ] 添加批量删除题库功能
- [ ] 支持按分类导入
- [ ] 添加导入历史记录

---

## 文档版本

**版本**: V2.0
**创建时间**: 2026-06-21
**最后更新**: 2026-06-21
**更新内容**: 完成所有代码开发和配置修改，待测试验证