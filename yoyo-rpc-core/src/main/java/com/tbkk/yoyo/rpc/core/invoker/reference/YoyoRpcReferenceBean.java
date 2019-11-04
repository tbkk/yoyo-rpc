package com.tbkk.yoyo.rpc.core.invoker.reference;

import com.tbkk.yoyo.rpc.core.invoker.call.CallType;
import com.tbkk.yoyo.rpc.core.invoker.call.YoyoRpcInvokeCallback;
import com.tbkk.yoyo.rpc.loadbalance.LoadBalance;
import com.tbkk.yoyo.rpc.net.Client;
import com.tbkk.yoyo.rpc.net.NetEnum;
import com.tbkk.yoyo.rpc.core.invoker.YoyoRpcInvokerFactory;

import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Proxy;

/**
 * rpc reference bean, use by api
 *
 * @author tbkk 2019-10-29 20:18:32
 */
@Slf4j
public class YoyoRpcReferenceBean{
    // ---------------------- config ----------------------

    private Class<?> iface;
    private CallType callType;
    private LoadBalance loadBalance;
    private String version;
    private long timeout = 1000;
    private String address;
    private String accessToken;
    private YoyoRpcInvokeCallback invokeCallback;
    private YoyoRpcInvokerFactory registerCenter;
    private Client client = null;

    public YoyoRpcReferenceBean(CallType callType,
                                LoadBalance loadBalance,
                                Class<?> iface,
                                String version,
                                long timeout,
                                String address,
                                String accessToken,
                                YoyoRpcInvokeCallback invokeCallback,
                                YoyoRpcInvokerFactory registerCenter
    ) {

        this.callType = callType;
        this.loadBalance = loadBalance;
        this.iface = iface;
        this.version = version;
        this.timeout = timeout;
        this.address = StringUtils.trim(address);
        this.accessToken = accessToken;
        this.invokeCallback = invokeCallback;
        this.registerCenter = registerCenter;

        if (this.callType == null) {
            throw new YoyoRpcException("yoyo-rpc reference callType missing.");
        }
        if (this.loadBalance == null) {
            throw new YoyoRpcException("yoyo-rpc reference loadBalance missing.");
        }
        if (this.iface == null) {
            throw new YoyoRpcException("yoyo-rpc reference iface missing.");
        }
        if (this.timeout < 0) {
            this.timeout = 0;
        }
        if (this.registerCenter == null) {
            throw new YoyoRpcException("YoyoRpcInvokerFactory missing.");
        }

        try {
            client = NetEnum.NETTY.clientClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new YoyoRpcException(e);
        }
    }

    // ---------------------- util ----------------------

    public Object getInstanceObject() {
        RpcReferenceInvocationHandler handler = new RpcReferenceInvocationHandler();
        handler.setAccessToken(accessToken);
        handler.setAddress(address);
        handler.setCallType(callType);
        handler.setClient(client);
        handler.setInvokeCallback(invokeCallback);
        handler.setRegisterCenterFactory(registerCenter);
        handler.setTimeout(timeout);
        handler.setVersion(version);
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{iface},handler);
    }


    public Class<?> getObjectType() {
        return iface;
    }

}
