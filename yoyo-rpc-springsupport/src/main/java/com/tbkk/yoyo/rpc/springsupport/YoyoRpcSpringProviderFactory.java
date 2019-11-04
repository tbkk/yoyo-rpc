package com.tbkk.yoyo.rpc.springsupport;

import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;
import com.tbkk.yoyo.rpc.core.provider.YoyoRpcProviderFactory;
import com.tbkk.yoyo.rpc.net.NetEnum;
import com.tbkk.yoyo.rpc.net.serializer.AbstractSerializer;
import com.tbkk.yoyo.rpc.net.server.service.ProviderServicePool;
import com.tbkk.yoyo.rpc.springsupport.annotation.YoyoRpcService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * yoyo-rpc provider (for spring)
 *
 * @author tbkk 2019-10-18 18:09:20
 */
@AllArgsConstructor
public class YoyoRpcSpringProviderFactory implements ApplicationContextAware, InitializingBean,DisposableBean {


    private YoyoRpcProviderFactory rpcProviderFactory = null;

    // ---------------------- util ----------------------

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(YoyoRpcService.class);
        if (serviceBeanMap!=null && serviceBeanMap.size()>0) {
            for (Object serviceBean : serviceBeanMap.values()) {
                // valid
                if (serviceBean.getClass().getInterfaces().length ==0) {
                    throw new YoyoRpcException("yoyo-rpc, service(YoyoRpcService) must inherit interface.");
                }
                // add service
                YoyoRpcService yoyoRpcService = serviceBean.getClass().getAnnotation(YoyoRpcService.class);

                String iface = serviceBean.getClass().getInterfaces()[0].getName();
                String version = yoyoRpcService.version();

                ProviderServicePool.getSingleton().addService(iface, version, serviceBean);
            }
        }

        // TODO，addServices by api + prop

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        rpcProviderFactory.start();
    }

    @Override
    public void destroy() throws Exception {
        rpcProviderFactory.stop();
    }

}
