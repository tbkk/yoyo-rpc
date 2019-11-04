package com.tbkk.yoyo.rpc.net.exchange;

import lombok.Data;

import java.io.Serializable;

/**
 * response
 *
 * @author tbkk 2019-10-29 19:39:54
 */
@Data
public class RpcResponse implements Serializable{
	private static final long serialVersionUID = 1L;

	private String requestId;
    private String errorMsg;
    private Object result;
}
