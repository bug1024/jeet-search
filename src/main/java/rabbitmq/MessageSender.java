package rabbitmq;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 发送消息
 *
 * @author bug1024
 * @date 2017-03-26
 */
@Service
public class MessageSender {

    private static Logger logger = LoggerFactory.getLogger(MessageSender.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private AmqpTemplate amqpTemplate;

    public Boolean sendMessage(String routingKey, Object message){
        try {
            String msg = objectMapper.writeValueAsString(message);
            amqpTemplate.convertAndSend(routingKey, msg);
            return true;
        } catch (JsonProcessingException e) {
            logger.warn("json encode failed", e);
            return false;
        } catch (Exception e) {
            logger.warn("send to mq failed", e);
            return false;
        }
    }

}
