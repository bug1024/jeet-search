# jeet-search
一套基于ES的搜索方案

## 环境
 - 系统环境：MacOS 10.12
 - 数据库：MySQL 5.7.16
 - ES版本：stable 5.2.2
 - binlog订阅消费组件：canal 4.2.12
 - 消息队列：RabbitMQ 3.6.6

## 原理
 - 业务方更新MySQL表记录
 - canal模拟mysql slave的交互协议，伪装自己为mysql slave，向mysql master发送dump协议
 - mysql master收到dump请求，开始推送binary log给slave(也就是canal)
 - canal解析binary log对象(原始为byte流)
 - canal客户端将解析后数据发送到到RabbitMQ
 - 业务方消费MQ，调用ES API更新索引，实现索引的实时更新


