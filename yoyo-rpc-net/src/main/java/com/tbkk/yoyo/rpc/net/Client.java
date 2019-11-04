package com.tbkk.yoyo.rpc.net;

import com.tbkk.yoyo.rpc.net.exchange.RpcRequest;
import com.tbkk.yoyo.rpc.net.exchange.RpcResponse;
import lombok.extern.slf4j.Slf4j;


/**
 * @author tbkk
 */
public interface Client {

	/**
	 * async send, bind requestId and future-response
	 *
	 * @param address
	 * @param rpcRequest
	 * @return
	 * @throws Exception
	 */
	public void asyncSend(String address, RpcRequest rpcRequest) throws Exception;

	public RpcResponse syncSendAndRecv(String address, RpcRequest rpcRequest) throws Exception;

}
