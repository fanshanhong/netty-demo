package com.fanshanhong.nettydemo.netty.simple;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-30 11:23
 * @Modify:
 */
public class NettySimpleClient {
    public static void main(String[] args) {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();


       try {

           Bootstrap bootstrap = new Bootstrap();

           bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                   .handler(new ChannelInitializer<SocketChannel>() {
                       @Override
                       protected void initChannel(SocketChannel ch) throws Exception {
                           ch.pipeline().addLast(new ClientSimpleHandler());
                       }
                   });

           ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 10010).sync();

           channelFuture.channel().closeFuture().sync();

       }catch (Exception e) {
          e.printStackTrace();
       }finally {
           eventLoopGroup.shutdownGracefully();
       }
    }
}
