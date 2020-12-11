package com.fanshanhong.nettydemo.netty.simple;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-30 14:02
 * @Modify:
 */
public class ServerSimpleHandler2 extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception { // PooledUnsafeDirectByteBuf(freed)
        System.out.println(ReferenceCountUtil.refCnt(msg) + "   " + msg.refCnt());
        System.out.println(msg.toString(Charset.forName("UTF-8")));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//
//                }
//                System.out.println(ReferenceCountUtil.refCnt(msg) + "   " + msg.refCnt());
//
//                //下面的两行代码都会报错：Exception in thread "Thread-1" io.netty.util.IllegalReferenceCountException: refCnt: 0
//                //就是因为 msg 已经被释放了。虽然断点看的时候， msg里面的一些属性还是有值的， 但是调用就报错。
//                //long l = msg.memoryAddress();
//                //byte b = msg.readByte();
//
//                //客户端不会收到， 并且报错：io.netty.util.IllegalReferenceCountException: refCnt: 0, decrement: 1,并且客户端与客户端的连接channel 会被自动关闭了
//                ctx.writeAndFlush(msg);
//            }
//        }).start();


        // 不管是采用注释里面写的方法， 延时 两秒发送， 主要为了等finally调用：ReferenceCountUtil.release(msg); 完成
        // 还是下面的直接调用 ctx.write(msg); 客户端都是无法收到消息的。
        // 因为write 是异步的， 调用的时候， msg底层的ByteBuf可能已经被释放掉了。
        // 客户端能收到， 但是报错：io.netty.util.IllegalReferenceCountException: refCnt: 0, decrement: 1, 与客户端连接的channel 没有被关闭
//        ctx.writeAndFlush(Unpooled.wrappedBuffer("123".getBytes()));
        ctx.channel().writeAndFlush(Unpooled.wrappedBuffer("对接".getBytes()));
    }
}
