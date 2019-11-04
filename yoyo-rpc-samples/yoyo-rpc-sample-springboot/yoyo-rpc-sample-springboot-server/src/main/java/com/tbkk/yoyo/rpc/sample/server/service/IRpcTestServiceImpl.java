package com.tbkk.yoyo.rpc.sample.server.service;

import com.tbkk.yoyo.rpc.sample.api.IRpcTestService;
import com.tbkk.yoyo.rpc.sample.api.dto.UserDTO;
import com.tbkk.yoyo.rpc.springsupport.annotation.YoyoRpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;

/**
 * @author tbkk
 */
@YoyoRpcService
@Service
@Slf4j
public class IRpcTestServiceImpl implements IRpcTestService {

    @Override
    public UserDTO sayHi(String name) {

        String word = MessageFormat.format("Hi {0}, from {1} as {2}",
                name, IRpcTestServiceImpl.class.getName(), LocalDateTime.now().toString());


        if ("error".equalsIgnoreCase(name)){
            throw new RuntimeException("test exception.");
        }

        UserDTO userDTO = new UserDTO(name, word);
        log.info(userDTO.toString());

        return userDTO;
    }

}
