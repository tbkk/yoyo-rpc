package com.tbkk.yoyo.rpc.sample.client.controller;

import com.tbkk.yoyo.rpc.sample.api.IRpcTestService;
import com.tbkk.yoyo.rpc.sample.api.dto.UserDTO;
import com.tbkk.yoyo.rpc.springsupport.annotation.YoyoRpcReference;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author tbkk
 */
@RestController
@RequestMapping("/rpc-test")
@Slf4j
@Api("测试命令")
public class IndexController {
	
	@YoyoRpcReference
	private IRpcTestService rpcTestService;



	@ResponseBody
	@ApiOperation(value = "rpc测试")
	@RequestMapping(value = "/doTest", method = RequestMethod.POST)
	public UserDTO http(String name) {
		long startTime = System.currentTimeMillis();
		try {
			for (int i = 0; i < 999; i++)
			{
				rpcTestService.sayHi(name);
			}
			UserDTO userDTO = rpcTestService.sayHi(name);
			long spendTime = System.currentTimeMillis() - startTime;
			log.info("##: {}, doTest finished Spend Time{} ", name, spendTime);
			return userDTO;
		} catch (Exception e) {
			e.printStackTrace();
			return new UserDTO("", e.getMessage());
		}
	}

}
