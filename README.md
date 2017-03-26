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

## MySQL配置
 修改my.cnf，开启binlog
```
    [mysqld]
    log-bin=mysql-bin #添加这一行就ok
    binlog-format=ROW #选择row模式
    server_id=1 #配置mysql replaction需要定义，不能和canal的slaveId重复
```
 新建canal用户并分配权限
```sql
    CREATE USER canal IDENTIFIED BY 'canal';
    GRANT SELECT, REPLICATION SLAVE, REPLICATION CLIENT ON *.* TO 'canal'@'%';
    -- GRANT ALL PRIVILEGES ON *.* TO 'canal'@'%' ;
    FLUSH PRIVILEGES;
```

## canal配置
 修改instance.properties，更多配置[戳此](https://github.com/alibaba/canal/wiki/AdminGuide)
```
    ## mysql serverId
    canal.instance.mysql.slaveId = 1234

    # position info，需要改成自己的数据库信息
    canal.instance.master.address = 127.0.0.1:3306
    canal.instance.master.journal.name =
    canal.instance.master.position =
    canal.instance.master.timestamp =

    #canal.instance.standby.address =
    #canal.instance.standby.journal.name =
    #canal.instance.standby.position =
    #canal.instance.standby.timestamp =

    # username/password，需要改成自己的数据库信息
    canal.instance.dbUsername = canal
    canal.instance.dbPassword = canal
    canal.instance.defaultDatabaseName =
    canal.instance.connectionCharset = UTF-8

    # table regex
    canal.instance.filter.regex = .*\\..*
```

## Changelog
 - 2017.3.18
  * 启动项目
  * 搭建canal，rabbitmq，es环境
 - 2017.3.19
  * 实现canal和rabbitmq部分逻辑
 - 2017.3.25
  * 引入Spring框架重构代码
