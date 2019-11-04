package com.tbkk.yoyo.rpc.net.exchange;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

/**
 * request
 *
 * @author tbkk 2019-10-29 19:39:12
 */
@Data
public class RpcRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String requestId = UUID.randomUUID().toString();
	private long createMillisTime = System.currentTimeMillis();
	private String accessToken = "";

    private String className = "";
    private String methodName = "";
    private Class<?>[] parameterTypes = null;
    private Object[] parameters = null;

	private String version = "";
}
