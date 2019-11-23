package com.tbkk.yoyo.rpc.net.server.service;


import com.tbkk.yoyo.rpc.common.exception.ThrowableUtil;
import com.tbkk.yoyo.rpc.common.utils.YoyoServiceUtil;
import com.tbkk.yoyo.rpc.net.exchange.RpcRequest;
import com.tbkk.yoyo.rpc.net.exchange.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tbkk 2019-10-19
 */
@Slf4j
public class ProviderServicePool {

    private static class Holder {
        private static final ProviderServicePool PROVIDER_SERVICE_POOL = new ProviderServicePool();
    }


    public static ProviderServicePool getSingleton(){
        return Holder.PROVIDER_SERVICE_POOL;
    }

    private ProviderServicePool()
    {
    }

    /**
     * init local rpc service map
     */
    private Map<String, Object> serviceData = new HashMap<>();
    public Map<String, Object> getServiceData() {
        return serviceData;
    }

    /**
     * add service
     *
     * @param iface
     * @param version
     * @param serviceBean
     */
    public void addService(String iface, String version, Object serviceBean){
        String serviceKey = YoyoServiceUtil.genServiceKey(iface, version);
        serviceData.put(serviceKey, serviceBean);

        log.info(" yoyo-rpc, provider callback add service success. serviceKey = {}, serviceBean = {}", serviceKey, serviceBean.getClass());
    }

    /**
     * invoke service
     *
     * @param rpcRequest
     * @return
     */
    public RpcResponse invokeService(RpcRequest rpcRequest) {

        //  make response
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(rpcRequest.getRequestId());

        // match service bean
        String serviceKey = YoyoServiceUtil.genServiceKey(rpcRequest.getClassName(), rpcRequest.getVersion());
        Object serviceBean = serviceData.get(serviceKey);

        // valid
        if (serviceBean == null) {
            rpcResponse.setErrorMsg("The serviceKey["+ serviceKey +"] not found.");
            return rpcResponse;
        }

        if (System.currentTimeMillis() - rpcRequest.getCreateMillisTime() > 3*60*1000) {
            rpcResponse.setErrorMsg("The timestamp difference between admin and executor exceeds the limit.");
            return rpcResponse;
        }
        String accessToken  = rpcRequest.getAccessToken();
        if (accessToken!=null && accessToken.trim().length()>0 && !accessToken.trim().equals(rpcRequest.getAccessToken())) {
            rpcResponse.setErrorMsg("The access token[" + rpcRequest.getAccessToken() + "] is wrong.");
            return rpcResponse;
        }

        try {
            // invoke
            Class<?> serviceClass = serviceBean.getClass();
            String methodName = rpcRequest.getMethodName();
            Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
            Object[] parameters = rpcRequest.getParameters();

            Method method = serviceClass.getMethod(methodName, parameterTypes);
            method.setAccessible(true);
            Object result = method.invoke(serviceBean, parameters);

			/*FastClass serviceFastClass = FastClass.create(serviceClass);
			FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
			Object result = serviceFastMethod.invoke(serviceBean, parameters);*/

            rpcResponse.setResult(result);
        } catch (Throwable t) {
            // catch error
            log.error("yoyo-rpc provider invokeService error.", t);
            rpcResponse.setErrorMsg(ThrowableUtil.toString(t));
        }

        return rpcResponse;
    }

}
