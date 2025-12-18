# 编码指南 - 解决中文乱码

> **V4.0编码规范** - 确保所有文件UTF-8无BOM

---

## 问题背景

### 常见乱码场景

1. **Java代码中文注释乱码**
2. **前端页面中文显示乱码**
3. **数据库中文数据乱码**
4. **Git提交消息乱码**
5. **控制台输出乱码**

---

## 解决方案

### 1. 文件编码统一为UTF-8（无BOM）

#### 什么是BOM？

BOM（Byte Order Mark）是UTF-8文件开头的特殊标记字节（EF BB BF）。

- ✅ **UTF-8无BOM**：推荐，兼容性好
- ❌ **UTF-8有BOM**：可能导致问题（Java无法识别、脚本执行错误）

#### 检查文件编码

**Windows PowerShell**：
```powershell
Get-Content yourfile.java -Encoding UTF8
```

**Linux/Mac**：
```bash
file -bi yourfile.java
# 输出：text/x-java; charset=utf-8
```

#### 转换文件编码

**Visual Studio Code**：
1. 打开文件
2. 点击右下角编码（如"UTF-8 with BOM"）
3. 选择"Save with Encoding"
4. 选择"UTF-8"（无BOM）

**IntelliJ IDEA**：
1. File → Settings → Editor → File Encodings
2. 设置：
   - Global Encoding: UTF-8
   - Project Encoding: UTF-8
   - Default encoding for properties files: UTF-8
   - ✅ 勾选"Transparent native-to-ascii conversion"

**批量转换（Linux/Mac）**：
```bash
# 转换目录下所有Java文件为UTF-8无BOM
find . -name "*.java" -exec sh -c 'iconv -f UTF-8 -t UTF-8 "$1" -o "$1.tmp" && mv "$1.tmp" "$1"' _ {} \;
```

---

### 2. Java项目配置

#### Maven项目（pom.xml）

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
</properties>
```

#### Gradle项目（build.gradle）

```gradle
tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}
```

#### IDE配置（IntelliJ IDEA）

**File → Settings → Editor → File Encodings**：

```
Global Encoding: UTF-8
Project Encoding: UTF-8
Default encoding for properties files: UTF-8
☑ Transparent native-to-ascii conversion
```

**File → Settings → Build, Execution, Deployment → Compiler → Java Compiler**：

```
Additional command line parameters: -encoding UTF-8
```

---

### 3. Spring Boot配置

#### application.yml

```yaml
spring:
  # HTTP编码
  http:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

  # 消息源编码
  messages:
    encoding: UTF-8

  # Thymeleaf编码（如使用）
  thymeleaf:
    encoding: UTF-8
```

#### application.properties

```properties
spring.http.encoding.charset=UTF-8
spring.http.encoding.enabled=true
spring.http.encoding.force=true
spring.messages.encoding=UTF-8
```

---

### 4. 数据库编码

#### MySQL配置

**创建数据库时指定编码**：

```sql
CREATE DATABASE mydb
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;
```

**创建表时指定编码**：

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) NOT NULL,
    nickname VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

**修改现有数据库编码**：

```sql
ALTER DATABASE mydb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
ALTER TABLE users CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### JDBC连接字符串

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
```

或者：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=UTC
```

#### MyBatis-Plus配置

```yaml
mybatis-plus:
  configuration:
    # 日志输出
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      # 字段策略
      insert-strategy: not_empty
      update-strategy: not_empty
```

---

### 5. 前端配置

#### React项目

**index.html**：

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My App</title>
</head>
<body>
    <div id="root"></div>
</body>
</html>
```

**package.json**（确保构建时使用UTF-8）：

```json
{
  "scripts": {
    "start": "react-scripts start",
    "build": "react-scripts build"
  }
}
```

#### Vue项目

**vue.config.js**：

```javascript
module.exports = {
  chainWebpack: config => {
    config.plugin('html').tap(args => {
      args[0].meta = {
        charset: 'UTF-8'
      }
      return args
    })
  }
}
```

---

### 6. Git配置

#### Git Bash中文乱码

```bash
# 设置Git使用UTF-8
git config --global core.quotepath false
git config --global gui.encoding utf-8
git config --global i18n.commit.encoding utf-8
git config --global i18n.logoutputencoding utf-8

# Windows Git Bash
export LESSCHARSET=utf-8
```

#### .gitattributes文件

在项目根目录创建`.gitattributes`：

```
# 所有文本文件使用LF换行
* text=auto eol=lf

# Java源文件
*.java text eol=lf

# 前端文件
*.js text eol=lf
*.jsx text eol=lf
*.ts text eol=lf
*.tsx text eol=lf
*.css text eol=lf
*.scss text eol=lf
*.html text eol=lf
*.json text eol=lf

# 配置文件
*.yml text eol=lf
*.yaml text eol=lf
*.properties text eol=lf
*.xml text eol=lf

# 文档
*.md text eol=lf

# 二进制文件
*.jpg binary
*.png binary
*.gif binary
*.pdf binary
*.jar binary
```

---

### 7. 控制台输出

#### Windows命令提示符

```cmd
chcp 65001
```

将编码设置为UTF-8（65001）。

#### PowerShell

```powershell
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8
```

#### Java程序输出

```java
// 确保System.out使用UTF-8
System.setOut(new PrintStream(System.out, true, "UTF-8"));
```

---

## 测试验证

### 创建测试文件

**TestEncoding.java**：

```java
public class TestEncoding {
    public static void main(String[] args) {
        // 测试中文输出
        System.out.println("你好，世界！");
        System.out.println("Hello, 世界！");
        System.out.println("UTF-8编码测试：中文、English、日本語、한국어");

        // 测试中文注释
        String name = "张三"; // 姓名
        System.out.println("姓名：" + name);
    }
}
```

### 测试数据库

```java
@Test
public void testChinese() {
    User user = new User();
    user.setUsername("zhangsan");
    user.setNickname("张三");
    userMapper.insert(user);

    User saved = userMapper.selectById(user.getId());
    assertEquals("张三", saved.getNickname()); // 应该通过
}
```

---

## V4.0工作流中的应用

### Codex实现后端代码时

交接文档会包含：

```markdown
## 编码要求

- 所有Java文件：UTF-8无BOM
- 中文注释：正常使用
- pom.xml已配置UTF-8编码
- application.yml已配置UTF-8
- 数据库连接字符串已包含UTF-8参数

## 验证步骤

1. 创建包含中文的测试用例
2. 运行测试，确保中文正常显示
3. 检查数据库，确保中文正常存储
```

### Gemini实现前端代码时

交接文档会包含：

```markdown
## 编码要求

- 所有JS/JSX/TS/TSX文件：UTF-8无BOM
- index.html已设置<meta charset="UTF-8">
- 中文文案正常使用
- API响应中文正常显示

## 验证步骤

1. 页面显示中文文案
2. API返回中文数据正常显示
3. 表单提交中文数据正常
```

---

## 故障排查

### 问题1：Java控制台乱码

**症状**：`System.out.println("中文")` 显示乱码

**解决**：

1. IDE设置：File → Settings → Editor → File Encodings → UTF-8
2. 运行配置：Run → Edit Configurations → VM options: `-Dfile.encoding=UTF-8`
3. Maven/Gradle配置：添加UTF-8编码配置

### 问题2：数据库中文乱码

**症状**：插入中文后查询显示 `???` 或乱码

**解决**：

1. 检查数据库编码：`SHOW VARIABLES LIKE 'character%';`
2. 检查表编码：`SHOW CREATE TABLE users;`
3. 检查JDBC连接字符串
4. 如已有数据乱码，需要重新导入

### 问题3：Git提交消息乱码

**症状**：`git log` 显示提交消息乱码

**解决**：

```bash
git config --global i18n.commit.encoding utf-8
git config --global i18n.logoutputencoding utf-8
```

### 问题4：前端页面乱码

**症状**：浏览器显示中文为乱码

**解决**：

1. 检查HTML的`<meta charset="UTF-8">`
2. 检查HTTP响应头：`Content-Type: text/html; charset=UTF-8`
3. 检查文件本身是否UTF-8编码

---

## 检查清单

### 项目初始化时

```
[ ] IntelliJ IDEA设置为UTF-8
[ ] Maven pom.xml配置UTF-8
[ ] application.yml配置UTF-8
[ ] 数据库创建为utf8mb4
[ ] JDBC连接字符串包含UTF-8参数
[ ] .gitattributes文件已创建
[ ] Git配置UTF-8
[ ] 前端index.html设置UTF-8
```

### 每次提交代码前

```
[ ] 检查新增文件是UTF-8无BOM
[ ] 测试中文显示正常
[ ] 测试中文存储正常
[ ] Git提交消息使用中文正常
```

---

**✅ 遵循此指南，V4.0项目将不会出现任何中文乱码问题！**
