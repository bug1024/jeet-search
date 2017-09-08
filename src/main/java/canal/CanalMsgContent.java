package canal;

import lombok.Data;

import java.util.List;

/**
 * Canal基础信息 包括表名等
 * @author bug1024
 * @date 2017-09-08
 */
@Data
public class CanalMsgContent {

    private String binLogFile;
    private Long binlogOffset;
    private String dbName;
    private String tableName;
    private String eventType;
    private List<CanalChangeInfo> dataBefore;
    private List<CanalChangeInfo> dataAfter;
}
