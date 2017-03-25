import canal.CanalPool;
import canal.CanalService;
import canal.SimpleCanalMsgHandlerImpl;
import org.springframework.test.context.ContextConfiguration;

/**
 * 启动canal消息处理程序
 *
 * @author bug1024
 * @date 2017-03-25
 */
@ContextConfiguration("classpath:/spring/applicationContext.xml")
public class StartCanalMsgHandler {

    public static void main(String[] args) {
        SimpleCanalMsgHandlerImpl simpleCanalMsgHandler = new SimpleCanalMsgHandlerImpl();
        CanalPool canalPool = new CanalPool();
        CanalService canalService = new CanalService(canalPool, simpleCanalMsgHandler);
        canalService.start();
    }

}
