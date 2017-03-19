package core;

import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.alibaba.otter.canal.protocol.Message;
import com.alibaba.otter.canal.protocol.CanalEntry.Column;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.alibaba.otter.canal.protocol.CanalEntry.EntryType;
import com.alibaba.otter.canal.protocol.CanalEntry.EventType;
import com.alibaba.otter.canal.protocol.CanalEntry.RowChange;
import com.alibaba.otter.canal.protocol.CanalEntry.RowData;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;

/**
 * canal client
 *
 * @author bug1024
 * @date 2017-03-19
 */
public class CanalClient {

    public static void main(String args[]) {
        // @TODO 基于zookeeper动态获取canal server的地址
        CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(AddressUtils.getHostIp(),
                11111), "example", "", "");
        int batchSize = 1000;
        int emptyCount = 0;
        try {
            connector.connect();
            connector.subscribe(".*\\..*");
            connector.rollback();
            int totalEmptyCount = 300;

            System.out.println("=======Begin=========");

            while (emptyCount < totalEmptyCount) {
                // 获取指定数量的数据
                Message message = connector.getWithoutAck(batchSize);
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
                    printEntry(message.getEntries());
                }

                // 提交确认
                connector.ack(batchId);
                // 处理失败, 回滚数据
                // connector.rollback(batchId);
            }
        } finally {
            connector.disconnect();
        }
    }

    private static void printEntry(List<Entry> entries) {
        for (Entry entry : entries) {
            if (entry.getEntryType() == EntryType.TRANSACTIONBEGIN || entry.getEntryType() == EntryType.TRANSACTIONEND) {
                continue;
            }

            RowChange rowChange = null;
            try {
                rowChange = RowChange.parseFrom(entry.getStoreValue());
            } catch (Exception e) {
                throw new RuntimeException("ERROR ## parse error, data:" + entry.toString(), e);
            }

            EventType eventType = rowChange.getEventType();

            Map<String, Object> baseInfo = new HashMap<String, Object>(5);
            baseInfo.put("binlogFile", entry.getHeader().getLogfileName());
            baseInfo.put("binlogOffset", entry.getHeader().getLogfileOffset());
            baseInfo.put("dbName", entry.getHeader().getSchemaName());
            baseInfo.put("tableName", entry.getHeader().getTableName());
            baseInfo.put("eventType", eventType);

            for (RowData rowData : rowChange.getRowDatasList()) {
                sendToMq(baseInfo, rowData.getBeforeColumnsList(), rowData.getAfterColumnsList());
            }
        }
    }

    private static Map columnToMap(Column column) {
        Map<String, Object> columnMap = new HashMap<String, Object>(3);
        columnMap.put("name", column.getName());
        columnMap.put("value", column.getValue());
        columnMap.put("update", column.getUpdated());

        return columnMap;
    }


    private static void sendToMq(Map<String, Object> baseInfo, List<Column> columnsBefore, List<Column> columnsAfter) {
        List<Object> beforeArray = new ArrayList(columnsBefore.size());
        List<Object> afterArray = new ArrayList(columnsAfter.size());

        for (Column column: columnsBefore) {
            beforeArray.add(columnToMap(column));
        }

        for (Column column: columnsAfter) {
            afterArray.add(columnToMap(column));
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dbName", baseInfo.get("dbName"));
        map.put("tableName", baseInfo.get("tableName"));
        map.put("eventType", baseInfo.get("eventType"));
        map.put("before", beforeArray);
        map.put("after", afterArray);

        String routeKey = baseInfo.get("dbName") + "." + baseInfo.get("tableName");
        String msg = map.toString();

        // @TODO 发送到消息队列
        System.out.println(routeKey + ":" + msg);
    }

}
