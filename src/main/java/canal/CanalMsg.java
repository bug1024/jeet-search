package canal;

import lombok.Data;

import java.util.Map;

/**
 * canal消息
 *
 * @author bug1024
 * @date 2017-03-25
 */
@Data
public class CanalMsg {

    private Map msg;

    private String key;

    CanalMsg(String key, Map msg) {
        this.key = "canal." + key;
        this.msg = msg;
    }

}
