package com.bug1024.jeetsearch.mq.kafka;

import com.bug1024.jeetsearch.canal.CanalMsg;
import com.bug1024.jeetsearch.mq.MessageSender;
import com.bug1024.jeetsearch.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 发送消息
 *
 * @author bug1024
 * @date 2017-03-26
 */
@Service
@Slf4j
public class KafkaMessageSender implements MessageSender {

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void send(CanalMsg canalMsg) {
        String msg = JsonUtil.toJson(canalMsg);
        log.info("send message:{}", msg);
        // TODO topic 策略
        kafkaTemplate.send("canal", msg);
    }

}
