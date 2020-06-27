package com.bug1024.jeetsearch.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.bug1024.jeetsearch.consts.CommonConstant;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

/**
 * @author bug1024
 * @date 2017-03-25
 */
@Service
public class CanalConnectorFactory {

    public CanalConnector getConnector() {
        return CanalConnectors.newSingleConnector(new InetSocketAddress(CommonConstant.CANAL_HOST, CommonConstant.CANAL_PORT), CommonConstant.CANAL_DEST, "", "");
    }

}
