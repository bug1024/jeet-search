package com.bug1024.jeetsearch.mq.kafka;

import com.bug1024.jeetsearch.canal.CanalMsg;
import com.bug1024.jeetsearch.canal.CanalMsgHandler;
import com.bug1024.jeetsearch.mq.MessageSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 利用MQ处理canal消息处理
 *
 * @author bug1024
 * @date 2017-03-25
 */
@Service
public class CanalMsgToKafkaHandler implements CanalMsgHandler {

    @Resource(name = "kafkaMessageSender")
    private MessageSender messageSender;

    @Override
    public void handle(CanalMsg canalMsg) {
        messageSender.send(canalMsg);
    }

}
