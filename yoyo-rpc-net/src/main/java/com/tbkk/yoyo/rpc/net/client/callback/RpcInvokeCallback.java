package com.tbkk.yoyo.rpc.net.client.callback;



/**
 * @author tbkk
 */
public abstract class RpcInvokeCallback<T> {

    public abstract void onSuccess(T result);

    public abstract void onFailure(Throwable exception);

}
