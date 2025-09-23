# 智小派 - 基于Spring AI Alibaba的企业级智能客服系统

## 项目简介

智小派（PiAI）是无限派科技有限公司基于Spring AI Alibaba技术栈打造的新一代企业级智能客服系统。该系统深度融合了大语言模型与企业业务场景，为企业提供高效、智能的客户服务解决方案。

## 体验地址:
http://124.223.11.132:8081/
账号/密码(也可以自行注册)
zhangsan/123456
lisi/123456
wangwu/123456

登录后，新建会话，可以问AI:
> 测试AI会话记忆
你好，介绍下自己
我叫小明
我叫什么名字
> 测试RAG
有几款产品
CRM基础版多少钱
> 测试AI调用业务工具tools
帮我下单CRM高级版
我有哪些订单

## 核心功能

### 🤖 智能对话引擎
- **AI客服助手**：基于阿里云DashScope（通义千问）大模型构建的智能客服"小派"
- **流式响应**：支持实时流式对话输出，提供类人交互体验
- **多轮对话记忆**：自研基于MongoDB的对话历史持久化存储机制，突破Spring AI默认内存存储限制

### 📚 知识增强检索（RAG）
- **企业知识库**：集成公司基础信息、产品详情及FAQ文档
- **智能检索**：结合向量检索技术，实现精准的知识匹配与回答

### ⚙️ 高级特性
- **Function Calling**：支持searXNG联网搜索及未来扩展的企业内部工具调用（如订单查询、下单等）
- **向量存储**：基于Redis-Stack构建高性能向量数据库
- **全链路持久化**：采用MongoDB存储对话历史，MySQL存储业务数据

## 技术架构

### 核心技术栈
| 技术组件 | 版本 |
|---------|------|
| JDK | 21 |
| Spring Boot | 3.4.0 |
| Spring AI | 1.0.0 |
| Spring AI Alibaba | 1.0.0.3 |
| 数据库 | MySQL 8.0+, MongoDB 6.0+, Redis-Stack 7.0+ |

### 系统架构图
```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   前端应用       │───▶│   派小智后端       │───▶│  阿里云通义千问   │
│  (React/Vue)    │    │ (Spring AI)      │    │   (Qwen)        │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐
│   MongoDB       │   │   MySQL         │   │ Redis-Stack     │
│ 对话历史存储      │   │ 业务数据存储      │   │ 向量数据库        │
└─────────────────┘   └─────────────────┘   └─────────────────┘
```

## 快速开始
### 中间件redis-stack的docker命令
```sh
docker run -d --name redis-stack \
--restart=always  \
-v $(pwd)/redis-data:/data \
-p 9379:6379 \
-e REDIS_ARGS="--requirepass 123456" \
redis/redis-stack:latest
```
注意 为了不和本地的redis端口冲突，这里将redis-stack的端口映射到9379

### mongodb
```sh
# 创建数据持久化目录（避免容器重启数据丢失）
mkdir -p /mnt/mongodb/data 
```
```
docker run -d \
--name mongodb \
-p 27017:27017 \
-v $(pwd)/mongodb/data:/data/db \
mongo:latest
```
### mysql
```sh
docker run -d \
  --name mysql8 \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root123456 \
  -v $(pwd)/mysql/conf:/etc/mysql/conf.d \
  -v $(pwd)/mysql/data:/var/lib/mysql \
  --restart unless-stopped \
  mysql:8.0.20
```
```shell
# 进入容器命令
docker exec -it mysql8 mysql -uroot -p
```

### searxng 本地搜索工具
```
docker run -p 8088:8080 \
  --name searxng \
  -d --restart=always \
  -v $(pwd)/SearXNG:/etc/searxng \
  -e "BASE_URL=http://localhost:8080/" \
  -e "INSTANCE_NAME=searxng-instance" \
  searxng/searxng:latest
```
> 调整配置文件，搜索结果支持json返回：vim $(pwd)/SearXNG/settings.yml
```
  # formats: [html, csv, json, rss]
  formats:
    - html
    - json # 🔥新增支持json
```
访问： http://localhost:8080/ ,设置搜索引擎为 bing，souhu 等支撑国内的搜索引擎
测试访问: http://127.0.0.1:8088/search?q=SpringAI&format=json

### 环境要求
- JDK 21+
- Maven 3.8+
- Docker (可选，用于依赖服务)

### 启动步骤
1. 克隆后端项目：
```bash
git clone https://github.com/stef4java/pi-backend.git
```

2. 替换配置变量：
### 替换`application.yml`中的配置变量
```yml
  ai:
    dashscope:
      api-key: <your api key>

  data:
    redis:
      host: 127.0.0.1
      port: 9379
      # 在Redis 6.0+版本中，默认用户名是 default，所以URI格式为 redis://default:password@host:port
      username: default
      password: <your password>

    mongodb:
      uri: mongodb://127.0.0.1:27017/spring_ai

  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/spring_ai?useUnicode=true&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&allowMultiQueries=true&tinyInt1isBit=false&allowLoadLocalInfile=true&allowLocalInfile=true&allowUrl
    username: root
    password: <your password>
```
### 替换`redisson.yml`中的配置变量
```yml
# Redisson 单实例配置
singleServerConfig:
  # 🔥需要更改成你的配置
  address: "redis://127.0.0.1:9379"
  username: default
  password: 123456
```

### 前端项目
- GitHub地址：[https://github.com/stef4java/pi-ui](https://github.com/stef4java/pi-ui)

## 许可证
本项目采用Apache 2.0许可证，详情请参见[LICENSE](LICENSE)文件。

## 感谢：
> 迁移了很多[RuoYi-Vue-Plus]框架代码，感谢作者[RuoYi-Vue-Plus](https://github.com/dromara/RuoYi-Vue-Plus)
> [AI 零代码应用生成平台](https://github.com/liyupi/yu-ai-code-mother)
