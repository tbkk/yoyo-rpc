package com.tbkk.yoyo.rpc.loadbalance.route;


import com.tbkk.yoyo.rpc.loadbalance.YoyoRpcLoadBalance;

import java.util.Random;
import java.util.TreeSet;

/**
 * random
 *
 * @author tbkk 2019-12-04
 */
public class YoyoRpcLoadBalanceRandomStrategy extends YoyoRpcLoadBalance {

    private Random random = new Random();

    @Override
    public String route(String serviceKey, TreeSet<String> addressSet) {
        // arr
        String[] addressArr = addressSet.toArray(new String[addressSet.size()]);

        // random
        String finalAddress = addressArr[random.nextInt(addressSet.size())];
        return finalAddress;
    }

}
