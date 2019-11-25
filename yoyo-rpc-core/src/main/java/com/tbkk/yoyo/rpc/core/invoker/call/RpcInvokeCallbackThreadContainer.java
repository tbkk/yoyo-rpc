package com.tbkk.yoyo.rpc.core.invoker.call;


/**
 * @author tbkk 2019-10-23
 */
public class RpcInvokeCallbackThreadContainer {


    private ThreadLocal<YoyoRpcInvokeCallback> threadInvokerFuture = new ThreadLocal<>();

    /**
     * get callback
     *
     * @return
     */
    public YoyoRpcInvokeCallback getCallback() {
        YoyoRpcInvokeCallback invokeCallback = threadInvokerFuture.get();
        threadInvokerFuture.remove();
        return invokeCallback;
    }

    /**
     * set future
     *
     * @param invokeCallback
     */
    public void setCallback(YoyoRpcInvokeCallback invokeCallback) {
        threadInvokerFuture.set(invokeCallback);
    }

    /**
     * remove future
     */
    public void removeCallback() {
        threadInvokerFuture.remove();
    }


}
