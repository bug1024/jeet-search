package core;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;

/**
 * RabbitMQ消费者
 *
 * @author wangyu@weidian.com
 * @date 2017-03-19
 */
public class RabbitMQConsumer {

    public static void main(String[] args) {
        final Channel channel = RabbitMQClient.INSTANCE.getChannel();
        try {
            Boolean autoAck = false;
            channel.basicConsume("q-canal", autoAck, "myConsumerTag", new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    //String routingKey = envelope.getRoutingKey();
                    //String contentType = properties.getContentType();
                    long deliveryTag = envelope.getDeliveryTag();
                    String msg = new String(body, "UTF-8");
                    System.out.println(msg);
                    try {
                        channel.basicAck(deliveryTag, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}