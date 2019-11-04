package com.tbkk.yoyo.rpc.core.invoker.call;


/**
 * @author tbkk 2019-10-23
 */
public class RpcInvokeCallbackThreadContainer {


    private ThreadLocal<RpcInvokeCallbackThreadContainer> threadInvokerFuture = new ThreadLocal<RpcInvokeCallbackThreadContainer>();

    /**
     * get callback
     *
     * @return
     */
    public RpcInvokeCallbackThreadContainer getCallback() {
        RpcInvokeCallbackThreadContainer invokeCallback = threadInvokerFuture.get();
        threadInvokerFuture.remove();
        return invokeCallback;
    }

    /**
     * set future
     *
     * @param invokeCallback
     */
    public void setCallback(RpcInvokeCallbackThreadContainer invokeCallback) {
        threadInvokerFuture.set(invokeCallback);
    }

    /**
     * remove future
     */
    public void removeCallback() {
        threadInvokerFuture.remove();
    }


}
