package rabbitmq;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;

/**
 * 发送消息
 *
 * @author bug1024
 * @date 2017-03-26
 */
public class MessageSender {

    private static Logger logger = LoggerFactory.getLogger(MessageSender.class);

    private AmqpTemplate amqpTemplate;

    public MessageSender(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public Boolean sendMessage(String routingKey, Object message){
        try {
            String msg = JSON.toJSONString(message);
            amqpTemplate.convertAndSend(routingKey, msg);
            return true;
        } catch (Exception e) {
            logger.warn("send to mq failed");
            return false;
        }
    }

}
