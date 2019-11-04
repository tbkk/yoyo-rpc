package com.tbkk.yoyo.rpc.sample.api;

import com.tbkk.yoyo.rpc.sample.api.dto.UserDTO;

/**
 * Demo API
 */
public interface IRpcTestService {

	public UserDTO sayHi(String name);

}
