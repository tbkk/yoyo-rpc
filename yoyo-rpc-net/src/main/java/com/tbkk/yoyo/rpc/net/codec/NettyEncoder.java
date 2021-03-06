package com.tbkk.yoyo.rpc.net.codec;

import com.tbkk.yoyo.rpc.net.serializer.AbstractSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * encoder
 *
 * @author tbkk 2019-10-29 19:43:00
 */
public class NettyEncoder extends MessageToByteEncoder<Object> {

    private Class<?> genericClass;
    private AbstractSerializer serializer;

    public NettyEncoder(Class<?> genericClass, final AbstractSerializer serializer) {
        this.genericClass = genericClass;
        this.serializer = serializer;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = serializer.serialize(in);
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}