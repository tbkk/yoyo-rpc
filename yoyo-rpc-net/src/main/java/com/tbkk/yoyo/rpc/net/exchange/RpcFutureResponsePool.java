package com.tbkk.yoyo.rpc.net.exchange;

import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.*;

/**
 * yoyo-rpc invoker callback, init service-register
 *
 * @author tbkk 2019-10-19
 */
@Slf4j
public class RpcFutureResponsePool {

    private static class Holder {
        private static final RpcFutureResponsePool rpcFutureResponsePool = new RpcFutureResponsePool();
    }

    public static RpcFutureResponsePool getSingleton(){
        return RpcFutureResponsePool.Holder.rpcFutureResponsePool;
    }

    private RpcFutureResponsePool()
    {
    }

    private ConcurrentMap<String, RpcFutureResponse> futureResponsePool = new ConcurrentHashMap<>();

    public void setInvokerFuture(String requestId, RpcFutureResponse futureResponse){
        futureResponsePool.put(requestId, futureResponse);
    }

    public void removeInvokerFuture(String requestId){
        futureResponsePool.remove(requestId);
    }

    public void notifyInvokerFuture(String requestId, final RpcResponse rpcResponse){

        final RpcFutureResponse futureResponse = futureResponsePool.get(requestId);
        if (futureResponse == null) {
            return;
        }

        if (futureResponse.getInvokeCallback()!=null) {

            try {
                RpcResponseCallBackThreadPool.getSingleton().executeResponseCallback(() -> {
                    if (rpcResponse.getErrorMsg() != null) {
                        futureResponse.getInvokeCallback().onFailure(new YoyoRpcException(rpcResponse.getErrorMsg()));
                    } else {
                        futureResponse.getInvokeCallback().onSuccess(rpcResponse.getResult());
                    }
                });
            }catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {

            futureResponse.setResponseAndNotify(rpcResponse);
        }

        // do remove
        futureResponsePool.remove(requestId);

    }
}
