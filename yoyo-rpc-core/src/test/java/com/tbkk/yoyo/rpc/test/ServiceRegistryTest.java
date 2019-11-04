package com.tbkk.yoyo.rpc.test;

import com.tbkk.yoyo.rpc.registry.RegisterCenter;
import com.tbkk.yoyo.rpc.registry.impl.ZkRegisterCenter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author tbkk 2019-10-17
 */
public class ServiceRegistryTest {

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, InterruptedException {

        Map<String, String> param = new HashMap<>();
        param.put(ZkRegisterCenter.ZK_ADDRESS, "127.0.0.1:2181");
        param.put(ZkRegisterCenter.ZK_DIGEST, "");
        param.put(ZkRegisterCenter.ENV, "test");


        Class<? extends RegisterCenter> serviceRegistryClass = ZkRegisterCenter.class;

        RegisterCenter registerCenter = serviceRegistryClass.newInstance();
        registerCenter.start(param);


        String servicename = "demo_service";
        System.out.println(registerCenter.discovery(servicename));

        registerCenter.register(new HashSet<String>(Arrays.asList(servicename)), "127.0.0.1:8888");
        TimeUnit.MILLISECONDS.sleep(10);
        System.out.println(registerCenter.discovery(servicename));

        registerCenter.register(new HashSet<String>(Arrays.asList(servicename)), "127.0.0.1:9999");
        TimeUnit.MILLISECONDS.sleep(10);
        System.out.println(registerCenter.discovery(servicename));

        registerCenter.remove(new HashSet<String>(Arrays.asList(servicename)), "127.0.0.1:9999");
        TimeUnit.MILLISECONDS.sleep(10);
        System.out.println(registerCenter.discovery(servicename));

    }

}
