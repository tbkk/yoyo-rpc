package com.tbkk.yoyo.rpc.common.utils;

import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author tbkk 2019-02-18
 */
public class YoyoServiceUtil {

    public static String makeServiceKey(String iface, String version){
        String serviceKey = iface;
        if (version!=null && version.trim().length()>0) {
            serviceKey += "#".concat(version);
        }
        return serviceKey;
    }
}
