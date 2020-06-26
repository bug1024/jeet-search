package com.bug1024.jeetsearch.canal;

/**
 * canal消息处理
 *
 * @author bug1024
 * @date 2017-03-25
 */
public interface CanalMsgHandler {

    Boolean handle(CanalMsg canalMsg);

}
