package com.tbkk.yoyo.rpc.springsupport.annotation;

import com.tbkk.yoyo.rpc.loadbalance.LoadBalance;
import com.tbkk.yoyo.rpc.net.NetEnum;
import com.tbkk.yoyo.rpc.net.serializer.AbstractSerializer;
import com.tbkk.yoyo.rpc.core.invoker.call.CallType;


import java.lang.annotation.*;


/**
 * @author tbkk
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface YoyoRpcReference {

    CallType callType() default CallType.SYNC;
    LoadBalance loadBalance() default LoadBalance.ROUND;
    String version() default "";
    long timeout() default 1000;
    String address() default "";
    String accessToken() default "";

}
