package com.bug1024.jeetsearch.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.bug1024.jeetsearch.consts.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * canal客户端服务
 *
 * @author bug1024
 * @date 2017-03-25
 */
@Service
@Slf4j
public class CanalService {

    @Autowired
    private CanalMsgHandler canalMsgHandler;

    @Autowired
    private CanalPool canalPool;

    @PostConstruct
    public void start() {
        log.info("CanalService start");
        int emptyCount = 0;
        CanalConnector canalConnector = canalPool.getConnector();
        try {
            log.info("CanalService connect begin");
            canalConnector.connect();
            log.info("CanalService connect success");
            canalConnector.subscribe(".*\\..*");
            canalConnector.rollback();

            while (emptyCount < CommonConstant.CANAL_TOTAL_EMPTY_COUNT) {
                // 获取指定数量的数据
                Message message = canalConnector.getWithoutAck(CommonConstant.BATCH_SIZE);
                long batchId = message.getId();
                int size = message.getEntries().size();
                if (batchId == -1 || size == 0) {
                    emptyCount++;
                    log.info("emptyCount:{}", emptyCount);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {

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
        } catch (Exception e) {
            log.error("canal process error", e);
        } finally {
            log.info("CanalService disconnect");
            canalConnector.disconnect();
        }
    }

    private void processEntry(List<CanalEntry.Entry> entries) {
        List<CanalMsg> msgList = convertToCanalMsgList(entries);
        for (CanalMsg msg : msgList) {
            canalMsgHandler.handle(msg);
        }
    }

    private List<CanalMsg> convertToCanalMsgList(List<CanalEntry.Entry> entries) {
        List<CanalMsg> msgList = new ArrayList<>();
        CanalMsgContent canalMsgContent = null;
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
            canalMsgContent = new CanalMsgContent();
            canalMsgContent.setBinLogFile(entry.getHeader().getLogfileName());
            canalMsgContent.setBinlogOffset(entry.getHeader().getLogfileOffset());
            canalMsgContent.setDbName(entry.getHeader().getSchemaName());
            canalMsgContent.setTableName(entry.getHeader().getTableName());
            canalMsgContent.setEventType(eventType.toString().toLowerCase());

            for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
                canalMsgContent.setDataBefore(convertToCanalChangeInfoList(rowData.getBeforeColumnsList()));
                canalMsgContent.setDataAfter(convertToCanalChangeInfoList(rowData.getAfterColumnsList()));
                CanalMsg canalMsg = new CanalMsg(canalMsgContent);
                msgList.add(canalMsg);
            }
        }

        return msgList;
    }

    private List<CanalChangeInfo> convertToCanalChangeInfoList(List<CanalEntry.Column> columnList) {
        List<CanalChangeInfo> canalChangeInfoList = new ArrayList<>();
        for (CanalEntry.Column column : columnList) {
            CanalChangeInfo canalChangeInfo = new CanalChangeInfo();
            canalChangeInfo.setName(column.getName());
            canalChangeInfo.setValue(column.getValue());
            canalChangeInfo.setUpdate(column.getUpdated());
            canalChangeInfoList.add(canalChangeInfo);
        }

        return canalChangeInfoList;
    }

}
