package com.fanshanhong.nettydemo.netty.decode.replaying;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-08-06 10:24
 * @Modify:
 */
public class MyReplayingDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
//        if(in.readableBytes() > 4) {
//            out.add(in.readInt());
//        }

        if(in.readableBytes() > 1) {
            byte b = in.readByte();
            out.add(Integer.valueOf(String.valueOf(b)));
        }

    }
}


/**
 * MessageToMessageDecoder 解码, 入站
 * 泛型 Long 是输入的类型
 *
 */
class A extends MessageToMessageDecoder<Long> {

    @Override
    protected void decode(ChannelHandlerContext ctx, Long msg, List<Object> out) throws Exception {

    }
}

/**
 * MessageToMessageEncoder 编码, 出站
 * 泛型 Double 是输入类型
 *
 */
class B extends MessageToMessageEncoder<Double> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Double msg, List<Object> out) throws Exception {

    }
}
