# 开发日志

## 2026-09-09 初始提交

提交：9fd9a69

完成内容：
- 搭建 Spring Boot 项目基础结构
- 新增 MQ 服务核心实体，包括消息、队列、交换机、绑定和消息属性
- 配置 GitHub 远端仓库：https://github.com/dongzhongcen/eazymq.git
- 推送 master 分支到远端仓库

验证结果：
- Maven 测试通过
- 当前本地 master 分支已跟踪 origin/master

## 2026-09-09 元数据表初始化

提交：本次提交

完成内容：
- 新增 MetaMapper 建表接口和 Mapper XML
- 新增 exchange_table、queue_table、binding_table 三张 SQLite 元数据表
- 新增启动初始化逻辑，应用启动时自动创建元数据表
- 配置 SQLite 驱动和 MyBatis Mapper 加载路径
- 将本地 SQLite 数据目录 date/ 加入忽略规则

验证结果：
- Maven 测试通过
- 启动测试过程中已生成 date/meta.db
