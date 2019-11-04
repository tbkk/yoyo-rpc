package com.tbkk.yoyo.rpc.net.serializer.impl;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.tbkk.yoyo.rpc.common.exception.YoyoRpcException;
import com.tbkk.yoyo.rpc.net.serializer.AbstractSerializer;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * hessian serialize
 * @author tbkk 2019-9-26 02:53:29
 */
public class HessianSerializer extends AbstractSerializer {

	@Override
	public <T> byte[] serialize(T obj){
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Hessian2Output ho = new Hessian2Output(os);
		try {
			ho.writeObject(obj);
			ho.flush();
			byte[] result = os.toByteArray();
			return result;
		} catch (IOException e) {
			throw new YoyoRpcException(e);
		} finally {
			try {
				ho.close();
			} catch (IOException e) {
				throw new YoyoRpcException(e);
			}
			try {
				os.close();
			} catch (IOException e) {
				throw new YoyoRpcException(e);
			}
		}

	}

	@Override
	public <T> Object deserialize(byte[] bytes, Class<T> clazz) {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		Hessian2Input hi = new Hessian2Input(is);
		try {
			Object result = hi.readObject();
			return result;
		} catch (IOException e) {
			throw new YoyoRpcException(e);
		} finally {
			try {
				hi.close();
			} catch (Exception e) {
				throw new YoyoRpcException(e);
			}
			try {
				is.close();
			} catch (IOException e) {
				throw new YoyoRpcException(e);
			}
		}
	}
	
}
