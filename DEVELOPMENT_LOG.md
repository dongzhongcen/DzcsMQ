# 开发日志

## 2026-09-09 初始提交

提交：`9fd9a69`

### 完成内容

- 搭建 Spring Boot 项目基础结构。
- 新增 MQ 服务核心实体，包括消息、队列、交换机、绑定和消息属性。
- 配置 GitHub 远端仓库并推送初始 `master` 分支。

### 验证结果

- Maven 测试通过。
- 本地 `master` 分支已跟踪远端分支。

## 2026-09-09 元数据表初始化

提交：历史提交

### 完成内容

- 新增 `MetaMapper` 建表接口和 Mapper XML。
- 新增 `exchange_table`、`queue_table`、`binding_table` 三张 SQLite 元数据表。
- 新增启动初始化逻辑，应用启动时自动创建元数据表。
- 配置 SQLite 驱动和 MyBatis Mapper 加载路径。
- 将本地 SQLite 数据目录加入忽略规则。

### 验证结果

- Maven 测试通过。
- 启动测试过程中可生成本地元数据数据库。

## 2026-09-09 Mapper 增删操作实现

提交：历史提交

### 完成内容

- 实现 `exchange_table` 的 `insertExchange` 和 `deleteExchange`。
- 实现 `queue_table` 的 `insertQueue` 和 `deleteQueue`。
- 实现 `binding_table` 的 `insertBinding` 和 `deleteBinding`。
- 为 `Binding` 补充 getter/setter，保证 MyBatis 能读取绑定字段。
- 为 `deleteQueue` 参数补充 `@Param("queueName")`，保证 XML 参数名可用。
- 增加 Mapper 增删操作测试，覆盖三类元数据的插入和删除。

### 验证结果

- Maven 测试通过。
- Mapper XML 增删方法已完成实际数据库调用验证。

## 2026-10-01 元数据查询与 DataBaseManager 封装

提交：本次提交

### 完成内容

- 在 `MetaMapper.xml` 中实现 `selectAllExchanges`、`selectAllQueues`、`selectAllBindings`。
- 在 `MetaMapper` 中补充对应查询接口。
- 在 `DataBaseManager` 中封装元数据查询方法。
- 新增 `DataBaseManagerTests`，覆盖交换机、队列、绑定的插入、查询和删除流程。

### 验证结果

- `./mvnw -q -DskipTests compile` 通过。
- `./mvnw -q test` 通过。

## 2026-10-01 消息持久化、内存数据中心和虚拟主机

提交：本次提交

### 完成内容

- 新增 `MessageFileManager`，支持消息文件创建、写入、删除标记、加载和垃圾回收。
- 新增 `DiskDataCenter`，统一封装数据库和消息文件操作。
- 新增 `MemoryDataCenter`，管理交换机、队列、绑定、消息和未确认消息。
- 新增消费者相关类：`Consumer`、`ConsumerCallback`、`ConsumerManager`。
- 新增 `VirtualHost`，实现交换机、队列、绑定、发布、确认和 topic 路由等核心逻辑。
- 新增 `BrokerServer` 基础服务端骨架。

### 验证结果

- `./mvnw -q -DskipTests compile` 通过。
- `./mvnw -q test` 通过。

## 2026-10-01 客户端/服务端网络通信骨架

提交：本次提交

### 完成内容

- 新增通用请求/响应模型：`Request`、`Response`、`RequestType`、`ResponseCode`。
- 新增 `FrameIO`，封装对象流读写。
- 新增服务端 `ServerChannel`，将网络请求分发到虚拟主机核心 API。
- 完善 `BrokerServer`，支持连接处理、创建 channel、关闭 channel 和响应返回。
- 新增客户端 `Connection` 和 `Channel`，支持通过网络调用核心 MQ API。
- 新增 `ClientServerTests`，验证客户端创建 channel 并调用交换机、队列、绑定、发布和删除流程。

### 验证结果

- `./mvnw -q -DskipTests compile` 通过。
- `./mvnw -q test` 通过。
