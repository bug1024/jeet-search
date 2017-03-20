package core;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import org.apache.http.HttpHost;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.Collections;

/**
 * RabbitMQ消费者
 *
 * @author wangyu@weidian.com
 * @date 2017-03-19
 */
public class RabbitMQConsumer {

    public static void main(String[] args) {
        testES();
    }

    public static void testMQ() {
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

    public static void testES() {
        try {
            RestClient restClient = RestClient.builder(new HttpHost("localhost", 9200, "http")).build();
            Response response = restClient.performRequest("GET", "/",
                    Collections.singletonMap("pretty", "true"));

            System.out.println(EntityUtils.toString(response.getEntity()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}