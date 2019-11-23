package com.tbkk.yoyo.rpc.core.invoker;

import com.tbkk.yoyo.rpc.common.utils.YoyoServiceUtil;
import com.tbkk.yoyo.rpc.loadbalance.LoadBalance;
import com.tbkk.yoyo.rpc.net.client.connect.ConnectClientPool;
import com.tbkk.yoyo.rpc.net.exchange.BaseCallback;
import com.tbkk.yoyo.rpc.net.exchange.RpcResponseCallBackThreadPool;
import com.tbkk.yoyo.rpc.registry.RegisterCenter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;


/**
 * @author tbkk
 */
@NoArgsConstructor
@Slf4j
public class YoyoRpcInvokerFactory {


    @Setter
    private RegisterCenter registerCenter;

    private LoadBalance loadBalance = LoadBalance.ROUND;
    private List<BaseCallback> stopCallbackList = new ArrayList<>();

    public YoyoRpcInvokerFactory(RegisterCenter registerCenter) {
        this.registerCenter = registerCenter;
    }

    public void start() throws Exception {
        registerCenter.start();
        stopCallbackList.add(() -> ConnectClientPool.getSingleton().clear());

    }

    public void stop() throws Exception {
        // stop register
        if (registerCenter != null) {
            registerCenter.stop();
        }

        // stop callback
        if (stopCallbackList.size() > 0) {
            for (BaseCallback callback : stopCallbackList) {
                try {
                    callback.run();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        RpcResponseCallBackThreadPool.getSingleton().stopCallbackThreadPool();
    }

    public RegisterCenter getRegisterCenter() {
        return registerCenter;
    }


    public void addStopCallBack(BaseCallback callback) {
        stopCallbackList.add(callback);
    }

    public String getFinalAddress(String className, String version) {

        String finalAddress = "";
        String serviceKey = YoyoServiceUtil.genServiceKey(className, version);
        TreeSet<String> addressSet = this.getRegisterCenter().discovery(serviceKey);
        // load balance
        if (addressSet == null || addressSet.size() == 0) {
            // pass
            log.warn("yoyo-rpc can not find address for : {}", serviceKey);
        } else if (addressSet.size() == 1) {
            finalAddress = addressSet.first();
        } else {
            finalAddress = loadBalance.yoyoRpcInvokerRouter.route(serviceKey, addressSet);
        }

        return finalAddress;
    }
}
