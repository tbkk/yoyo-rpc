package com.tbkk.yoyo.rpc.core.invoker.reference;

import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;
import com.tbkk.yoyo.rpc.core.invoker.YoyoRpcInvokerFactory;
import com.tbkk.yoyo.rpc.core.invoker.call.CallType;
import com.tbkk.yoyo.rpc.core.invoker.call.YoyoRpcInvokeCallback;
import com.tbkk.yoyo.rpc.net.Client;
import com.tbkk.yoyo.rpc.net.exchange.RpcFutureResponse;
import com.tbkk.yoyo.rpc.net.exchange.RpcFutureResponsePool;
import com.tbkk.yoyo.rpc.net.exchange.RpcRequest;
import com.tbkk.yoyo.rpc.net.exchange.RpcResponse;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class RpcReferenceInvocationHandler implements InvocationHandler {

    private String version = "";
    private String address = "";
    private YoyoRpcInvokerFactory registerCenterFactory;
    private String accessToken;
    private CallType callType;
    private Client client = null;
    private long timeout = 1000;
    private YoyoRpcInvokeCallback invokeCallback;


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        {
            // method param
            String className = method.getDeclaringClass().getName();
            String methodName = method.getName();
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] parameters = args;

            // filter method like "Object.toString()"
            if (className.equals(Object.class.getName())) {
                throw new YoyoRpcException("yoyo-rpc proxy class-method not support");
            }


            String finalAddress = address;
            if (StringUtils.isBlank(finalAddress)) {
                finalAddress = registerCenterFactory.getFinalAddress(className, version);
            }
            if (finalAddress == null || finalAddress.trim().length() == 0) {
                throw new YoyoRpcException("yoyo-rpc reference bean[" + className + "] address empty");
            }

            // request
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setRequestId(UUID.randomUUID().toString());
            rpcRequest.setCreateMillisTime(System.currentTimeMillis());
            rpcRequest.setAccessToken(accessToken);
            rpcRequest.setClassName(className);
            rpcRequest.setMethodName(methodName);
            rpcRequest.setParameterTypes(parameterTypes);
            rpcRequest.setParameters(parameters);


            RpcResponse rpcResponse = doCall(finalAddress, rpcRequest, callType);

            Object rpcResult  = null;
            if (Objects.nonNull(rpcResponse))
            {
                rpcResult = rpcResponse.getResult();
            }
            return rpcResult;
        }
    }

    private RpcResponse syncCall(String finalAddress, RpcRequest rpcRequest) throws Throwable
    {
        RpcFutureResponse futureResponse = new RpcFutureResponse(rpcRequest, null);
        RpcFutureResponsePool.getSingleton().setInvokerFuture(rpcRequest.getRequestId(), futureResponse);

        try {
            client.asyncSend(finalAddress, rpcRequest);

            // 此处block
            RpcResponse rpcResponse = futureResponse.get(timeout, TimeUnit.MILLISECONDS);
            if (rpcResponse.getErrorMsg() != null) {
                throw new YoyoRpcException(rpcResponse.getErrorMsg());
            }
            return rpcResponse;
        } catch (Exception e) {
            log.info(" yoyo-rpc, invoke error, address:{}, RpcRequest{}", finalAddress, rpcRequest);

            throw (e instanceof YoyoRpcException) ? e : new YoyoRpcException(e);
        } finally {
            // future-response remove
            //futureResponse.removeInvokerFuture();
            RpcFutureResponsePool.getSingleton().setInvokerFuture(rpcRequest.getRequestId(), futureResponse);
        }
    }

    private RpcResponse doCall(String finalAddress, RpcRequest rpcRequest, CallType callType) throws Throwable
    {
        if (CallType.SYNC == callType) {
            return syncCall(finalAddress, rpcRequest);

        }else if (CallType.FUTIRE == callType) {
            //todo add future
            throw new YoyoRpcException("yoyo-rpc callType[" + callType + "] do not support now");
        }else if (CallType.CALLBACK == callType) {
            //todo add callBack
            return null;
        } else if (CallType.ONE_WAY == callType) {
            client.asyncSend(finalAddress, rpcRequest);
            return null;
        } else {
            throw new YoyoRpcException("yoyo-rpc callType[" + callType + "] do not support now");
        }

    }
}
