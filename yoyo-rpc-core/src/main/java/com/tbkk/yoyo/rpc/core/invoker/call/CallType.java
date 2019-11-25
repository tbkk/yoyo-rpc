package com.tbkk.yoyo.rpc.core.invoker.call;

/**
 * rpc call type
 *
 * @author tbkk 2019-10-19
 */
public enum CallType {


    SYNC,

    FUTIRE,

    CALLBACK,

    ONE_WAY;


    public static CallType match(String name, CallType defaultCallType){
        for (CallType item : CallType.values()) {
            if (item.name().equals(name)) {
                return item;
            }
        }
        return defaultCallType;
    }

}
