package com.fanshanhong.nettydemo.nio.groupchat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.security.DigestException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.ZipException;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-26 17:48
 * @Modify:
 */
public class GroupChatClient {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9999;

    //如果客户端只是单纯的发消息的, 就不需要selector, 直接connect 然后调用channel.write就好了
    // 但是这里, 我们客户端还要接收服务端转发的消息, 因此需要个selecotr, 然后在上面监听READ方法, 就是有人发消息过来啦, 需要我们读啦
    Selector selector;
    SocketChannel socketChannel;


    GroupChatClient() throws Exception {
        selector = Selector.open();

        /**
         *    Opens a socket channel and connects it to a remote address.
         *    打开一个socket channel 并且连接到远端地址
         *
         *    This convenience method works as if by invoking the {@link #open()}
         *    method, invoking the {@link #connect(SocketAddress) connect} method upon
         *    the resulting socket channel, passing it <tt>remote</tt>, and then
         *    returning that channel.
         *    这是一个便捷的方法, 相当于 先调用open, 返回socketChannel, 然后再调用socketChannel的connect方法, 并把远端地址传入connect方法
         */

        socketChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
        // 带参数的open方法就相当于执行了下面这两句
        //socketChannel = SocketChannel.open();
        //socketChannel.connect(new InetSocketAddress(HOST, PORT));

        System.out.println("我是" + socketChannel.getLocalAddress() + ", 我上线啦, 服务器地址:" + socketChannel.socket().getRemoteSocketAddress());

        socketChannel.configureBlocking(false);

        // 将socketChannel 注册
        socketChannel.register(selector, SelectionKey.OP_READ);
    }


    public void sendMsg(String msg) throws Exception {
        System.out.println("准备发消息, 服务器地址:" + socketChannel.socket().getRemoteSocketAddress());
        socketChannel.write(ByteBuffer.wrap(msg.getBytes()));
    }


    public void readMsg() {
        try {
            // 当客户端关闭的时候, 还有一次读事件过来的.
            // 当你手动停止任意一方时，另一方都会不断收到READ事件。
            // 因此客户端亚扼要处理-1的情况
            while (selector.select() > 0) {


                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();

                    if (key.isReadable()) {


                        try {
                            SocketChannel socketChannel = (SocketChannel) key.channel();

                            System.out.println("有消息来了, 服务器地址:" + socketChannel.socket().getRemoteSocketAddress());

                            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                            int readResult;
                            readResult = socketChannel.read(byteBuffer);
                            if (readResult == -1) {  //需要单独检测read返回-1的情况
                                // 如果socketChannel已经关闭, 调用getRemote方法, 会报错:ClosedChannelException
                                System.out.println("readResult is -1, " + "检测到服务端" + socketChannel.socket().getRemoteSocketAddress() + "连接断开");
                                socketChannel.close();
                                key.cancel();
                            } else {
                                // outChannel.write是要从byteBuffer里读数据了.因此 要flip一下.
                                byteBuffer.flip();
                                // 拿到buffer中的有效数据
                                byte[] tbyte = new byte[byteBuffer.limit()];
                                byteBuffer.get(tbyte);
                                System.out.println(new String(tbyte));
                            }
                        } catch (IOException e) {
                            // socketChannel.read(buf)抛出异常时，则是连接断开了。需要关闭channel，且取消SelectionKey
                            try {
                                // 如果socketChannel已经关闭, 调用getRemote方法, 会报错:ClosedChannelException
                                System.out.println("检测到服务端" + socketChannel.socket().getRemoteSocketAddress() + "连接断开");
                                socketChannel.close();
                                key.cancel();
                                e.printStackTrace();
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }

                    }
                    iterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {


        final GroupChatClient client = new GroupChatClient();


        new Thread(
                new Runnable() {
                    public void run() {
                        client.readMsg();
                    }
                }
        ).start();

        new Thread(
                new Runnable() {
                    public void run() {
                        Scanner scanner = new Scanner(System.in);
                        while (scanner.hasNextLine()) {
                            String msg = scanner.nextLine();
                            try {
                                client.sendMsg(msg);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    }
                }
        ).start();


    }


}
