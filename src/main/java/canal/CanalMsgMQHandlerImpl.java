package canal;

import com.alibaba.fastjson.JSON;
import rabbitmq.MessageSender;

/**
 * 利用MQ处理canal消息处理
 *
 * @author bug1024
 * @date 2017-03-25
 */
public class CanalMsgMQHandlerImpl implements CanalMsgHandler {

    private MessageSender messageSender;

    public CanalMsgMQHandlerImpl(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public Boolean handle(CanalMsg canalMsg) {
        System.out.println(JSON.toJSONString(canalMsg.getMsg()));
        //messageSender.sendMessage(canalMsg.getKey(), canalMsg.getMsg());
        messageSender.sendMessage("canal", canalMsg.getMsg());
        return null;
    }

}
