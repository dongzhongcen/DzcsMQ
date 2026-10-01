# DzcsMQ

<p align="center">
  <img alt="Java" src="https://img.shields.io/badge/java-17%2B-orange">
  <img alt="Spring Boot" src="https://img.shields.io/badge/spring--boot-4.x-brightgreen">
  <img alt="MyBatis" src="https://img.shields.io/badge/mybatis-3.x-blue">
  <img alt="SQLite" src="https://img.shields.io/badge/sqlite-3.x-lightgrey">
</p>

DzcsMQ 是一个用于学习和实践消息队列核心机制的 Java 项目。项目目前实现了 MQ 服务端的基础数据结构、元数据持久化、消息文件持久化、内存数据管理、虚拟主机核心 API，以及简单的客户端/服务端网络通信流程。

## 功能特性

- **元数据管理**：支持交换机、队列、绑定关系的 SQLite 持久化。
- **消息持久化**：支持按队列创建消息文件、写入消息、删除消息标记、加载消息和垃圾回收。
- **内存数据中心**：维护交换机、队列、绑定、消息和未确认消息的运行时状态。
- **虚拟主机 API**：支持 `exchangeDeclare`、`queueDeclare`、`queueBind`、`basicPublish`、`basicAck` 等核心操作。
- **转发规则**：支持 direct、fanout、topic 三类交换机的基础路由逻辑。
- **网络通信骨架**：支持客户端连接服务端、创建 channel，并通过请求/响应调用核心 API。
- **测试覆盖**：包含 Mapper、DataBaseManager、客户端/服务端基础调用流程测试。

## 项目结构

```text
src/main/java/org/example/dzcsmq
├── common/              # 客户端和服务端共享的请求、响应和传输工具
├── mqclient/            # 客户端 Connection、Channel
├── mqserver/            # BrokerServer、ServerChannel
│   ├── core/            # MQ 核心实体、VirtualHost、消费者管理
│   ├── datacenter/      # 数据库、磁盘文件、内存数据管理
│   └── mapper/          # MyBatis Mapper 和初始化逻辑
└── DzcsmqApplication.java
```

## 快速开始

### 环境要求

- JDK 17 或更高版本
- Maven Wrapper（项目已包含 `mvnw` / `mvnw.cmd`）

### 编译

```bash
./mvnw -q -DskipTests compile
```

Windows：

```powershell
.\mvnw.cmd -q -DskipTests compile
```

### 运行测试

```bash
./mvnw -q test
```

Windows：

```powershell
.\mvnw.cmd -q test
```

## 当前状态

项目仍处于学习和迭代阶段，已完成基础 MQ 模型、持久化和通信骨架。后续可继续完善：

- 更完整的协议编解码和异常响应
- 消费者异步推送与确认机制
- 持久化恢复流程
- Broker 启动参数和配置化
- 更系统的单元测试和集成测试

## 数据和敏感信息

项目运行产生的数据库、消息文件、构建产物、IDE 配置和本地敏感配置已通过 `.gitignore` 排除，不应提交到仓库。