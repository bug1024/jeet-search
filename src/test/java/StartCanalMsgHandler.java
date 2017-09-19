import canal.CanalMsgMQHandlerImpl;
import canal.CanalPool;
import canal.CanalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import rabbitmq.MessageSender;

/**
 * 启动canal消息处理程序
 *
 * @author bug1024
 * @date 2017-03-25
 */
public class StartCanalMsgHandler {

    private static Logger logger = LoggerFactory.getLogger(StartCanalMsgHandler.class);

    public static void main(String[] args) {
        try {
            ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext.xml");
            CanalService canalService = (CanalService) context.getBean("canalService");
            canalService.start();
        } catch (Exception e) {
            logger.warn("canal service error", e);
        }
    }

}
