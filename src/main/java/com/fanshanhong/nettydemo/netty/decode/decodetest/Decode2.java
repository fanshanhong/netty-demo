package com.fanshanhong.nettydemo.netty.decode.decodetest;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-08-05 22:42
 * @Modify:
 */
public class Decode2 extends MessageToMessageDecoder<Integer> {
    @Override
    protected void decode(ChannelHandlerContext ctx, Integer msg, List<Object> out) throws Exception {
        out.add(msg);
        out.add(999);
        out.add(888);

    }
}
