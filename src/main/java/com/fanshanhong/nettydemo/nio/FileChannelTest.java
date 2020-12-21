package com.fanshanhong.nettydemo.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Description: FileChannel相关接口方法demo
 * @Author: fan
 * @Date: 2020-07-24 15:19
 * @Modify:
 */
public class FileChannelTest {

    public static void main(String[] args) throws Exception {

        // Channel 有这些方法
        // read
        // write
        // transferFrom
        // transferTo

        writeTest();

        readTest();

        copyFile();

        transferFromTest();

    }

    /**
     * FileChannel write 方法
     *
     * @throws Exception
     */
    static void writeTest() throws Exception {

        String str = "hello, 中国\n";
        FileOutputStream fileOutputStream = new FileOutputStream("file01.txt", false);

        // 通过输出流 fileOutputStream 的 getChannel() 方法获取对应的 fileChannel
        // fileChannel 是 fileOutputStream 对象中的一个属性, 可以理解为在原生的输出流中内置了一个管道, 用于NIO的读写.
        // 返回的 channel 真实类型为 FileChannelImpl
        FileChannel channel = fileOutputStream.getChannel();


        // NIO中, 与Channel交互必须通过Buffer   ByteBuffer  ->  FileChannel  ->  文件


        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes("UTF-8"));
        // 这里不要flip了, 因为wrap之后, position就是0了.
        // byteBuffer.flip();
        // 也可以使用 byteBuffer.put()方法来把字符串放入ByteBuffer中

        // Writes a sequence of bytes to this channel from the given buffer.
        // 从给定的这个buffer中, 将byte序列写入到Channel
        channel.write(byteBuffer);

        // Reads a sequence of bytes from this channel into the given buffer.
        // 从Channel中读取一个byte序列, 放到buffer中.
        // channel.read();

        // 如果不用wrap, 也可以 用put
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024);
        byteBuffer1.put(str.getBytes("UTF-8"));
        // 一定要记得flip()啊
        byteBuffer1.flip();
        channel.write(byteBuffer1); // channel 直接write, 就是追加写入了.

        // 关闭资源
        channel.close();
        fileOutputStream.close();

        // 验证是否写入成功
    }

    /**
     * FileChannel read 方法
     *
     * @throws Exception
     */
    static void readTest() throws Exception {

        FileInputStream fileInputStream = new FileInputStream("file01.txt");


        // 通过输入流, 获取其中内置的Channel. 这里拿到的channel , 实质是FileChannelImpl对象
        FileChannel channel = fileInputStream.getChannel();


        // 创建缓冲区, 缓冲区大小就跟文件大小一样
        ByteBuffer byteBuffer = ByteBuffer.allocate(fileInputStream.available());

        // Reads a sequence of bytes from this channel into the given buffer.
        // 把数据从管道中读出来, 放在缓冲区里面
        channel.read(byteBuffer);

        // 后面直接使用了 byteBuffer.array() 方法, 所以这里不需要flip了. 如果是想要使用相对方法get(), 就需要flip一下
        // byteBuffer.flip();

        System.out.println(new String(byteBuffer.array()));

    }


    /**
     * 使用 ByteBuffer + FileChannel 完成文件的拷贝
     * <p>
     * 将 file01.txt 的内容拷贝到 file02.txt
     *
     * @throws Exception
     */
    static void copyFile() throws Exception {


        FileInputStream fileInputStream = new FileInputStream("file01.txt");
        FileChannel inChannel = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("file02.txt");
        FileChannel outChannel = fileOutputStream.getChannel();


        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(512);


        while (inChannel.read(byteBuffer) != -1) { // read 完, 数据全都装在byteBuffer里. position:76 limit:512 capacity:512
            // outChannel.write是要从byteBuffer里读数据了.因此 要flip一下.
            byteBuffer.flip();// flip之后, position=0, limit=76, 然后开始从byteBuffer里面读.

            // outChannel.write: 从byteBuffer里读, 把读到的数据写到outChannel里.
            outChannel.write(byteBuffer); // 这句执行完之后, position=76 limit=76
            // 这里要记得clear, 把position=0, limit=512.  否则 position=limit=76, 下次进入while 循环开始read, read结果=0, 会陷入死循环, 一直读一直写
            byteBuffer.clear();
        }

        // 关闭相关的资源
        inChannel.close();
        outChannel.close();
        fileInputStream.close();
        fileOutputStream.close();
    }

    /**
     * 使用 transferFrom 方法,从源 channel 中拷贝数据到 目标 channel
     *
     * @throws Exception
     */
    static void transferFromTest() throws Exception {

        // 源图片
        FileInputStream fileInputStream = new FileInputStream("Mojave.jpg");
        FileChannel sourceChannel = fileInputStream.getChannel();

        // 目标图片
        FileOutputStream fileOutputStream = new FileOutputStream("Mojave2.jpg");
        FileChannel dstChannel = fileOutputStream.getChannel();

        /*
         * Transfers bytes into this channel's file from the given readable byte
         * channel.
         * 从给定的可读字节通道中将字节传输到此通道的文件中。
         *
         * An attempt is made to read up to <tt>count</tt> bytes from the
         * source channel and write them to this channel's file starting at the
         * given <tt>position</tt>.
         * 尝试从源 channel中最多读取 count 个字节, 是从源channel 中指定的position位置开始读, 并把他们写入到目标 channel 对应的文件中.(这个position是源channel的position, 不是transferFrom方法中的参数position)
         *
         * An invocation of this method may or may not
         * transfer all of the requested bytes; whether or not it does so depends
         * upon the natures and states of the channels.  Fewer than the requested
         * number of bytes will be transferred if the source channel has fewer than
         * <tt>count</tt> bytes remaining, or if the source channel is non-blocking
         * and has fewer than <tt>count</tt> bytes immediately available in its
         * input buffer.
         * 这个方法的调用,可能会,也可能不会传输所有的字节
         * 是否能够传输所有的字节,取决于channel的性质和状态
         * 如果源通道剩余的字节数少于 count 个字节，则将传输少于请求的字节数
         * 又或者,源通道是非阻塞的,并且它立刻可读的字节数少于 count 个字节, 也会出现无法传输全部字节的情况
         *
         * This method does not modify this channel's position.  If the given
         * position is greater than the file's current size then no bytes are
         * transferred.  If the source channel has a position then bytes are read
         * starting at that position and then the position is incremented by the
         * number of bytes read.
         * 此方法不会修改此通道的位置。如果给定的position大于文件的当前大小，则不会传输任何字节。
         * 如果源通道有position(说的是 position < 源文件size)，则从该位置开始读取字节，并且在读的过程中, position 会自增(就是相对读的那个方法)
         *
         * This method is potentially much more efficient than a simple loop
         * that reads from the source channel and writes to this channel.  Many
         * operating systems can transfer bytes directly from the source channel
         * into the filesystem cache without actually copying them.
         * 此方法可能比从源通道读取并写入此通道的简单循环效率更高。许多操作系统可以直接将字节从源通道转移到文件系统缓存中，而无需实际复制它们。(说的是零拷贝)
         *
         * @param  src
         *         The source channel 源channel
         *
         * @param  position 指的是从源文件的哪个位置开始读, 不能为负数
         *         The position within the file at which the transfer is to begin;
         *         must be non-negative
         *
         * @param  count 传输的最大字节数
         *         The maximum number of bytes to be transferred; must be
         *         non-negative
         *
         * @return  The number of bytes, possibly zero,
         *          that were actually transferred
         *          返回值: 实际传输的字节数，可能为零
         */
        long count = dstChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        System.out.println("--" + count);

        // 释放资源
        sourceChannel.close();
        dstChannel.close();
        fileInputStream.close();
        fileOutputStream.close();


        //-----------------------

        // 图片无法验证position, 用文本文件验证一下position
        FileInputStream fileInputStream2 = new FileInputStream("file01.txt");
        FileChannel sourceChannel2 = fileInputStream2.getChannel();

        FileOutputStream fileOutputStream2 = new FileOutputStream("file03.txt");
        FileChannel dstChannel2 = fileOutputStream2.getChannel();
        long size = dstChannel2.size();//0
        // dstChannel2.write(ByteBuffer.wrap("2".getBytes()));
        // sourceChannel2.position(5);

        long transferCount = dstChannel2.transferFrom(sourceChannel2, 0, sourceChannel2.size());
        System.out.println("transfer完成:" + transferCount);

        // 这个position 到底是干嘛的??
        // position=0, 就正常全部拷贝
        // position=2,5, 拷贝的数量是0??
        // 加上这句, 先给目标文件里写点东西, 就能正常 transferFrom 了
        // dstChannel2.write(ByteBuffer.wrap("2".getBytes()));


        // 理解了: 想要给目标文件指定position,首先目标文件里面要有内容. 也就是目标文件的size > position. 比如我上面想要指定positon=1, 那dst2文件中至少要有1个字节的数据哇
        // 如果想要给 源文件制定position, 是用下面的这个方法
        // sourceChannel2.position(5);

        // 释放资源
        sourceChannel2.close();
        dstChannel2.close();
        fileInputStream2.close();
        fileOutputStream2.close();
    }
}
