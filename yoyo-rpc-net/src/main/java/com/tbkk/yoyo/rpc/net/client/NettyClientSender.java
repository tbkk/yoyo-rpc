package com.tbkk.yoyo.rpc.net.client;


import com.tbkk.yoyo.rpc.net.Client;
import com.tbkk.yoyo.rpc.net.client.connect.ConnectClientPool;
import com.tbkk.yoyo.rpc.net.exchange.RpcRequest;
import com.tbkk.yoyo.rpc.net.exchange.RpcResponse;

/**
 * netty client
 *
 * @author tbkk 2019-11-24 22:25:15
 */
public class NettyClientSender implements Client {

	@Override
	public void asyncSend(String address, RpcRequest rpcRequest) throws Exception {
		ConnectClientPool.getSingleton().asyncSend(rpcRequest, address);
	}

	@Override
	public RpcResponse syncSendAndRecv(String address, RpcRequest rpcRequest) throws Exception {
		return null;
	}

}
