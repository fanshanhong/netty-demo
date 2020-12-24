package com.fanshanhong.nettydemo.zerocopy;

import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-27 22:09
 * @Modify:
 */
public class NewServer {
    public static void main(String[] args) throws Exception {

        // 简单起见, 不用selector了

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        // 获取到与通道关联的socket对象
        ServerSocket serverSocket = serverSocketChannel.socket();

        // 如果一个socket 已经被关闭, 刚刚关闭后, 这个端口号还不能被其他使用, 因为刚刚关闭后的一小段时间内, 处于超时状态, 称为TIME_WAIT
        // 在TIME_WAIT状态下, 如果有新的socket 想要绑定到这个端口号, 是不行的, 会提示地址被占用了
        //  我们在 bind 方法之前 调用将setReuseAddress=true, 意思让  这个端口号在timeWAIT的状态下能够被重用
        // 这样新的socket 就可以绑定到处与timewait状态的端口号啦
        serverSocket.setReuseAddress(true);
        serverSocket.bind(new InetSocketAddress(8898));

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 20); //20K

//        while (true) {

        /**
         * Accepts a connection made to this channel's socket.
         * 接受与当前的 serverSocketChannel  的socket 建立的连接
         *
         * <p> If this channel is in non-blocking mode then this method will
         * immediately return <tt>null</tt> if there are no pending connections.
         * Otherwise it will block indefinitely until a new connection is available
         * or an I/O error occurs.
         * 如果serverSocketChannel 处于非阻塞状态, 这个方法会立刻返回null(如果没有挂起的连接的情况下)
         * 否则(也就是阻塞模式), 这个方法会一直阻塞, 直到有新的连接可用或者发生了IO错误
         *
         * <p> The socket channel returned by this method, if any, will be in
         * blocking mode regardless of the blocking mode of this channel.
         * 这个方法返回的socket channel 一定是处于阻塞模式的, 不管 这个ServerSocketChannel 是啥模式
         *
         * <p> This method performs exactly the same security checks as the {@link
         * java.net.ServerSocket#accept accept} method of the {@link
         * java.net.ServerSocket} class.  That is, if a security manager has been
         * installed then for each new connection this method verifies that the
         * address and port number of the connection's remote endpoint are
         * permitted by the security manager's {@link
         * java.lang.SecurityManager#checkAccept checkAccept} method.  </p> */
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(true);

        int len = -1;
        FileChannel fileChannel = new FileOutputStream("aa.rar").getChannel();
        while (true) {
            // read 返回值参考https://blog.csdn.net/cao478208248/article/details/41648359
            len = socketChannel.read(byteBuffer);
            if (len == -1) {
                break;
            }

            byteBuffer.flip();
            // 这里没有丢弃
            fileChannel.write(byteBuffer);
            byteBuffer.clear();
        }

        fileChannel.close();
        socketChannel.close();
    }

    /**
     * 当socketChannel为阻塞方式时（默认就是阻塞方式）read函数，不会返回0;
     * 简单说下原因: 阻塞方式的socketChannel，若没有数据可读，或者缓冲区满了，就会阻塞，直到满足读的条件，条件满足后, 读到的字节数量就是 正值(count>0)
     * 如果读完了, 就返回 -1;
     * 所以一般阻塞方式的read是比较简单的
     *
     * 下面总结一下非阻塞场景下的read碰到的问题。注意：这里的场景都是基于客户端以阻塞socket的方式发送数据。
     *
     * 1、read什么时候返回-1
     *
     * read返回-1说明客户端的数据发送完毕，并且主动的close socket。所以在这种场景下，（服务器程序）你需要关闭socketChannel并且取消key，最好是退出当前函数。注意，这个时候服务端要是继续使用该socketChannel进行读操作的话，就会抛出“远程主机强迫关闭一个现有的连接”的IO异常。
     *
     * 2、read什么时候返回0
     *
     * 其实read返回0有3种情况，
     * ① 某一时刻socketChannel中当前（注意是当前）没有数据可以读，这时会返回0，
     * ② 其次是bytebuffer的position等于limit了，即bytebuffer的remaining等于0，这个时候也会返回0，(也就是buffer 满了)
     * ③ 最后一种情况就是客户端的数据发送完毕了（注意看后面的程序里有这样子的代码），这个时候客户端想获取服务端的反馈调用了recv函数，若服务端继续read，这个时候就会返回0。
     *
     * 对异常的一些处理, 参考:https://blog.csdn.net/anlian523/article/details/105009863/
     *
     */
}
