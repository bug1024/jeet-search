import canal.CanalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 启动CanalService
 * @author bug1024
 * @date 2017-11-03
 */
public class CanalStarter {
    private static Logger logger = LoggerFactory.getLogger(CanalStarter.class);

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
