# 后端任务微规格

**任务ID**: T001-Backend
**模块**: [模块名称]
**功能**: [功能描述]
**优先级**: P0 / P1 / P2
**预估时间**: XX分钟

---

## 任务目标

[用一句话描述这个后端任务要实现什么]

---

## 上下文

### 相关设计
- 设计文档: `01-design-final.md`
- API设计: [API文档链接或在设计文档中的章节]
- 数据库设计: [数据库设计文档或ER图]

### 依赖关系
- 依赖的其他服务: [列出依赖的微服务或外部API]
- 依赖的数据表: [列出需要读写的数据表]
- 依赖的中间件: [Redis、MQ等]

### 技术栈
- 语言: Java 17 / Python 3.11 / Node.js / [其他]
- 框架: Spring Boot 3.x / FastAPI / Express / [其他]
- ORM: MyBatis / Hibernate / SQLAlchemy / Prisma / [其他]
- 数据库: MySQL 8.0 / PostgreSQL / MongoDB / [其他]

---

## 需要实现的内容

### 1. API端点

#### API 1: 创建用户
**端点**: `POST /api/users`
**用途**: 创建新用户

**请求体**:
```json
{
  "username": "string (3-20字符)",
  "email": "string (邮箱格式)",
  "password": "string (8-20字符)",
  "role": "string (可选, 默认'user')"
}
```

**响应**:
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": "123",
    "username": "john_doe",
    "email": "john@example.com",
    "role": "user",
    "createdAt": "2025-11-30T10:00:00Z"
  }
}
```

**错误响应**:
```json
{
  "code": 400,
  "message": "用户名已存在",
  "errors": {
    "username": ["用户名已被使用"]
  }
}
```

**状态码**:
- 200: 创建成功
- 400: 请求参数错误
- 409: 用户名或邮箱已存在
- 500: 服务器错误

#### API 2: [API名称]
[同上格式]

### 2. 数据库操作

#### 表结构（如需创建新表）
**表名**: `users`

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(20) NOT NULL UNIQUE,
  email VARCHAR(100) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role VARCHAR(20) NOT NULL DEFAULT 'user',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  deleted_at TIMESTAMP NULL,
  INDEX idx_username (username),
  INDEX idx_email (email),
  INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

#### 数据库迁移脚本
**文件**: `migrations/V001__create_users_table.sql`
**说明**: 创建users表及相关索引

#### 查询语句
**查询1**: 根据用户名查找用户
```sql
SELECT id, username, email, role, created_at
FROM users
WHERE username = ? AND deleted_at IS NULL
LIMIT 1;
```

**查询2**: 分页获取用户列表
```sql
SELECT id, username, email, role, created_at
FROM users
WHERE deleted_at IS NULL
ORDER BY created_at DESC
LIMIT ? OFFSET ?;
```

**性能考虑**:
- [ ] username和email字段已有索引
- [ ] 使用LIMIT避免全表扫描
- [ ] 避免SELECT *，只查询需要的字段

### 3. 业务逻辑

#### Service层
**类名**: `UserService`
**文件**: `src/main/java/com/xxx/service/UserService.java`

**方法清单**:
```java
public interface UserService {
    /**
     * 创建用户
     * @param dto 用户创建DTO
     * @return 创建的用户
     * @throws DuplicateUserException 用户名或邮箱已存在
     * @throws ValidationException 参数验证失败
     */
    UserVO createUser(CreateUserDTO dto);

    /**
     * 根据ID获取用户
     * @param id 用户ID
     * @return 用户信息
     * @throws UserNotFoundException 用户不存在
     */
    UserVO getUserById(Long id);

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param dto 更新DTO
     * @return 更新后的用户
     */
    UserVO updateUser(Long id, UpdateUserDTO dto);

    /**
     * 删除用户（软删除）
     * @param id 用户ID
     */
    void deleteUser(Long id);
}
```

**核心业务逻辑**:
1. **创建用户流程**:
   - 验证输入参数（用户名、邮箱、密码格式）
   - 检查用户名是否已存在
   - 检查邮箱是否已存在
   - 密码加密（使用BCrypt）
   - 保存到数据库
   - 返回用户信息（不包含密码）

2. **密码加密逻辑**:
   ```java
   String hashedPassword = BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
   ```

3. **重复检查逻辑**:
   ```java
   if (userRepository.existsByUsername(username)) {
       throw new DuplicateUserException("用户名已存在");
   }
   if (userRepository.existsByEmail(email)) {
       throw new DuplicateUserException("邮箱已存在");
   }
   ```

#### Repository/DAO层
**接口名**: `UserRepository`
**文件**: `src/main/java/com/xxx/repository/UserRepository.java`

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

### 4. 数据模型

#### Entity（数据库实体）
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String username;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role = "user";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Getters and Setters
}
```

#### DTO（数据传输对象）
```java
// 创建用户DTO
public class CreateUserDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度必须在3-20之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在8-20之间")
    private String password;

    private String role;

    // Getters and Setters
}

// 用户视图对象
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private LocalDateTime createdAt;

    // Getters and Setters
}
```

### 5. 参数验证

**验证规则**:
- 用户名: 3-20字符，只能包含字母、数字、下划线
- 邮箱: 符合邮箱格式
- 密码: 8-20字符，必须包含字母和数字
- 角色: 只能是 user, admin, moderator 之一

**验证时机**:
- Controller层: 使用`@Valid`注解进行基本验证
- Service层: 进行业务逻辑验证（如重复检查）

### 6. 异常处理

**自定义异常**:
```java
public class DuplicateUserException extends BusinessException {
    public DuplicateUserException(String message) {
        super(409, message);
    }
}

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException(String message) {
        super(404, message);
    }
}
```

**全局异常处理器**:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateUser(DuplicateUserException e) {
        return ResponseEntity.status(409)
            .body(new ErrorResponse(409, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, List<String>> errors = extractFieldErrors(e);
        return ResponseEntity.status(400)
            .body(new ErrorResponse(400, "参数验证失败", errors));
    }
}
```

### 7. 安全考虑

**必须实现的安全措施**:
- [x] 密码使用BCrypt加密，不存储明文
- [x] SQL参数化查询，防止SQL注入
- [x] 输入验证，防止XSS
- [x] 敏感信息（密码）不在日志中输出
- [x] 敏感信息（密码）不在响应中返回

**认证授权**（如需要）:
- [ ] 需要登录认证
- [ ] 需要角色权限检查
- [ ] 需要CSRF防护

---

## 约束条件

### 必须遵守
- [x] 遵守项目编码规范（CLAUDE.md）
- [x] 使用UTF-8编码（无BOM）
- [x] 所有public方法必须有JavaDoc注释
- [x] 复杂业务逻辑必须有行内注释
- [x] 异常必须有明确的错误信息
- [x] 日志记录关键操作（INFO级别）和异常（ERROR级别）

### 禁止操作
- [ ] 不使用SELECT *查询
- [ ] 不在循环中执行SQL查询（N+1问题）
- [ ] 不使用字符串拼接构造SQL（必须用参数化）
- [ ] 不在生产环境输出完整的异常堆栈到响应
- [ ] 不存储明文密码
- [ ] 不修改已有API的签名（除非明确允许）

### 性能要求
- [ ] 数据库查询使用索引
- [ ] 批量操作使用批处理（batch）
- [ ] 频繁查询的数据使用缓存（如需要）
- [ ] API响应时间 < 200ms（P95）

---

## 验收标准

### 功能验收
- [ ] 所有API端点可正常访问
- [ ] 参数验证正确（测试各种边界条件）
- [ ] 错误处理正确（测试各种错误场景）
- [ ] 业务逻辑正确（重复检查、数据完整性等）
- [ ] 数据正确保存到数据库

### API测试
```bash
# 创建用户 - 成功
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }'

# 预期: 200, 返回用户信息

# 创建用户 - 重复用户名
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "another@example.com",
    "password": "password123"
  }'

# 预期: 409, 返回"用户名已存在"

# 创建用户 - 参数验证失败
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "email": "invalid-email",
    "password": "123"
  }'

# 预期: 400, 返回验证错误详情
```

### 单元测试
**测试文件**: `src/test/java/com/xxx/service/UserServiceTest.java`

**测试用例**:
- [ ] testCreateUser_Success - 正常创建用户
- [ ] testCreateUser_DuplicateUsername - 用户名重复
- [ ] testCreateUser_DuplicateEmail - 邮箱重复
- [ ] testCreateUser_InvalidUsername - 用户名格式错误
- [ ] testGetUserById_Success - 正常获取用户
- [ ] testGetUserById_NotFound - 用户不存在
- [ ] testUpdateUser_Success - 正常更新用户
- [ ] testDeleteUser_Success - 正常删除用户

**测试覆盖率要求**: > 85%

### 集成测试
**测试文件**: `src/test/java/com/xxx/controller/UserControllerIntegrationTest.java`

**测试内容**:
- [ ] API端点完整流程测试
- [ ] 数据库事务测试
- [ ] 异常场景测试

### 数据库验证
```sql
-- 验证用户已创建
SELECT * FROM users WHERE username = 'john_doe';

-- 验证密码已加密（不是明文）
SELECT password_hash FROM users WHERE username = 'john_doe';
-- 预期: password_hash应该是BCrypt格式的哈希值，不是"password123"

-- 验证索引已创建
SHOW INDEX FROM users;
```

### 自动化验证命令
```bash
# 编译检查
mvn clean compile

# 单元测试
mvn test

# 集成测试
mvn verify -P integration-test

# 代码覆盖率
mvn jacoco:report

# 代码规范检查
mvn checkstyle:check

# 所有检查（一键执行）
mvn clean verify
```

---

## 测试要求

### 单元测试
使用Mockito模拟依赖：
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateUser_Success() {
        // Given
        CreateUserDTO dto = new CreateUserDTO();
        dto.setUsername("john_doe");
        dto.setEmail("john@example.com");
        dto.setPassword("password123");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // When
        UserVO result = userService.createUser(dto);

        // Then
        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
        verify(userRepository).save(any(User.class));
    }
}
```

### 集成测试
使用Testcontainers进行真实数据库测试（推荐）

---

## 参考文件

### 需要阅读的文件
- `CLAUDE.md` - 项目开发规范
- `src/main/java/com/xxx/config/SecurityConfig.java` - 安全配置
- `src/main/java/com/xxx/common/BaseEntity.java` - 实体基类
- `src/main/java/com/xxx/exception/BusinessException.java` - 异常基类

### 可参考的类似实现
- `src/main/java/com/xxx/controller/ProductController.java` - 类似的Controller实现
- `src/main/java/com/xxx/service/impl/ProductServiceImpl.java` - 类似的Service实现

---

## 实现检查清单

### 开始前
- [ ] 阅读相关设计文档
- [ ] 确认数据库表结构设计正确
- [ ] 确认API设计符合RESTful规范
- [ ] 拉取最新代码

### 实现中
- [ ] 创建Entity、DTO、VO类
- [ ] 创建Repository接口
- [ ] 实现Service层业务逻辑
- [ ] 实现Controller层API
- [ ] 添加参数验证
- [ ] 添加异常处理
- [ ] 编写单元测试
- [ ] 编写集成测试

### 完成后
- [ ] 代码自检（遵守规范）
- [ ] 运行所有验收命令
- [ ] 使用Postman/curl手工测试API
- [ ] 检查数据库数据正确性
- [ ] 检查日志输出
- [ ] 生成实现报告（04-implementation.md）

---

## 预期输出文件

### 新增文件
- `src/main/java/com/xxx/entity/User.java` - 实体类
- `src/main/java/com/xxx/dto/CreateUserDTO.java` - DTO
- `src/main/java/com/xxx/dto/UserVO.java` - VO
- `src/main/java/com/xxx/repository/UserRepository.java` - Repository
- `src/main/java/com/xxx/service/UserService.java` - Service接口
- `src/main/java/com/xxx/service/impl/UserServiceImpl.java` - Service实现
- `src/main/java/com/xxx/controller/UserController.java` - Controller
- `src/test/java/com/xxx/service/UserServiceTest.java` - Service单元测试
- `src/test/java/com/xxx/controller/UserControllerTest.java` - Controller测试
- `migrations/V001__create_users_table.sql` - 数据库迁移脚本

### 修改文件
- `src/main/resources/application.yml` - 如需添加配置

---

## 日志要求

### 日志级别使用
```java
// INFO: 记录关键业务操作
log.info("用户创建成功: username={}, id={}", username, userId);

// WARN: 记录异常但不影响主流程的情况
log.warn("发送欢迎邮件失败: userId={}, error={}", userId, e.getMessage());

// ERROR: 记录异常错误
log.error("创建用户失败: username={}", username, e);
```

### 禁止记录敏感信息
```java
// ❌ 错误示例
log.info("用户登录: username={}, password={}", username, password);

// ✅ 正确示例
log.info("用户登录: username={}", username);
```

---

## 备注

[任何需要特别说明的内容]

---

**创建时间**: YYYY-MM-DD HH:mm:ss
**创建者**: Claude Code
**任务拆解来源**: 02-tasks.md
