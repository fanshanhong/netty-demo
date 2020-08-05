package com.fanshanhong.nettydemo.nio.test2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class TestDisconnectClient {
    static SocketChannel socketChannel = null;
    static Selector selector = null;

    public static void main(String[] args) throws IOException {
        socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1",8888));
        socketChannel.configureBlocking(false);

        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_READ);
        int result = 0; int i = 1;
        while((result = selector.select()) > 0) {
            System.out.println(String.format("selector %dth loop, ready event number is %d", i++, result));
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey sk = iterator.next();

                if (sk.isReadable()) {
                    System.out.println("有数据可读");
                    SocketChannel canReadChannel = (SocketChannel)sk.channel();
                    ByteBuffer buf = ByteBuffer.allocate(1024);
                    try {
                        while (canReadChannel.read(buf) > 0) {
                            buf.flip();
                            System.out.println(new String(buf.array()));
                            buf.clear();
                        }
                    } catch (IOException e) {
                        canReadChannel.close();
                        sk.cancel();
                        System.out.println("检测到远程连接断开");
                        e.printStackTrace();
                    }
                }

                iterator.remove();
            }
        }
    }
}
