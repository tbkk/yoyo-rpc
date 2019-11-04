package com.tbkk.yoyo.rpc.loadbalance;

import com.tbkk.yoyo.rpc.loadbalance.route.*;

/**
 * @ tbkk
 */

public enum LoadBalance {

    RANDOM(new YoyoRpcLoadBalanceRandomStrategy()),
    ROUND(new YoyoRpcLoadBalanceRoundStrategy()),
    LRU(new YoyoRpcLoadBalanceLRUStrategy()),
    LFU(new YoyoRpcLoadBalanceLFUStrategy()),
    CONSISTENT_HASH(new YoyoRpcLoadBalanceConsistentHashStrategy());


    public final YoyoRpcLoadBalance yoyoRpcInvokerRouter;

    private LoadBalance(YoyoRpcLoadBalance yoyoRpcInvokerRouter) {
        this.yoyoRpcInvokerRouter = yoyoRpcInvokerRouter;
    }


    public static LoadBalance match(String name, LoadBalance defaultRouter) {
        for (LoadBalance item : LoadBalance.values()) {
            if (item.equals(name)) {
                return item;
            }
        }
        return defaultRouter;
    }



    /*public static void main(String[] args) {
        String serviceKey = "service";
        TreeSet<String> addressSet = new TreeSet<String>(){{
            add("1");
            add("2");
            add("3");
            add("4");
            add("5");
        }};

        for (LoadBalance item : LoadBalance.values()) {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 100000; i++) {
                String address = LoadBalance.LFU.yoyoRpcInvokerRouter.route(serviceKey, addressSet);
                //System.out.println(address);;
            }
            long end = System.currentTimeMillis();
            System.out.println(item.name() + " --- " + (end-start));
        }

    }*/


}