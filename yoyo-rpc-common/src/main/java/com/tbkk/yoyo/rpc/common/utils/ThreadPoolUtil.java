package com.tbkk.yoyo.rpc.common.utils;

import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;

import java.util.concurrent.*;

/**
 * @author tbkk 2019-02-18
 */
public class ThreadPoolUtil {

    /**
     * make server thread pool
     *
     * @param serverType
     * @return
     */
    public static ThreadPoolExecutor makeServerThreadPool(final String serverType){
        ThreadPoolExecutor serverHandlerPool = new ThreadPoolExecutor(
                60,
                300,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                r -> new Thread(r, "yoyo-rpc, "+serverType+"-serverHandlerPool-" + r.hashCode()),
                (r, executor) -> {
                    throw new YoyoRpcException("yoyo-rpc "+serverType+" Thread pool is EXHAUSTED!");
                });		// default maxThreads 300, minThreads 60

        return serverHandlerPool;
    }

}
