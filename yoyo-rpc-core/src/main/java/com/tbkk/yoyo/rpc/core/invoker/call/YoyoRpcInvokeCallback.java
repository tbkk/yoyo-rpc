package com.tbkk.yoyo.rpc.core.invoker.call;


import com.tbkk.yoyo.rpc.net.client.callback.RpcInvokeCallback;

/**
 * @author tbkk 2019-10-23
 */
public abstract class YoyoRpcInvokeCallback<T> extends RpcInvokeCallback<T> {

    @Override
    public abstract void onSuccess(T result);

    @Override
    public abstract void onFailure(Throwable exception);


    // ---------------------- thread invoke callback ----------------------

    private static ThreadLocal<YoyoRpcInvokeCallback> threadInvokerFuture = new ThreadLocal<YoyoRpcInvokeCallback>();

    /**
     * get callback
     *
     * @return
     */
    public static YoyoRpcInvokeCallback getCallback() {
        YoyoRpcInvokeCallback invokeCallback = threadInvokerFuture.get();
        threadInvokerFuture.remove();
        return invokeCallback;
    }

    /**
     * set future
     *
     * @param invokeCallback
     */
    public static void setCallback(YoyoRpcInvokeCallback invokeCallback) {
        threadInvokerFuture.set(invokeCallback);
    }

    /**
     * remove future
     */
    public static void removeCallback() {
        threadInvokerFuture.remove();
    }


}
