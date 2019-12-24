# Yoyo-RPC

# Overview


# Features
- Create distributed services without writing extra code.
- Provides cluster support and integrate with popular service discovery services like [Zookeeper][zookeeper]. 
- Supports advanced scheduling features like weighted load-balance, scheduling cross IDCs, etc.
- Optimization for high load scenarios, provides high availability in production environment.
- Supports both synchronous and asynchronous calls.
- Support cross-language interactive with Golang, PHP, Lua(Luajit), etc.

# Quick Start

> The minimum requirements to run the quick start are: 
>  * JDK 1.8
>  * A java-based project management software like [Maven][maven] or [Gradle][gradle]

## Synchronous calls

1. Add dependencies to pom.

```xml
    <dependency>
        <groupId>com.tbkk.yoyo</groupId>
        <artifactId>yoyo-rpc-springsupport</artifactId>
        <version>1.0.2-SNAPSHOT</version>
    </dependency>
```

2. Create an interface for both service provider and consumer.

    `com.tbkk.yoyo.rpc.sample.api`  

    ```java
    package com.tbkk.yoyo.rpc.sample.api;

    import com.tbkk.yoyo.rpc.sample.api.dto.UserDTO;

    /**
    * Demo API
    */
    public interface IRpcTestService {

        public UserDTO sayHi(String name);

    }
    ```

3. Write an implementation, create and start RPC Server.
        
    `com.tbkk.yoyo.rpc.sample.server.service.IRpcTestServiceImpl.java`
    ```java
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
    ```
    
     `com.tbkk.yoyo.rpc.sample.server.conf.YoyoRpcSpringProviderFactory.java`
    
    ```java
    @Configuration
    @Slf4j
    public class YoyoRpcProviderConfig {

        @Value("${yoyo-rpc.registry.address}")
        private String address;

        @Value("${yoyo-rpc.registry.env}")
        private String env;

        @Bean
        public YoyoRpcSpringProviderFactory yoyoRpcSpringProviderFactory() {

            RegisterCenter registerCenter = new ZkRegisterCenter();
            registerCenter.setRegisterParam(new HashMap<String, String>(){{
                put(ZkRegisterCenter.ZK_ADDRESS, address);
                put(ZkRegisterCenter.ENV, env);
            }});

            YoyoRpcProviderFactory yoyoRpcProviderFactory = new YoyoRpcProviderFactory(registerCenter);

            log.info(" yoyo-rpc provider config init finish.");
            return new YoyoRpcSpringProviderFactory(yoyoRpcProviderFactory);
        }

    }
    ```

    ```yml
    yoyo-rpc:
        registry:
            address: 127.0.0.1:2181
            env: dev
    ```

4. Create and start RPC Client.


    ```yml
    yoyo-rpc:
        registry:
            address: 127.0.0.1:2181
            env: dev
    ```

    `com.tbkk.yoyo.rpc.sample.client.conf.YoyoRpcInvokerConfig.java`

    ```java
    @Configuration
    @Slf4j
    public class YoyoRpcInvokerConfig {

        @Value("${yoyo-rpc.registry.address}")
        private String address;

        @Value("${yoyo-rpc.registry.env}")
        private String env;


        @Bean
        public YoyoRpcSpringInvokerFactory yoyoJobExecutor() {


            RegisterCenter registerCenter = new ZkRegisterCenter();
            registerCenter.setRegisterParam(new HashMap<String, String>(){{
                put(ZkRegisterCenter.ZK_ADDRESS, address);
                put(ZkRegisterCenter.ENV, env);
            }});

            YoyoRpcInvokerFactory yoyoRpcInvokerFactory = new YoyoRpcInvokerFactory(registerCenter);

            YoyoRpcSpringInvokerFactory invokerFactory = new YoyoRpcSpringInvokerFactory(yoyoRpcInvokerFactory);

            log.info(" yoyo-rpc invoker config init finish.");
            return invokerFactory;
        }

    }
    ```
    
    Execute main function in Client will invoke the remote service and print response.

##  Asynchronous calls

    ```java
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
    ```


# Documents

* [Wiki(中文)](https://github.com/tbkk/yoyo-rpc/wiki/cn_overview)

# Contributors

* tbkk([@tbkk](https://github.com/tbkk)) &nbsp;&nbsp;&nbsp; 极简.易用;

# License

Motan is released under the [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).

[maven]:https://maven.apache.org
[gradle]:http://gradle.org
[consul]:http://www.consul.io
[zookeeper]:http://zookeeper.apache.org


