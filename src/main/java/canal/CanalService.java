package canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * canal客户端服务
 *
 * @author bug1024
 * @date 2017-03-25
 */
public class CanalService {

    private CanalMsgHandler canalMsgHandler;

    private CanalPool canalPool;

    public CanalService(CanalPool canalPool, CanalMsgHandler canalMsgHandler) {
        this.canalPool  = canalPool;
        this.canalMsgHandler = canalMsgHandler;
    }

    public void start() {
        int batchSize = 1000;
        int emptyCount = 0;

        CanalConnector canalConnector = canalPool.getConnector();

        try {
            canalConnector.connect();
            canalConnector.subscribe(".*\\..*");
            canalConnector.rollback();
            int totalEmptyCount = 300;

            System.out.println("=======Begin=======");

            while (emptyCount < totalEmptyCount) {
                // 获取指定数量的数据
                Message message = canalConnector.getWithoutAck(batchSize);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    System.out.println(emptyCount);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {

                    }
                } else {
                    emptyCount = 0;
                    processEntry(message.getEntries());
                }

                // 提交确认
                canalConnector.ack(batchId);
                // 处理失败, 回滚数据
                // connector.rollback(batchId);
            }

            System.out.println("=======End=======");
        } finally {
            canalConnector.disconnect();
        }
    }

    private void processEntry(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {
            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                continue;
            }

            CanalEntry.RowChange rowChange = null;
            try {
                rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parse error, data:" + entry.toString(), e);
            }

            CanalEntry.EventType eventType = rowChange.getEventType();

            Map<String, Object> baseInfo = new HashMap<String, Object>(5);
            baseInfo.put("binlogFile", entry.getHeader().getLogfileName());
            baseInfo.put("binlogOffset", entry.getHeader().getLogfileOffset());
            baseInfo.put("dbName", entry.getHeader().getSchemaName());
            baseInfo.put("tableName", entry.getHeader().getTableName());
            baseInfo.put("eventType", eventType.toString().toLowerCase());

            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                CanalMsg canalMsg = getCanalMsg(baseInfo, rowData.getBeforeColumnsList(), rowData.getAfterColumnsList());
                canalMsgHandler.handle(canalMsg);
            }
        }
    }

    private Map columnConvertToMap(CanalEntry.Column column) {
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("name", column.getName());
        map.put("value", column.getValue());
        map.put("update", column.getUpdated());

        return map;
    }

    private CanalMsg getCanalMsg(Map<String, Object> baseInfo, List<CanalEntry.Column> columnsBefore, List<CanalEntry.Column> columnsAfter) {
        List<Object> beforeArray = new ArrayList(columnsBefore.size());
        List<Object> afterArray  = new ArrayList(columnsAfter.size());

        for (CanalEntry.Column column: columnsBefore) {
            beforeArray.add(columnConvertToMap(column));
        }

        for (CanalEntry.Column column: columnsAfter) {
            afterArray.add(columnConvertToMap(column));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dbName", baseInfo.get("dbName"));
        map.put("tableName", baseInfo.get("tableName"));
        map.put("eventType", baseInfo.get("eventType"));
        map.put("before", beforeArray);
        map.put("after", afterArray);

        CanalMsg canalMsg = new CanalMsg();
        canalMsg.setKey(baseInfo.get("tableName").toString());
        canalMsg.setMsg(map);

        return canalMsg;
    }

}
