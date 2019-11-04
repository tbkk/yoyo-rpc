package com.tbkk.yoyo.rpc.net;


import com.tbkk.yoyo.rpc.net.client.NettyClientSender;
import com.tbkk.yoyo.rpc.net.server.NettyServer;

/**
 * remoting net
 *
 * @author tbkk 2019-11-24 22:09:57
 */
public enum NetEnum {


	/**
	 * netty tcp server
	 */
	NETTY(NettyServer.class, NettyClientSender.class),

	;


	public final Class<? extends Server> serverClass;
	public final Class<? extends Client> clientClass;

	NetEnum(Class<? extends Server> serverClass, Class<? extends Client> clientClass) {
		this.serverClass = serverClass;
		this.clientClass = clientClass;
	}

	public static NetEnum autoMatch(String name, NetEnum defaultEnum) {
		for (NetEnum item : NetEnum.values()) {
			if (item.name().equals(name)) {
				return item;
			}
		}
		return defaultEnum;
	}

}