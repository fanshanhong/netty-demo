package com.fanshanhong.nettydemo.netty.decode.decodetest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-08-05 22:42
 * @Modify:
 */
public class Decode1 extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if(in.readableBytes() >= 4) {
            out.add(in.readInt());
        }
    }
}