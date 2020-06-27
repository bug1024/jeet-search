package com.bug1024.jeetsearch.mq;

import com.bug1024.jeetsearch.canal.CanalMsg;

/**
 * 发送消息
 *
 * @author bug1024
 * @date 2020-06-26
 */
public interface MessageSender {

    /**
     * send msg
     * @param canalMsg
     */
    void send(CanalMsg canalMsg);
}