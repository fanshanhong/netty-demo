package com.fanshanhong.nettydemo.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-30 14:31
 * @Modify:
 */
public class ClientSimpleHandler extends ChannelInboundHandlerAdapter {

    // 当通道就绪就会触发
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelActive");
        try {
            new Thread(new Runnable() {
                public void run() {
                    Scanner scanner = new Scanner(System.in);
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.startsWith("##stop")) {
                            ctx.close();
                            return;
                        }
                        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes(CharsetUtil.UTF_8)));
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            ctx.close();
        }
    }

    // 有数据可读
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf byteBuf = (ByteBuf) msg;

        System.out.println("收到服务器的消息:" + byteBuf.toString(Charset.forName("UTF-8")));

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
