package com.tbkk.yoyo.rpc.sample.server.conf;

import com.tbkk.yoyo.rpc.core.provider.YoyoRpcProviderFactory;
import com.tbkk.yoyo.rpc.registry.RegisterCenter;
import com.tbkk.yoyo.rpc.registry.impl.ZkRegisterCenter;
import com.tbkk.yoyo.rpc.springsupport.YoyoRpcSpringProviderFactory;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

/**
 * yoyo-rpc provider config
 *
 * @author tbkk 2019-10-19
 */
@Configuration
@Slf4j
public class YoyoRpcProviderConfig {


    @Value("${yoyo-rpc.registry.address}")
    private String address;

    @Value("${yoyo-rpc.registry.env}")
    private String env;

    @Bean
    public YoyoRpcSpringProviderFactory yoyoRpcSpringProviderFactory() {

        RegisterCenter registerCenter = new ZkRegisterCenter();
        registerCenter.setRegisterParam(new HashMap<String, String>(){{
            put(ZkRegisterCenter.ZK_ADDRESS, address);
            put(ZkRegisterCenter.ENV, env);
        }});

        YoyoRpcProviderFactory yoyoRpcProviderFactory = new YoyoRpcProviderFactory(registerCenter);

        log.info(" yoyo-rpc provider config init finish.");
        return new YoyoRpcSpringProviderFactory(yoyoRpcProviderFactory);
    }

}