package com.bug1024.jeetsearch.canal;

/**
 * canal消息处理
 *
 * @author bug1024
 * @date 2017-03-25
 */
public interface CanalMsgHandler {

    /**
     * 处理canal消息
     * @param canalMsg
     */
    void handle(CanalMsg canalMsg);

}
