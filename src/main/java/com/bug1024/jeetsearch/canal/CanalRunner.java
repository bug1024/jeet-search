package com.bug1024.jeetsearch.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.common.utils.NamedThreadFactory;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.bug1024.jeetsearch.consts.CommonConstant;
import com.bug1024.jeetsearch.utils.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * canal客户端服务
 *
 * @author bug1024
 * @date 2017-03-25
 */
@Component
@Slf4j
public class CanalRunner implements CommandLineRunner {

    @Resource
    private CanalMsgHandler canalMsgHandler;

    @Resource
    private CanalConnectorFactory canalConnectorFactory;

    private static ExecutorService executorService = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new NamedThreadFactory("canal-runner")
    );

    @Override
    public void run(String... args) {
        executorService.execute(this::start);
    }

    private void start() {
        log.info("CanalService start");
        int emptyCount = 0;
        CanalConnector canalConnector = canalConnectorFactory.getConnector();
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
                    TimeUtil.sleep(1000);
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
