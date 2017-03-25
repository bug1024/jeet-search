import canal.CanalService;
import canal.SimpleCanalMsgHandlerImpl;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * @author bug1024
 * @date 2017-03-25
 */
@ContextConfiguration("classpath:/spring/applicationContext.xml")
public class Start {

    public static void main(String[] args) {
        CanalService canalService = new CanalService();
        SimpleCanalMsgHandlerImpl handler = new SimpleCanalMsgHandlerImpl();
        canalService.setCanalMsgHandler(handler);
        canalService.start();
    }

}
