package com.fanshanhong.nettydemo.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-08-18 10:37
 * @Modify:
 */
public class ByteBufTest {
    public static void main(String[] args) {

        // 创建容量大小为10的buffer
        ByteBuf byteBuf = Unpooled.buffer(10);
        // 创建一个默认容量的buffer
        ByteBuf byteBuf1 = Unpooled.buffer();
        ByteBuf byteBuf2 = Unpooled.wrappedBuffer("123".getBytes());
        ByteBuf byteBuf3 = Unpooled.copiedBuffer("123".getBytes());

        // Unpooled 非池化的
        // Pooled   池化的
        byteBuf3.resetReaderIndex();

        for (int i = 0; i < 10; i++) {
            byteBuf.writeByte(i); // 相对写, 会修改writerIndex
        }
        System.out.println("readerIndex:" + byteBuf.readerIndex() + "   " + "writerIndex:" + byteBuf.writerIndex());


        for (int i = 0; i < byteBuf.readableBytes(); i++) {
            System.out.println("绝对读:" + byteBuf.getByte(i)); // 绝对读, 不修改readerIndex
            System.out.println("相对读:" + byteBuf.readByte()); // 相对读, 修改readerIndex
        }


        // 如果读取操作的参数也是* {@link ByteBuf}并且未指定目标索引，则指定的缓冲区的{@link #writerIndex（）writerIndex}会一起增加。
        //byteBuf.readBytes(byteBuf1)  ;
    }
}
