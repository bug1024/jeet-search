package core;

import com.rabbitmq.client.*;

/**
 * RabbitMQ客户端，用于初始化连接
 *
 * @author wangyu@weidian.com
 * @date 2017-03-19
 */
public enum RabbitMQClient {

    INSTANCE;

    private Connection conn;

    private Channel channel;

    public Connection getConn() {
        return conn;
    }

    public Channel getChannel() {
        return channel;
    }

    RabbitMQClient() {
        //ResourceBundle conf = ResourceBundle.getBundle("src/main/resources/rabbitmq/rabbitmq");
        //System.out.println(conf.getString("host"));

        // @TODO 使用配置文件
        String userName = "guest";
        String password = "guest";
        String virtualHost = "/";
        String hostName = "127.0.0.1";
        int portNumber = 5672;
        String exchangeType = "direct";
        String exchangeName = "x-canal";
        String queueName = "q-canal";
        String routingKey = "user";

        try {

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUsername(userName);
            factory.setPassword(password);
            factory.setVirtualHost(virtualHost);
            factory.setHost(hostName);
            factory.setPort(portNumber);

            conn = factory.newConnection();
            channel = conn.createChannel();
            channel.exchangeDeclare(exchangeName, exchangeType, true);
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, exchangeName, routingKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publish(String msg) {
        String routingKey = "user";
        try {
            byte[] messageBodyBytes = msg.getBytes();
            channel.basicPublish("x-canal", routingKey, null, messageBodyBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
