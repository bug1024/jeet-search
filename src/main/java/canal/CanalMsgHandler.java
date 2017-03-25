package canal;

import com.alibaba.fastjson.JSON;

/**
 * @author bug1024
 * @date 2017-03-25
 */
public interface CanalMsgHandler {

    Boolean handle(CanalMsg canalMsg);

}
