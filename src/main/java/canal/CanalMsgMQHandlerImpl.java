package canal;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rabbitmq.MessageReceiver;
import rabbitmq.MessageSender;

/**
 * 利用MQ处理canal消息处理
 *
 * @author bug1024
 * @date 2017-03-25
 */
public class CanalMsgMQHandlerImpl implements CanalMsgHandler {

    private static Logger logger = LoggerFactory.getLogger(CanalMsgMQHandlerImpl.class);

    private MessageSender messageSender;

    public CanalMsgMQHandlerImpl(MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public Boolean handle(CanalMsg canalMsg) {
        return messageSender.sendMessage(canalMsg.getKey(), canalMsg.getMsg());
    }

}
