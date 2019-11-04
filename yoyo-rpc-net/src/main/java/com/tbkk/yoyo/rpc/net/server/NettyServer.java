package com.tbkk.yoyo.rpc.net.server;


import com.tbkk.yoyo.rpc.common.utils.ThreadPoolUtil;
import com.tbkk.yoyo.rpc.net.Server;
import com.tbkk.yoyo.rpc.net.codec.NettyDecoder;
import com.tbkk.yoyo.rpc.net.codec.NettyEncoder;
import com.tbkk.yoyo.rpc.net.exchange.RpcRequest;
import com.tbkk.yoyo.rpc.net.exchange.RpcResponse;
import com.tbkk.yoyo.rpc.net.serializer.AbstractSerializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * netty rpc server
 *
 * @author tbkk 2019-10-29 18:17:14
 */
@Slf4j
public class NettyServer extends Server {

    //private Thread thread;
    private ExecutorService nettyServerSingleThreadExecutor;

    private final AbstractSerializer serializer = AbstractSerializer.SerializeEnum.PROTOSTUFF.getSerializer();

    @Override
    public void start(int port) throws Exception {

        nettyServerSingleThreadExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.setName("netty-server");
            return thread;
        });

        nettyServerSingleThreadExecutor.execute(() -> {
            final ThreadPoolExecutor serverHandlerPool = ThreadPoolUtil.makeServerThreadPool(NettyServer.class.getSimpleName());
            EventLoopGroup bossGroup = new NioEventLoopGroup();
            EventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                ServerBootstrap bootstrap = new ServerBootstrap();
                bootstrap.group(bossGroup, workerGroup)
                        .channel(NioServerSocketChannel.class)
                        .childHandler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            public void initChannel(SocketChannel channel) throws Exception {
                                channel.pipeline()
                                        .addLast(new IdleStateHandler(0,0,10, TimeUnit.MINUTES))
                                        .addLast(new NettyDecoder(RpcRequest.class, serializer))
                                        .addLast(new NettyEncoder(RpcResponse.class, serializer))
                                        .addLast(new NettyServerHandler(serverHandlerPool));
                            }
                        })
                        .childOption(ChannelOption.TCP_NODELAY, true)
                        .childOption(ChannelOption.SO_KEEPALIVE, true);

                // bind
                ChannelFuture future = bootstrap.bind(port).sync();

                log.info(" yoyo-rpc remoting server start success, nettype = {}, port = {}", NettyServer.class.getName(), port);
                startCallBack();

                // wait util stop
                future.channel().closeFuture().sync();

            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    log.info(" yoyo-rpc remoting server stop.");
                } else {
                    log.error(" yoyo-rpc remoting server error.", e);
                }
            } finally {

                // stop
                try {
                    serverHandlerPool.shutdown();    // shutdownNow
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                try {
                    workerGroup.shutdownGracefully();
                    bossGroup.shutdownGracefully();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

            }
        });

//        thread = new Thread(() -> {
//        });
//        thread.setDaemon(true);
//        thread.start();

    }

    @Override
    public void stop() throws Exception {

        // destroy server thread
//        if (thread != null && thread.isAlive()) {
//            thread.interrupt();
//        }
        nettyServerSingleThreadExecutor.shutdown();

        // on stop
        stopCallBack();
        log.info(" yoyo-rpc remoting server destroy success.");
    }

}
