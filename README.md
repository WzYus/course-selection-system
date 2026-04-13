# 🎓 高并发校园选课系统

一个基于 Spring Boot + Vue.js 的全栈 Web 应用，实现学生在线选课、退课、课表查询、成绩管理，教师端课程管理、成绩录入，管理员端用户与课程管理。系统采用 JWT 无状态认证、Redis 分布式锁、RabbitMQ 异步通知、Elasticsearch 全文检索，支持高并发选课场景下的数据一致性保障。

## ✨ 功能特性

### 👨‍🎓 学生端
- 个人信息维护（查看/修改资料、修改密码）
- 课程浏览（全文搜索 + 学分范围筛选）
- 选课 / 退课（分布式锁 + 乐观锁防超卖）
- 个人课表（自动解析上课时间，周历表格展示）
- 成绩查询（按年份筛选）

### 👩‍🏫 教师端
- 我的课程列表（分页）
- 查看选课学生名单（按年份、姓名筛选，分页）
- 在线录入/修改成绩（平时、期末，自动计算总评）
- 为学生选课（输入学号、课程编号）

### 👑 管理员端
- 用户管理（学生/教师/管理员增删改查，启用/禁用）
- 课程管理（增删改查）
- 选课记录查询（按课程、年份筛选）

### ⚙️ 技术亮点
- **高并发选课控制**：Redisson 分布式锁 + 数据库乐观锁，防止超卖
- **异步消息通知**：RabbitMQ 实现选课/退课/成绩更新后的邮件通知（QQ邮箱）
- **全文检索**：Elasticsearch + IK 分词器，支持课程名称、教师、描述关键词搜索
- **容器化部署**：Docker Compose 一键启动 MySQL、Redis、RabbitMQ、Elasticsearch
- **前后端分离**：RESTful API + Vue.js 响应式界面，Axios 统一错误拦截

## 🛠 技术栈

| 后端 | 前端 | 中间件/其他 |
|------|------|-------------|
| Spring Boot 4.0.3 | Vue 2 | MySQL 8.0 |
| Spring Security | Axios | Redis (Redisson) |
| JWT | HTML5/CSS3 | RabbitMQ |
| Spring Data JPA / Hibernate | | Elasticsearch 7.17 |
| Maven | | Docker Compose |

## 🚀 快速开始

### 前置要求
- JDK 21
- Docker & Docker Compose
- Maven（可选，可用项目中的 `mvnw`）

### 1. 克隆项目
```bash
git clone https://github.com/WzYus/course-selection-system.git
cd course-selection-system

### 2. 启动容器化中间件
```bash
cd D:\Docker   # 替换为你的 docker-compose.yml 所在目录
docker-compose up -d

等待 MySQL、Redis、RabbitMQ、Elasticsearch 启动完成（约30秒）。

3. 配置 application.yml
修改 spring.datasource.username/password 为你 MySQL 的账号密码。

修改 spring.mail.username/password 为你的 QQ 邮箱授权码。

修改 jwt.secret 为自定义密钥（至少32字符）。

4. 运行 Spring Boot 应用
bash
mvn spring-boot:run
或使用 IDE 运行 CsApplication.java。

5. 访问前端页面
登录注册页：http://localhost:8080/index.html

学生主页：http://localhost:8080/student.html

教师主页：http://localhost:8080/teacher.html

管理员主页：http://localhost:8080/admin.html

默认测试账号
角色	用户名	密码
管理员	admin	123456
学生	student1	123456
教师	teacher1	123456
注意：首次运行数据库为空，需要先注册学生/教师账号，或执行 src/main/resources/init.sql 初始化数据。

📦 项目结构
text
cs/
├── src/main/java/cn/detect/cs
│   ├── config          # 配置类（Security, Redis, RabbitMQ, ES）
│   ├── controller      # 控制器（REST API）
│   ├── service         # 业务逻辑
│   ├── repository      # 数据访问（JPA + Elasticsearch）
│   ├── entity          # JPA 实体
│   ├── document        # Elasticsearch 文档
│   ├── dto             # 数据传输对象
│   ├── security        # JWT 过滤器
│   ├── util            # 工具类
│   └── notification    # 消息队列消费者
├── src/main/resources
│   ├── application.yml # 配置文件
│   └── static          # 静态页面（HTML, CSS, JS）
└── pom.xml
📄 许可证
MIT License

🤝 贡献
欢迎提交 Issue 和 Pull Request。

📬 联系方式
作者：WzYus

邮箱：3220153787@qq.com

GitHub：WzYus