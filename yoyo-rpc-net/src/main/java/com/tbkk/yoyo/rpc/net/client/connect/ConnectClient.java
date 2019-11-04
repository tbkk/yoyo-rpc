package com.tbkk.yoyo.rpc.net.client.connect;


import com.tbkk.yoyo.rpc.net.exchange.RpcRequest;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tbkk 2019-10-19
 */
@Slf4j
public abstract class ConnectClient {

    // ---------------------- iface ----------------------

    public abstract void init(String address) throws Exception;

    public abstract void close();

    public abstract boolean isValidate();

    /**
     *
     * @param rpcRequest 发送rpc请求
     * @return void
     */
    public abstract void send(RpcRequest rpcRequest) throws Exception ;

    private ConnectClientPool connectClientPool = ConnectClientPool.getSingleton();

}
