package com.tbkk.yoyo.rpc.registry.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


@Slf4j
@AllArgsConstructor
public class ZkRegisterCenterRefreshThread extends Thread {

    private ZkRegisterCenter zkRegisterCenter;


    @Override
    public void run() {
        while (!zkRegisterCenter.isRefreshThreadStop()) {
            try {
                TimeUnit.SECONDS.sleep(60);

                zkRegisterCenter.refreshDiscoveryData(null);
                zkRegisterCenter.refreshRegistryData();
            } catch (Exception e) {
                if (!zkRegisterCenter.isRefreshThreadStop()) {
                    log.error("yoyo-rpc, refresh thread error.", e);
                }
            }
        }
        log.info("yoyo-rpc, refresh thread stoped.");
    }
}
