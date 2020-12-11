package com.fanshanhong.nettydemo.netty.simple;

import com.fanshanhong.nettydemo.netty.write.MyOutboundHandler;
import com.fanshanhong.nettydemo.netty.write.MyOutboundHandler2;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-30 11:24
 * @Modify:
 */
public class NettySimpleServer {
    public static void main(String[] args) {

        // bossGroup 只负责处理连接请求
        // workerGroup 负责与客户端的读写和业务处理
        // 都是死循环
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap serverBootstrap = new ServerBootstrap();

            // 服务器端相关配置
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)// 指定 bossGroup 使用 NioServerSocketChannel 来处理连接请求
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new ServerSimpleHandler());
                            ch.pipeline().addLast(new MyOutboundHandler());
                            ch.pipeline().addLast(new ServerSimpleHandler2());
                            ch.pipeline().addLast(new MyOutboundHandler2()); // 2 是出站的最后
                        }
                    });

            // 绑定端口并且同步处理
            // 这里启动了服务器
            ChannelFuture channelFuture = serverBootstrap.bind(10010).sync();

            // 对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
