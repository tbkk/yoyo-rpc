package com.tbkk.yoyo.rpc.test;


import com.tbkk.yoyo.rpc.registry.utils.YoyoZkClient;

import java.util.concurrent.TimeUnit;

public class YoyoZkClientTest {

    public static void main(String[] args) throws InterruptedException {

        YoyoZkClient client = null;
        try {
            client = new YoyoZkClient("10.80.179.166:2181", "/yoyo-rpc/test", null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }


        for (int i = 0; i < 100; i++) {
            System.out.println("------------- " + i);
            try {
                System.out.println(client.getClient());
            } catch (Exception e) {
                e.printStackTrace();
            }
            TimeUnit.SECONDS.sleep(5);
        }

    }

}