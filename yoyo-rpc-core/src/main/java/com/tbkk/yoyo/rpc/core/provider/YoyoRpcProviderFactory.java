package com.tbkk.yoyo.rpc.core.provider;

import com.tbkk.yoyo.rpc.common.constants.YoyoConstants;
import com.tbkk.yoyo.rpc.net.NetEnum;
import com.tbkk.yoyo.rpc.net.Server;
import com.tbkk.yoyo.rpc.net.server.service.ProviderServicePool;
import com.tbkk.yoyo.rpc.registry.RegisterCenter;

import com.tbkk.yoyo.rpc.common.utils.IpUtil;
import com.tbkk.yoyo.rpc.common.utils.NetUtil;
import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * provider
 *
 * @author tbkk 2019-10-31 22:54:27
 */
@Slf4j
public class YoyoRpcProviderFactory {

    @Setter
    private RegisterCenter registerCenter;

    private String serverIp = "";
    private int serverPort = 9090;
    private Server serverInstance;
    private String serviceAddress = "";


    public YoyoRpcProviderFactory(String serverIp, int serverPort, RegisterCenter registerCenter) {
        this.registerCenter = registerCenter;
        this.serverIp = serverIp;
        this.serverPort = serverPort;

        if (NetUtil.isPortUsed(serverPort)) {
            throw new YoyoRpcException("yoyo-rpc provider port[" + serverPort + "] is used.");
        }

        this.serviceAddress = IpUtil.getIpPort(serverIp, serverPort);
        try {
            serverInstance = NetEnum.NETTY.serverClass.newInstance();
        } catch (Exception e) {
            throw new YoyoRpcException("yoyo-rpc provider port[" + serverPort + "] is used.");
        }

    }

    public YoyoRpcProviderFactory(RegisterCenter registerCenter) {
        this.registerCenter = registerCenter;
        this.serverIp = IpUtil.getIp();
        this.serverPort = YoyoConstants.DEFAULT_PROVIDER_PORT;

        if (NetUtil.isPortUsed(serverPort)) {
            throw new YoyoRpcException("yoyo-rpc provider port[" + serverPort + "] is used.");
        }

        this.serviceAddress = IpUtil.getIpPort(serverIp, serverPort);
        try {
            serverInstance = NetEnum.NETTY.serverClass.newInstance();
        } catch (Exception e) {
            throw new YoyoRpcException("yoyo-rpc provider port[" + serverPort + "] is used.");
        }

    }

    public void start() throws Exception {
        serverInstance.setStartedCallback(() -> {
            registerCenter.start();
            Map serviceData = ProviderServicePool.getSingleton().getServiceData();
            if (serviceData.size() > 0) {
                registerCenter.register(serviceData.keySet(), serviceAddress);
            }
        });
        serverInstance.setStopedCallback(() -> {
            // stop register
            if (registerCenter != null) {
                registerCenter.stop();
                Map serviceData = ProviderServicePool.getSingleton().getServiceData();
                if (serviceData.size() > 0) {
                    registerCenter.remove(serviceData.keySet(), serviceAddress);
                }
                registerCenter.stop();
                registerCenter = null;
            }
        });
        serverInstance.start(serverPort);
    }

    public void stop() throws Exception {
        serverInstance.stop();
    }


}
