package com.fanshanhong.nettydemo.nio;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @Description: NIO 实现的 Client
 * @Author: fan
 * @Date: 2020-07-25 23:09
 * @Modify:
 */
public class NIOClient {
    public static void main(String[] args) throws Exception {

        SocketChannel socketChannel = SocketChannel.open();


        socketChannel.configureBlocking(false);

        /*
         *  Connects this channel's socket.
         *
         *  If this channel is in non-blocking mode then an invocation of this
         *  method initiates a non-blocking connection operation.
         *
         *
         *      * @return  <tt>true</tt> if a connection was established,
         *          <tt>false</tt> if this channel is in non-blocking mode
         *          and the connection operation is in progress
         */

        boolean connect = socketChannel.connect(new InetSocketAddress("127.0.0.1", 9983));
        if (connect == false) {


            // <tt>true</tt> if, and only if, this channel's socket is now
            //     *          connected
            while (!socketChannel.finishConnect()) {
                System.out.println("正在非阻塞连接, 这里可以做其他事情");
            }
        }

        // 如果connect = true  或者跳出了上面的whitel循环, 代表连接已经建立了.
        // 开始发消息到服务器


        InputStream in = System.in;

        InputStreamReader inputStreamReader = new InputStreamReader(in);

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String s = bufferedReader.readLine();
        while (!s.equals("exit")) {
            ByteBuffer wrap = ByteBuffer.wrap(s.getBytes("UTF-8"));
            socketChannel.write(wrap);

            s = bufferedReader.readLine();
        }


        socketChannel.close();

    }
}
