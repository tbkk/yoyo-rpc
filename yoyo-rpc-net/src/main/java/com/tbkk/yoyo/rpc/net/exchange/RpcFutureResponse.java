package com.tbkk.yoyo.rpc.net.exchange;

import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;
import com.tbkk.yoyo.rpc.net.client.callback.RpcInvokeCallback;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * call back future
 *
 * @author tbkk 2019-11-5 14:26:37
 */
public class RpcFutureResponse implements Future<RpcResponse> {

	private RpcRequest request;
	private RpcResponse response;


	private boolean done = false;
	private Object lock = new Object();

	@Getter
	@Setter
	private RpcInvokeCallback invokeCallback;


	public RpcFutureResponse(RpcRequest request, RpcInvokeCallback invokeCallback) {
		this.request = request;
		this.invokeCallback = invokeCallback;
	}


	// ---------------------- for invoke back ----------------------

	public void setResponseAndNotify(RpcResponse response) {
		this.response = response;
		synchronized (lock) {
			done = true;
			lock.notifyAll();
		}
	}


	// ---------------------- for invoke ----------------------

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		// TODO
		return false;
	}

	@Override
	public boolean isCancelled() {
		// TODO
		return false;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public RpcResponse get() throws InterruptedException, ExecutionException {
		try {
			return get(-1, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			throw new YoyoRpcException(e);
		}
	}

	@Override
	public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (!done) {
			synchronized (lock) {
				try {
					if (timeout < 0) {
						lock.wait();
					} else {
						long timeoutMillis = (TimeUnit.MILLISECONDS==unit)?timeout:TimeUnit.MILLISECONDS.convert(timeout , unit);
						lock.wait(timeoutMillis);
					}
				} catch (InterruptedException e) {
					throw e;
				}
			}
		}

		if (!done) {
			throw new YoyoRpcException("yoyo-rpc, request timeout at:"+ System.currentTimeMillis() +", request:" + request.toString());
		}
		return response;
	}


}
