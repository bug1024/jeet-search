package canal;

import com.alibaba.fastjson.JSON;

/**
 * @author bug1024
 * @date 2017-03-25
 */
public class SimpleCanalMsgHandlerImpl implements CanalMsgHandler {

    public Boolean handle(CanalMsg canalMsg) {
        System.out.println(JSON.toJSONString(canalMsg.getMsg()));
        return null;
    }

}
