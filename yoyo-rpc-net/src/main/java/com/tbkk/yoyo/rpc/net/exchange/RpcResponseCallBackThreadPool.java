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
public class RpcResponseCallBackThreadPool {

    private static class Holder {
        private static final RpcResponseCallBackThreadPool rpcResponseCallBackThreadPool = new RpcResponseCallBackThreadPool();
    }

    public static RpcResponseCallBackThreadPool getSingleton(){
        return RpcResponseCallBackThreadPool.Holder.rpcResponseCallBackThreadPool;
    }

    private RpcResponseCallBackThreadPool()
    {
    }

    private ThreadPoolExecutor responseCallbackThreadPool = new ThreadPoolExecutor(
            10,
            100,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(1000),
            r -> new Thread(r, "responseCallbackThreadPool-" + r.hashCode()),
            (r, executor) -> {
                throw new YoyoRpcException("Invoke Callback Thread pool is EXHAUSTED!");
            });

    public void executeResponseCallback(Runnable runnable){

//        if (responseCallbackThreadPool == null) {
//            synchronized (this) {
//                if (responseCallbackThreadPool == null) {
//                    responseCallbackThreadPool = new ThreadPoolExecutor(
//                            10,
//                            100,
//                            60L,
//                            TimeUnit.SECONDS,
//                            new LinkedBlockingQueue<>(1000),
//                            r -> new Thread(r, "responseCallbackThreadPool-" + r.hashCode()),
//                            (r, executor) -> {
//                                throw new YoyoRpcException("Invoke Callback Thread pool is EXHAUSTED!");
//                            });
//                }
//            }
//        }
        responseCallbackThreadPool.execute(runnable);
    }
    public void stopCallbackThreadPool() {
        if (responseCallbackThreadPool != null) {
            responseCallbackThreadPool.shutdown();
        }
    }

}
