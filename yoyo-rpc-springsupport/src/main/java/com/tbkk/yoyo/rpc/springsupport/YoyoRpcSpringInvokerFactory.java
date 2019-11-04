package com.tbkk.yoyo.rpc.springsupport;

import com.tbkk.yoyo.rpc.common.utils.YoyoServiceUtil;
import com.tbkk.yoyo.rpc.core.invoker.YoyoRpcInvokerFactory;
import com.tbkk.yoyo.rpc.core.invoker.reference.YoyoRpcReferenceBean;
import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;
import com.tbkk.yoyo.rpc.springsupport.annotation.YoyoRpcReference;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * yoyo-rpc invoker callback, init service-register and spring-bean by annotation (for spring)
 *
 * @author tbkk 2019-9-23
 */
@Slf4j
public class YoyoRpcSpringInvokerFactory extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean, DisposableBean, BeanFactoryAware {


    @Setter
    private YoyoRpcInvokerFactory yoyoRpcInvokerFactory;

    public YoyoRpcSpringInvokerFactory(YoyoRpcInvokerFactory yoyoRpcInvokerFactory)
    {
        this.yoyoRpcInvokerFactory = yoyoRpcInvokerFactory;
    }


    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    // ---------------------- util ----------------------

    @Override
    public void afterPropertiesSet() throws Exception {
        yoyoRpcInvokerFactory.start();
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {

        // collection
        final Set<String> serviceKeyList = new HashSet<>();

        // parse YoyoRpcReferenceBean
        //找到Sping容器中，带有YoyoRpcReference注解的field进行操作
        //生成引用的Bean
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            Class iface = field.getType();
            if (!iface.isInterface()) {
                throw new YoyoRpcException("yoyo-rpc, reference(YoyoRpcReference) must be interface.");
            }

            YoyoRpcReference rpcReference = field.getAnnotation(YoyoRpcReference.class);

            YoyoRpcReferenceBean referenceBean = new YoyoRpcReferenceBean(
                    rpcReference.callType(),
                    rpcReference.loadBalance(),
                    iface,
                    rpcReference.version(),
                    rpcReference.timeout(),
                    rpcReference.address(),
                    rpcReference.accessToken(),
                    null,
                    yoyoRpcInvokerFactory
            );

            Object serviceProxy = referenceBean.getInstanceObject();

            // set bean 动态设置远程调用对象
            field.setAccessible(true);
            field.set(bean, serviceProxy);

            log.info("yoyo-rpc, invoker callback init reference bean success. serviceKey = {}, bean.field = {}.{}",
                    YoyoServiceUtil.makeServiceKey(iface.getName(), rpcReference.version()), beanName, field.getName());

            // collection
            String serviceKey = YoyoServiceUtil.makeServiceKey(iface.getName(), rpcReference.version());
            serviceKeyList.add(serviceKey);
        }, field -> {
            if (field.isAnnotationPresent(YoyoRpcReference.class)) {
                return true;
            }
            return false;
        });

        if (yoyoRpcInvokerFactory.getRegisterCenter() != null) {
            try {
                yoyoRpcInvokerFactory.getRegisterCenter().discovery(serviceKeyList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return super.postProcessAfterInstantiation(bean, beanName);
    }


    @Override
    public void destroy() throws Exception {
        yoyoRpcInvokerFactory.stop();
    }


}
