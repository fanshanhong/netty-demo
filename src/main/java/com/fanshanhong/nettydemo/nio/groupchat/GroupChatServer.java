package com.fanshanhong.nettydemo.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-26 17:00
 * @Modify: NIO 对异常的一些处理, 参考:https://blog.csdn.net/anlian523/article/details/105009863/
 */
public class GroupChatServer {

    Selector selector;
    ServerSocketChannel serverSocketChannel;

    static final int PORT = 9999;

    // constructor
    GroupChatServer() {

        try {

            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();

            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));

            // 设置非阻塞模式
            serverSocketChannel.configureBlocking(false);

            // 将ServerSocketChannel 注册到 Selector 上, 关注的事件是有人来连接
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    /**
     * 监听有事件连接
     */
    private void listen() {
        try {

            while (selector.select() > 0) {
                // 在注册的通道上面有感兴趣的事件发生


                // 遍历selectedKeys集合
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {


                    SelectionKey key = iterator.next();
                    if (key.isAcceptable()) {


                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

                        SocketChannel socketChannel = serverSocketChannel.accept();

                        socketChannel.configureBlocking(false);

                        socketChannel.register(selector, SelectionKey.OP_READ);


                        // 这样就是上线了???
                        // Netty里是channelIsActive??
                        System.out.println(socketChannel.socket().getRemoteSocketAddress() + " 上线了..");


                    } else if (key.isReadable()) {

                        // 通道上有数据来啦, 可以读了
                        // 当客户端关闭的时候, 还有一次读事件过来的.
                        // 当你手动停止任意一方时，另一方都会不断收到READ事件。
                        handle(key);
                    }

                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handle(SelectionKey key) {

        SocketChannel socketChannel = (SocketChannel) key.channel();

        try {


            ByteBuffer byteBuffer = ByteBuffer.allocate(3);
            // 这里我一次从socketCHannel中最多读取3个字节
            // 如果, 客户端发了 1234567 7个字节的数据
            // select方法>0, 进来开始读
            // 那么, 第一次进入, 我只读走了 123   3个字节.socketChannel中还剩余4ge
            // 那么, select方法还会>0, 再次进入, 开始读, 又读走了 456 3个字节,
            // 然后 select方法还是会返回>0, 最后读走7, 才能完全读完.
            // 也就是说, 当socketChannel里还有数据没有读完的时候, select方法多次返回, 直到读完为止
            // 那, 如果我想一次性读完呢? 就是不管socketChanel中有多少数据, 我都一次性给读完, 这样能行么??
            // 好像可行的, while(socketChannel.read()>0) 就好了.
            // read的返回值: 参考:https://blog.csdn.net/cao478208248/article/details/41648359
            // 第一次写  read() > -1   一直死循环了.... 要写>0


            StringBuffer sb = new StringBuffer();
            int readResult;

            // read 返回值参考:https://blog.csdn.net/cao478208248/article/details/41648359
            // 对异常的一些处理, 参考:https://blog.csdn.net/anlian523/article/details/105009863/
            while ((readResult = socketChannel.read(byteBuffer)) > 0) {
                // 直接array 会把后面的0都打印出来.
//                String msg = new String(byteBuffer.array());
//                System.out.println(socketChannel.getRemoteAddress().toString()+"客户端说:" + msg);

                // outChannel.write是要从byteBuffer里读数据了.因此 要flip一下.
                byteBuffer.flip();
                // 拿到buffer中的有效数据
                byte[] tbyte = new byte[byteBuffer.limit()];
                byteBuffer.get(tbyte);
                String msg = new String(tbyte); // msg 客户端发来的内容

                sb.append(msg);

                byteBuffer.rewind();
            }
            if (readResult == -1) {  // read返回-1说明客户端的数据发送完毕，并且主动的close socket。因此需要单独检测read返回-1的情况,
                // 如果socketChannel已经关闭, 调用getRemote方法, 会报错:ClosedChannelException
                System.out.println("readResult is -1, " + "检测到客户端" + socketChannel.socket().getRemoteSocketAddress() + "连接断开");
                // 此时可以认为连接断了, 客户端下线了.

                // 然后关闭服务端的这个socketChannel, 并取消key
                socketChannel.close();
                key.cancel();
            } else {
                System.out.println(socketChannel.socket().getRemoteSocketAddress().toString() + "说: " + sb.toString());

                // 向其他客户端转发(向其他的SocketChannel write), 要排除掉自己
                sendMsgToOthers(socketChannel.socket().getRemoteSocketAddress().toString() + "说: "+sb.toString(), key);
            }


        } catch (Exception e) {
            // socketChannel.read(buf)抛出异常时，则是连接断开了。需要关闭channel，且取消SelectionKey
            try {
                // 如果socketChannel已经关闭, 调用getRemote方法, 会报错:ClosedChannelException
                System.out.println("检测到客户端" + socketChannel.socket().getRemoteSocketAddress() + "连接断开");
                socketChannel.close();
                key.cancel();
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void sendMsgToOthers(String msg, SelectionKey self) {
        // 遍历 所有注册到selector 的 SocketChannel, (其实就遍历selector的 keys集合就好了)

        try {
            for (SelectionKey key : selector.keys()) {

                // keys里面还要ServerSocketChannel,, 不用给ServerSocketChannel转发
                // 还要再排除掉自己
                if (key.channel() instanceof SocketChannel && key != self) {

                    SocketChannel targetChannel = (SocketChannel) key.channel();
                    targetChannel.write(ByteBuffer.wrap(msg.getBytes("UTF-8")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GroupChatServer groupChatServer = new GroupChatServer();
        groupChatServer.listen();
    }

}
