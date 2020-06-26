package com.bug1024.jeetsearch.mq.kafka;

import com.bug1024.jeetsearch.mq.MessageSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 发送消息
 *
 * @author bug1024
 * @date 2017-03-26
 */
@Service
@Slf4j
public class KafkaMessageSender implements MessageSender {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public Boolean sendMessage(String routingKey, Object message){
        try {
            String msg = MAPPER.writeValueAsString(message);
            log.info("send key:{} message:{}", routingKey, msg);
            return true;
        } catch (JsonProcessingException e) {
            log.warn("json encode failed", e);
            return false;
        }
    }

}
