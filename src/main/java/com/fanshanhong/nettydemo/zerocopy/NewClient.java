package com.fanshanhong.nettydemo.zerocopy;

import java.io.FileInputStream;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-27 22:33
 * @Modify:
 */
public class NewClient {
    public static void main(String[] args) throws Exception {

        // 零拷贝客户端
        String filename = "/Volumes/Mechanical/F/code/相关信息/医惠企业信息/医务在线应用市场材料/图标/Icon-App-216x216@1x.png";

        // 建立连接
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8898 /*9999*/));
        socketChannel.configureBlocking(true);

        // 获取到与文件关联的文件Channel
        FileChannel fileChannel = new FileInputStream(filename).getChannel();

        long start = System.currentTimeMillis();


        // 从 src channel 中把数据拷贝到当前 file channel
        // fileChannel.transferFrom(src);

        // 从当前channel中的数据写到 dst channel
        // fileChannel.transferTo(dst)


        /**
         * Transfers bytes from this channel's file to the given writable byte
         * channel.
         * 将 该 channel 关联的那个文件的字节内容,  传送到给定的 channel 中
         *
         * <p> An attempt is made to read up to <tt>count</tt> bytes starting at
         * the given <tt>position</tt> in this channel's file and write them to the
         * target channel.
         * 从当前的这个 channel 中, 从给定的 position 位置开始, 最多读取 count 个字节, 并把他们写到目标 channel (第三个参数指定的那个channel)
         *
         *
         * An invocation of this method may or may not transfer
         * all of the requested bytes;
         * 对该方法的调用, 可能会, 也可能不会传递所有请求的字节.
         * whether or not it does so depends upon the
         * natures and states of the channels.  Fewer than the requested number of
         * bytes are transferred if this channel's file contains fewer than
         * <tt>count</tt> bytes starting at the given <tt>position</tt>, or if the
         * target channel is non-blocking and it has fewer than <tt>count</tt>
         * bytes free in its output buffer.
         * 是否会传递所有请求的字节, 取决于channel的特性和状态.
         * 如果当前这个channel包含的字节数比count少的字节数, 那就传递不了那么多.  比如一个文件有80字节, 你想要从20开始(position=20), 传递100个字节(count=100), 那就传递不了那么多, 最多能传递60.
         * 或者, 目标 channel 是非阻塞通道，并且其输出缓冲区中的可用字节少于 count 个字节.
         *
         *
         * <p> This method does not modify this channel's position.  If the given
         * position is greater than the file's current size then no bytes are
         * transferred.  If the target channel has a position then bytes are
         * written starting at that position and then the position is incremented
         * by the number of bytes written.
         * 这个方法不会修改 当前 channel的 position.
         * 如果给定的position 大于当前文件的大小, 则不会传输任何字节。
         * 比如:当前文件只有20字节, 你从50开始传递, 就不会传递任何
         *
         * <p> This method is potentially much more efficient than a simple loop
         * that reads from this channel and writes to the target channel.  Many
         * operating systems can transfer bytes directly from the filesystem cache
         * to the target channel without actually copying them.  </p>
         * 该方法比从该通道读取并写入目标通道的简单循环更有效。
         * 许多操作系统可以 直接地!! 将字节直接从文件系统缓存传输到目标通道，而无需实际复制它们。
         *
         *
         * transferTo 底层就是借助零拷贝来实现的, 在UNIX和Linux系统中，调用这个方法将会引起sendfile()系统调用。
         * 发送磁盘上的文件数据, 建议使用 transferTo, 效率高点
         * 使用场景一般是：
         *
         * 较大，读写较慢，追求速度
         * M内存不足，不能加载太大数据
         * 带宽不够，即存在其他程序或线程存在大量的IO操作，导致带宽本来就小
         * 以上都建立在不需要进行数据文件操作的情况下，如果既需要这样的速度，也需要进行数据操作怎么办？
         * 那么使用NIO的直接内存！
         *
         * NIO的直接内存(MappedByteBuffer, 核心即是map()方法, 该方法把文件映射到内存中，获得内存地址addr，然后通过这个addr构造MappedByteBuffer类，以暴露各种文件操作API。)
         * 由于MappedByteBuffer申请的是堆外内存，因此不受Minor GC控制，只能在发生Full GC时才能被回收。而==DirectByteBuffer==改善了这一情况，它是MappedByteBuffer类的子类，同时它实现了DirectBuffer接口，维护一个Cleaner对象来完成内存回收。因此它既可以通过Full GC来回收内存，也可以调用clean()方法来进行回收。
         *
         * 作者：攀山客
         * 链接：https://www.jianshu.com/p/497e7640b57c
         * 来源：简书
         *
         * */

        // 返回值: 实际传递的字节数.
        long transferCount = fileChannel.transferTo(0, fileChannel.size(), socketChannel);
        // 通过fileChannel的size方法可以拿到文件的长度.
        // 但是socketChannel 就没有类似size的方法

        System.out.println("传递字节数: " + transferCount + "  耗时:" + (System.currentTimeMillis() - start));

        fileChannel.close();
        socketChannel.close(); // 这里close, 服务端就可能read 到-1

    }
}
