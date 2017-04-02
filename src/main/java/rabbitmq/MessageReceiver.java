package rabbitmq;

import canal.CanalField;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import elasticsearch.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 接收消息
 *
 * @author bug1024
 * @date 2017-03-26
 */
@Service
public class MessageReceiver implements ChannelAwareMessageListener {

    private static Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            String msg = new String(message.getBody());
            JSONObject jsonObject = JSON.parseObject(msg);
            logger.info(jsonObject.get("dbName").toString());

            // false只确认当前一个消息收到，true确认所有consumer获得的消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            logger.warn("message parse failed");
            // ack返回false，并重新回到队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
        // 拒绝消息
        //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
    }

}
