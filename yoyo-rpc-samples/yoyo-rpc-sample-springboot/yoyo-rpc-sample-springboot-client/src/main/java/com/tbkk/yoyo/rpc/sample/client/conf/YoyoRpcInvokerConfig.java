package com.tbkk.yoyo.rpc.sample.client.conf;

import com.tbkk.yoyo.rpc.core.invoker.YoyoRpcInvokerFactory;
import com.tbkk.yoyo.rpc.registry.RegisterCenter;
import com.tbkk.yoyo.rpc.registry.impl.ZkRegisterCenter;
import com.tbkk.yoyo.rpc.springsupport.YoyoRpcSpringInvokerFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * yoyo-rpc invoker config
 *
 * @author tbkk 2019-10-19
 */
@Configuration
@Slf4j
public class YoyoRpcInvokerConfig {

    @Value("${yoyo-rpc.registry.yoyoregistry.address}")
    private String address;

    @Value("${yoyo-rpc.registry.yoyoregistry.env}")
    private String env;


    @Bean
    public YoyoRpcSpringInvokerFactory yoyoJobExecutor() {


        RegisterCenter registerCenter = new ZkRegisterCenter();
        registerCenter.setRegisterParam(new HashMap<String, String>(){{
            put(ZkRegisterCenter.ZK_ADDRESS, address);
            put(ZkRegisterCenter.ENV, env);
        }});

        YoyoRpcInvokerFactory yoyoRpcInvokerFactory = new YoyoRpcInvokerFactory(registerCenter);

        YoyoRpcSpringInvokerFactory invokerFactory = new YoyoRpcSpringInvokerFactory(yoyoRpcInvokerFactory);

        log.info(" yoyo-rpc invoker config init finish.");
        return invokerFactory;
    }

}