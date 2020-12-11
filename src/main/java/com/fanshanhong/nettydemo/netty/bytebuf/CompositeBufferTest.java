package com.fanshanhong.nettydemo.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;

import java.util.Iterator;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-08-19 22:41
 * @Modify:
 */
public class CompositeBufferTest {
    public static void main(String[] args) {
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer(); // CompositeByteBuf类型

        // 创建一个堆缓冲区
        ByteBuf heapBuffer = Unpooled.buffer();// 类型:UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf(ridx: 0, widx: 0, cap: 256)
        heapBuffer.writeByte('a');
        heapBuffer.readerIndex(2);
        heapBuffer.writerIndex(2);
        heapBuffer.readByte();

        // 创建一个直接缓冲
        ByteBuf directBuffer = Unpooled.directBuffer();// UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(ridx: 0, widx: 0, cap: 256)
        directBuffer.writeByte('b');

        System.out.println(heapBuffer);
        System.out.println(directBuffer);

        compositeByteBuf.addComponent(heapBuffer);
        compositeByteBuf.addComponent(directBuffer);


        //compositeByteBuf.removeComponent(0);

        // 遍历
        Iterator<ByteBuf> iterator = compositeByteBuf.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
            // UnpooledSlicedByteBuf(ridx: 0, widx: 0, cap: 0/0, unwrapped: UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeHeapByteBuf(ridx: 0, widx: 0, cap: 256))
            // UnpooledSlicedByteBuf(ridx: 0, widx: 0, cap: 0/0, unwrapped: UnpooledByteBufAllocator$InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf(ridx: 0, widx: 0, cap: 256))
        }


        // 报错,Exception in thread "main" java.lang.UnsupportedOperationException  原因:
        byte[] array = compositeByteBuf.array();

        /*
         * @Override
         *         public byte[] array() {
         *             switch (componentCount) {
         *                 case 0:
         *                     return EmptyArrays.EMPTY_BYTES;
         *                 case 1:
         *                     return components[0].buf.array();
         *                 default:
         *                     throw new UnsupportedOperationException();
         *             }
         *         }
         */


    }
}
