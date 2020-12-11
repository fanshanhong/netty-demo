package com.fanshanhong.nettydemo.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-30 14:02
 * @Modify:
 */
public class ServerSimpleHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    // 读取数据
    // 管道(Pipeline)里有很多Handler(双向链表), 管道说的是业务逻辑处理管道. 类似于责任链模式
    // 通道 其实指的是与 客户端建立的socket连接.
    @Override
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println(ctx);

        // 网络上的数据一到达Netty, 就被封装成 ByteBuf了.
        // 我们这边没有其他的编解码器了. 因此这个 Object msg 实际上是 ByteBuf 类型
        // ByteBuf 是 Netty 对NIO中ByteBuffer的封装(性能更好)
        ByteBuf byteBuf = (ByteBuf) msg;

        System.out.println("客户端" + ctx.channel().remoteAddress() + "发过来消息:" + byteBuf.toString(Charset.forName("UTF-8")));

        System.out.println("channelRead 打印当前线程:" + Thread.currentThread().getName());

        // 如果执行耗时任务, 就这样做:
        // 将任务提交到channel对应的NioEventLoop的TaskQueue中去异步执行
        ctx.channel().eventLoop().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("execute 打印当前线程:" + Thread.currentThread().getName());
                    Thread.sleep(10*1000); // 模拟耗时任务
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ctx
                        .writeAndFlush(Unpooled.copiedBuffer("耗时任务".getBytes(CharsetUtil.UTF_8)));
            }
        });


        // 定时任何, 会提交到:ScheduleTaskQueue  任务队列中, 有上面的TaskQueue不是一个
        ctx.channel().eventLoop().schedule(new Runnable() {
            @Override
            public void run() {

            }
        }, 5, TimeUnit.SECONDS);

        /// 因为只有一个线程在执行, 所以提交到TaskQueue 和  ScheduleTaskQueue 中的任务都是串行执行的. 这里需要延迟的时间应该是:   上面的10秒+自己的延迟的5秒=15秒

    }

    // 消息读完回调
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        // 下面这两个有什么区别
        ctx.writeAndFlush(Unpooled.copiedBuffer("哈哈哈哈".getBytes(CharsetUtil.UTF_8)));
        //ctx.channel().writeAndFlush()

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx);
    }

    @Override
    protected void ensureNotSharable() {
        super.ensureNotSharable();
    }

    @Override
    public boolean isSharable() {
        return super.isSharable();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
    }
}
