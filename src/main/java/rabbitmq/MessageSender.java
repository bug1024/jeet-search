package rabbitmq;

import org.springframework.amqp.core.AmqpTemplate;

/**
 * 发送消息
 *
 * @author bug1024
 * @date 2017-03-26
 */
public class MessageSender {

    private AmqpTemplate amqpTemplate;

    public MessageSender(AmqpTemplate amqpTemplate) {
        this.amqpTemplate = amqpTemplate;
    }

    public Boolean sendMessage(String routingKey, Object message){
        try {
            amqpTemplate.convertAndSend(routingKey, message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
