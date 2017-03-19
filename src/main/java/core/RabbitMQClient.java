package core;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * RabbitMQ客户端，用于初始化连接
 *
 * @author wangyu@weidian.com
 * @date 2017-03-19
 */
public class RabbitMQClient {

    public Connection getConnection() throws IOException, TimeoutException {
        // @TODO 使用配置文件
        String userName = "guest";
        String password = "guest";
        String virtualHost = "/";
        String hostName = "127.0.0.1";
        int portNumber = 5672;

        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(userName);
        factory.setPassword(password);
        factory.setVirtualHost(virtualHost);
        factory.setHost(hostName);
        factory.setPort(portNumber);

        return factory.newConnection();
    }

}
