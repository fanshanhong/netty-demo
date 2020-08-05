package com.fanshanhong.nettydemo.nio.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class TestDisconnectServer {
    static ServerSocketChannel serverSocketChannel = null;
    static Selector selector = null;

    public static void main(String[] args) throws IOException {
        serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(8888));

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        int result = 0;
        int i = 1;
        while ((result = selector.select()) > 0) {
            System.out.println(String.format("selector %dth loop, ready event number is %d", i++, result));
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();

                if (sk.isAcceptable()) {
                    ServerSocketChannel ss = (ServerSocketChannel) sk.channel();
                    SocketChannel socketChannel = ss.accept();
                    socketChannel.configureBlocking(false);  //也切换非阻塞
                    socketChannel.register(selector, SelectionKey.OP_READ);  //注册read事件
                    System.out.println("接受到新的客户端连接");
                } else if (sk.isReadable()) {
                    System.out.println("有数据可读");
                }

                iterator.remove();
            }
        }
    }
}