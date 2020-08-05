package com.fanshanhong.nettydemo.netty.groupchat;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-08-05 09:48
 * @Modify:
 */

public class GroupChatServerHandler extends ChannelInboundHandlerAdapter {

    static Map<String, Channel> channelMap = new HashMap<>();

    // 也可以用这个ChannelGroup 来保存 channel 对象
    // ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端" + ctx.channel().remoteAddress() + "处于活动状态了");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("客户端" + ctx.channel().remoteAddress() + "处于不活动状态了");
        channelMap.remove(ctx.channel().id().asLongText());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("收到客户端" + ctx.channel().remoteAddress() + "的消息:" + msg);
        String message = ctx.channel().remoteAddress() + "说:" + (String) msg;

        // 转发
        Iterator<String> iterator = channelMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            Channel channel = channelMap.get(key);
            channel.writeAndFlush(message);
        }

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        System.out.println("处理器被添加了, 代表客户端" + ctx.channel().remoteAddress() + "加入群聊了" );
        channelMap.put(ctx.channel().remoteAddress().toString(), ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("处理器被移除了, 代表客户端" + ctx.channel().remoteAddress() + "离开群聊了");
        channelMap.remove(ctx.channel().remoteAddress().toString());
    }
}
