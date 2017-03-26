package canal;

import com.alibaba.fastjson.JSON;

/**
 * canal消息处理
 *
 * @author bug1024
 * @date 2017-03-25
 */
public interface CanalMsgHandler {

    Boolean handle(CanalMsg canalMsg);

}
