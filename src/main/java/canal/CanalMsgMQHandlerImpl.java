package canal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rabbitmq.MessageSender;

/**
 * 利用MQ处理canal消息处理
 *
 * @author bug1024
 * @date 2017-03-25
 */
@Service
public class CanalMsgMQHandlerImpl implements CanalMsgHandler {

    private static Logger logger = LoggerFactory.getLogger(CanalMsgMQHandlerImpl.class);

    @Autowired
    private MessageSender messageSender;

    public Boolean handle(CanalMsg canalMsg) {
        return messageSender.sendMessage(canalMsg.getKey(), canalMsg.getCanalMsgContent());
    }

}
