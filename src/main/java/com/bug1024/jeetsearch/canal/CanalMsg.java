package com.bug1024.jeetsearch.canal;

import lombok.Data;

import java.util.Date;

/**
 * canal消息
 *
 * @author bug1024
 * @date 2017-03-25
 */
@Data
public class CanalMsg {

    private Date receiveTime;

    private CanalMsgContent canalMsgContent;

    CanalMsg(CanalMsgContent canalMsgContent) {
        this.receiveTime = new Date();
        this.canalMsgContent = canalMsgContent;
    }

}
