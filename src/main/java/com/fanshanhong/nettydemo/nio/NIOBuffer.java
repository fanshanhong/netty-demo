package com.fanshanhong.nettydemo.nio;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.IntBuffer;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-23 15:50
 * @Modify:
 */
public class NIOBuffer {

    public static void main(String[] args) {

        // Buffer
        // Buffer 类定义了所有缓冲区都具有的4个属性，提供关于其所包含的数据元素信息。

        /*
         *     // Invariants: mark <= position <= limit <= capacity
         *     private int mark = -1;
         *     private int position = 0;
         *     private int limit;
         *     private int capacity;
         */

        // 真正的数据都是存在 hb数组里的.
        // IntBuffer 里有  int[] hb;
        // DoubleBuffer 里有  double[] hb;
        // CharBuffer 里有 char[] hb;


        // 用于存放整形的 Buffer
        IntBuffer intBuffer = IntBuffer.allocate(4);
        // allocate之后,  hb数据为: [0,0,0,0], position:0  limit:4  capacity:4


        /*
         *  *
         *  *       A buffer's capacity is the number of elements it contains.  The
         *  *   capacity of a buffer is never negative and never changes.
         *  *       buffer的 capacity 属性,表示这个buffer能装多少元素, 也就是容量大小.
         *  *   capacity 的值永远不会为负数, 也不会改变
         *  *
         *  *
         *  *      A buffer's limit is the index of the first element that should
         *  *   not be read or written.  A buffer's limit is never negative and is never
         *  *   greater than its capacity.
         *  *      buffer的 limit 属性, 表示不能被读或者被写的第一个元素的index. limit 永远小于等于 capacity
         *  *
         *  * 也就是超过limit的地方不能读写, 但是limit是可以变的, 我们可以直接修改limit
         *  *
         *  *      A buffer's position is the index of the next element to be
         *  *   read or written.  A buffer's position is never negative and is never
         *  *   greater than its limit.
         *  *      position 表示下一个将要被读/被写的index.  position永远不会是负数, 并且不会大于limit
         *  *
         */

        // 向Buffer中写入数据, put是相对操作, 每次put, position会自增
        intBuffer.put(1);// hb数据为: [1,0,0,0], position:1 limit:4 cap:4
        intBuffer.put(2);// hb数据为: [1,2,0,0], position:2 limit:4 cap:4
        intBuffer.put(3);// hb数据为: [1,2,3,0], position:3 limit:4 cap:4

        // 写完之后, 如果想要使用相对方法get, 就需要flip. 如果没有调用flip(), 会报错:BufferUnderflowException
        // 如果是使用绝对的get方法, 就不需要flip

        // 之前是写操作, 写完之后, position = 3;
        // flip方法中, limit = position; 表示下次读, 只能读到这里(limit=3). 因为之前写的时候只写了3个.
        // 然后 position = 0; 表示从头开始读

        /*
         *  limit = position;
         *  position = 0;
         *  mark = -1;
         */
        intBuffer.flip();

        System.out.println("intBuffer limit:" + intBuffer.limit()); // 3
        // 从Buffer读取数据
        for (int i = 0; i < intBuffer.limit(); i++) {
            int x = intBuffer.get();// Relative get method, position会每次向后移动
            System.out.println(x);
        }

        // 全部读完, 看看下标 position:3  limit:3  capacity:4


        intBuffer.position(0); // 把position修改到开头,又可以重新读一遍啦
        while (intBuffer.hasRemaining()) {
            int x = intBuffer.get();// Relative get method, position会每次向后移动
            System.out.println(x);
        }


        // 第二次全部读完, position:3  limit:3  capacity:4


        // 绝对get方法, position  limit  cap 下标都不变的.
        System.out.println("Absolute get method:" + intBuffer.get(1)); // Absolute get method
        System.out.println("Absolute get method:" + intBuffer.get(2)); // Absolute get method


        // 读完了, 此时还能再写么? flip一下就可以了么?
        intBuffer.flip();
        // flip之后,  position:0  limit:3  capacity:4

        intBuffer.put(444);// hb数据为: [444,2,3,0]
        intBuffer.put(555);// hb数据为: [444,555,0,0]
        intBuffer.put(666);// hb数据为: [444,555,666,0]

        // 此时, intBuffer中的 hb里面数据为   [444,555,666,0]
        // 此时, position:3  limit:3  capacity:4


        // Q:第四个(下标为3的元素)能put么?
        // A:由于limit是3, 第四个元素不能put了
        // intBuffer.put(777); // 这里会报错 BufferOverflowException
        // 但是, 我们可以通过手动修改 limit
        intBuffer.limit(4);
        intBuffer.put(777); // put是相对操作, 每次put, position会自增
        // 此时, hb里面数据为   [444,555,666,777], position:4  limit:4  capacity:4

        // 要读就要flip
        intBuffer.flip();
        while (intBuffer.hasRemaining()) {
            int x = intBuffer.get();// Relative get method, position会移动
            System.out.println("第二次写完:" + x);
        }


        // 总结: flip方法根本不是什么读写模式转换, 只是把几个下标的属性值改了改.


        // 下面演示一下wrap相关的方法


        // 用于存储 Char类型的Buffer

        char[] chars = {'a', 'b', '发', '看'};

        CharBuffer wrap = CharBuffer.wrap(chars);
        System.out.println("wrap:" + wrap.toString() + "  position:" + wrap.position() + "  limit:" + wrap.limit() + "  capacity:" + wrap.capacity());

        // 分析下这个wrap方法:
        // 创建了一个 HeapCharBuffer 对象
        // 并把chars 的内容全部装到  Buffer里面的hb(final char[] hb;)
        // 然后给 int mark, int pos=offset, int lim=offset+len, int cap  4个变量赋值
        /*
         *     CharBuffer(int mark, int pos, int lim, int cap,   // package-private
         *                  char[] hb, int offset)
         *     {
         *         super(mark, pos, lim, cap);
         *         this.hb = hb;
         *         this.offset = offset;
         *     }
         */

        // toString() 方法, 实质是从position->limit之间的内容
        /*
         * public String toString() {
         *         return toString(position(), limit());
         *     }
         *
         * abstract String toString(int start, int end);
         */

        // array() 方法, 是返回了整个 hb


        CharBuffer wrap1 = CharBuffer.wrap(chars, 1, 2);// 从 offset 开始, 往后 length 个
        System.out.println("wrap:" + wrap1.toString() + "  position:" + wrap1.position() + "  limit:" + wrap1.limit() + "  capacity:" + wrap1.capacity());
        System.out.println(new String(wrap1.array()));


        CharBuffer wrap2 = CharBuffer.wrap("模式转换");
        System.out.println("wrap:" + wrap2.toString() + "  position:" + wrap2.position() + "  limit:" + wrap2.limit() + "  capacity:" + wrap2.capacity());

        CharBuffer wrap3 = CharBuffer.wrap("flip方法根本不是什么读写模式转换", 0, 5); // 注意这两个参数是start 和 end, [0, 3)
        System.out.println("wrap:" + wrap3.toString() + "  position:" + wrap3.position() + "  limit:" + wrap3.limit() + "  capacity:" + wrap3.capacity());

        // 用于存储字节的 Buffer
        ByteBuffer byteBuffer = ByteBuffer.wrap("你好,中国".getBytes(), 0, 3);
        ByteBuffer byteBuffer2 = ByteBuffer.wrap("你好,中国".getBytes());

        // ByteBuffer的toString()方法被重写了
        System.out.println(byteBuffer.toString()); // java.nio.HeapByteBuffer[pos=0 lim=3 cap=13]
        System.out.println(byteBuffer2.toString());// java.nio.HeapByteBuffer[pos=0 lim=13 cap=13]
        // tip:UTF-8编码, 一个中文占3个字节.

        System.out.println(new String(byteBuffer.array())); // 你好,中国, array()方法是获取整个hb
        System.out.println(new String(byteBuffer2.array()));


        // 我想看看byteBuffer   position->limit之间的内容是啥, 用哪个方法啊...
        byte[] dest = new byte[4];
        byteBuffer.get(dest, 1, 3); // 这里没从dest的0开始写,  所以0位置处有个空的.
        System.out.println(new String(dest));

        // byteBuffer.get(dest, 1, 3); 是做了什么呢
        // 主要就是 从position位置(这个position位置是wrap方法中指定的offset参数. (因为wrap方法中, 将 position = offset 了))开始多次调用get()方法, 把get到的内容丢到dst数组指定的区间里面(这里是 从1开始, 长度是3)

        /*
         *  public ByteBuffer get(byte[] dst, int offset, int length) {
         *         checkBounds(offset, length, dst.length);
         *         if (length > remaining())
         *             throw new BufferUnderflowException();
         *         int end = offset + length;
         *         for (int i = offset; i < end; i++)
         *             dst[i] = get();
         *         return this;
         *  }
         */


        // clear方法
        // Clears this buffer.  The position is set to zero, the limit is set to the capacity, and the mark is discarded.
        // This method does not actually erase the data in the buffer
        // 只是修改几个属性, 并没有真正的清除掉底层hb数组里面的内容
        // 比如我们调用完 clear()方法, 再通过array()获取底层hb数组里的内容,肯定还是可以获取到的
        byteBuffer.clear();

        System.out.println("clear之后:" + new String(byteBuffer.array()));


        ByteBuffer readOnlyBuffer = byteBuffer.asReadOnlyBuffer();

        // 这里byteBuffer 的类型是HeapByteBuffer, 因此是调用HeapByteBuffer的asReadOnlyBuffer方法
        // 返回的readOnlyBuffer 类型是 HeapByteBufferR  最后的R表示只读.


        /*
         *     public ByteBuffer asReadOnlyBuffer() {
         *
         *         return new HeapByteBufferR(hb,
         *                                      this.markValue(),
         *                                      this.position(),
         *                                      this.limit(),
         *                                      this.capacity(),
         *                                      offset);
         *     }
         *
         */

        // 可以看到, asReadOnlyBuffer方法是使用原有buffer里的hb及其他一些属性, 又去创建了一个新的只读Buffer.
    }
}
