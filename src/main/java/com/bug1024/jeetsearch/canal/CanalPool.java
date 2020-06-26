package com.bug1024.jeetsearch.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.alibaba.otter.canal.common.utils.AddressUtils;
import com.bug1024.jeetsearch.consts.CommonConstant;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

/**
 * @author bug1024
 * @date 2017-03-25
 */
@Service
public class CanalPool {

    public CanalConnector getConnector() {
        // @TODO 基于zookeeper动态获取canal server的地址
        com.alibaba.otter.canal.client.CanalConnector connector = CanalConnectors.newSingleConnector(new InetSocketAddress(CommonConstant.CANAL_HOST, CommonConstant.CANAL_PORT),
                                                                                                     CommonConstant.CANAL_DEST,
                                                                                                     "",
                                                                                                     "");

        return connector;
    }

}
