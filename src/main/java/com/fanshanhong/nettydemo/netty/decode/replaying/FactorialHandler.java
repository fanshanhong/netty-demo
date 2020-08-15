package com.fanshanhong.nettydemo.netty.decode.replaying;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AttributeKey;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-08-13 09:57
 * @Modify:
 */
public class FactorialHandler extends ChannelInboundHandlerAdapter {
    private final AttributeKey<Integer> counter = AttributeKey.valueOf("counter");

    // This handler will receive a sequence of increasing integers starting
    // from 1.
    @Override
    public void channelRead(ChannelHandlerContext
                                    ctx, Object msg) {

        // when a client connect to server, and send a message to here, the ctx.attr(counter) info is DefaultAttributeMap$DefaultAttribute@1530
        // in the same channel, the ctx.attr(counter) info is immutable;
        // then, a another client connect to server , and send a message to here, the ctx.attr(counter) info is DefaultAttributeMap$DefaultAttribute@1567
        Integer a = ctx.attr(counter).get();

        System.out.println(ctx.channel() + " " + System.identityHashCode(ctx.channel()) + "  " + ctx.attr(counter).toString() + " " + System.identityHashCode(ctx.attr(counter)));

        if (a == null) {
            a = 1;
        }

        ctx.channel().attr(counter).set(a * (Integer) msg);
        System.out.println(ctx.channel().attr(counter).get());

        ctx.fireChannelRead(msg);
    }
}
