package rabbitmq;

import canal.CanalChangeInfo;
import canal.CanalMsgContent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import elasticsearch.User;
import elasticsearch.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.stereotype.Service;
import utils.BeanUtil;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 接收消息
 *
 * @author bug1024
 * @date 2017-03-26
 */
@Service
public class MessageReceiver implements ChannelAwareMessageListener {

    private static Logger logger = LoggerFactory.getLogger(MessageReceiver.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private UserRepository repository;

    public void onMessage(Message message, Channel channel) throws Exception {
        try {
            String jsonString = new String(message.getBody());

            User user = convertJsonToUser(jsonString);

            logger.info("##build index##" + user.toString());
            repository.save(user);

            // false只确认当前一个消息收到，true确认所有consumer获得的消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            logger.warn("message consume failed: " + e.getMessage());
            // ack返回false，并重新回到队列
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
        // 拒绝消息
        //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
    }

    private User convertJsonToUser(String jsonString) throws Exception {
        CanalMsgContent content = null;
        try {
            content = objectMapper.readValue(jsonString, CanalMsgContent.class);
        } catch (Exception e) {
            logger.warn("json decode failed", e);
            throw e;
        }

        List<CanalChangeInfo> afterList = content.getDataAfter();
        Map map = new HashMap();
        for (CanalChangeInfo changeInfo : afterList) {
            map.put(changeInfo.getName(), changeInfo.getValue());
        }

        User user = new User();
        BeanUtil.transMap2Bean(map, user);

        return user;
    }


}
