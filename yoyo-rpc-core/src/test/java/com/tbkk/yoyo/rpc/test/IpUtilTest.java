package com.tbkk.yoyo.rpc.test;

import com.tbkk.yoyo.rpc.common.utils.IpUtil;

import java.net.UnknownHostException;

/**
 * @author tbkk 2019-10-23
 */
public class IpUtilTest {

    public static void main(String[] args) throws UnknownHostException {
        System.out.println(IpUtil.getIp());
        System.out.println(IpUtil.getIpPort(8080));
    }

}
