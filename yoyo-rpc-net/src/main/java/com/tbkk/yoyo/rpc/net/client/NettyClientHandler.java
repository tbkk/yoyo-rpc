package com.tbkk.yoyo.rpc.net.client;

import com.tbkk.yoyo.rpc.net.exchange.RpcFutureResponsePool;
import com.tbkk.yoyo.rpc.net.exchange.RpcResponse;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * rpc netty client handler
 *
 * @author tbkk 2019-10-31 18:00:27
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, RpcResponse rpcResponse) throws Exception {

		RpcFutureResponsePool.getSingleton().notifyInvokerFuture(rpcResponse.getRequestId(), rpcResponse);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error(" yoyo-rpc netty client caught exception", cause);
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent){
			ctx.channel().close();      // close idle channel
			log.debug(" yoyo-rpc netty client close an idle channel.");
		} else {
			super.userEventTriggered(ctx, evt);
		}
	}

}
