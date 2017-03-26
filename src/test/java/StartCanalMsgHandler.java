import canal.CanalMsgMQHandlerImpl;
import canal.CanalPool;
import canal.CanalService;
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

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:/spring/applicationContext.xml");

        CanalPool canalPool = new CanalPool();

        AmqpTemplate amqpTemplate = (AmqpTemplate) context.getBean("amqpTemplate");
        MessageSender messageSender = new MessageSender(amqpTemplate);
        CanalMsgMQHandlerImpl canalMsgHandler = new CanalMsgMQHandlerImpl(messageSender);

        CanalService canalService = new CanalService(canalPool, canalMsgHandler);

        canalService.start();
    }

}
