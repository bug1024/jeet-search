package rabbitmq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Service;

/**
 * 接收消息
 *
 * @author bug1024
 * @date 2017-03-26
 */
@Service
public class MessageReceiver implements ChannelAwareMessageListener {

    public void onMessage(Message message, Channel channel) throws Exception {
        System.out.println(message);

        // false只确认当前一个消息收到，true确认所有consumer获得的消息
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        // ack返回false，并重新回到队列
        //channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        // 拒绝消息
        //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
    }

}
