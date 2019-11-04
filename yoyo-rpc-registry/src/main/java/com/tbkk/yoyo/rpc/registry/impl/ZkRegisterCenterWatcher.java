package com.tbkk.yoyo.rpc.registry.impl;

import com.tbkk.yoyo.rpc.registry.utils.YoyoZkClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

/**
 * service register for "zookeeper"
 *
 *  /yoyo-rpc/dev/
 *              - key01(service01)
 *                  - value01 (ip:port01)
 *                  - value02 (ip:port02)
 */
@Slf4j
@AllArgsConstructor
public class ZkRegisterCenterWatcher implements Watcher {

    private ZkRegisterCenter zkRegisterCenter;

    @Override
    public void process(WatchedEvent watchedEvent) {
        YoyoZkClient yoyoZkClient = zkRegisterCenter.getYoyoZkClient();
        if (yoyoZkClient == null)
        {
            return;
        }

        try {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.Expired) {
                log.info("yoyo-rpc, zk re-connect start reload service");
                yoyoZkClient.destroy();
                yoyoZkClient.initClient();
                zkRegisterCenter.refreshDiscoveryData(null);
                log.info("yoyo-rpc, zk re-connect reload service success");
            }

            // watch + refresh
            String path = watchedEvent.getPath();
            String key = zkRegisterCenter.fullPathToKey(path);
            if (key != null) {
                yoyoZkClient.getClient().exists(path, true);
                if (watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                    zkRegisterCenter.refreshDiscoveryData(key);
                } else if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    log.info("reload all");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
