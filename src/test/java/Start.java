import canal.CanalPool;
import canal.CanalService;
import canal.SimpleCanalMsgHandlerImpl;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author bug1024
 * @date 2017-03-25
 */
@ContextConfiguration("classpath:/spring/applicationContext.xml")
public class Start {

    public static void main(String[] args) {
        CanalService canalService = new CanalService();
        SimpleCanalMsgHandlerImpl handler = new SimpleCanalMsgHandlerImpl();
        CanalPool canalPool = new CanalPool();
        canalService.setCanalPool(canalPool);
        canalService.setCanalMsgHandler(handler);
        canalService.start();
    }

}
