package com.fanshanhong.nettydemo.bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description: 演示BIO模型. 用阻塞IO来实现服务端,针对每个客户端连接都新开一个线程与之进行通信
 * @Author: fan
 * @Date: 2020-07-23 14:03
 * @Modify:
 */
public class BIOServer {

    public static void main(String[] args) throws Exception {

        // 线程池
        ExecutorService threadPool = Executors.newCachedThreadPool();

        ServerSocket serverSocket = new ServerSocket(6666);

        System.out.println("服务器已经启动, 所在线程为:" + Thread.currentThread().getName());

        while (true) {
            System.out.println("监听中, 等待连接...");
            // 阻塞监听等待客户端连接
            final Socket socket = serverSocket.accept();

            System.out.println("有客户端连接进来");

            // 创建一个新的线程, 在这个新的线程中, 与刚刚连接进来的客户端进行通信
//            new Thread(new Runnable() {
//                public void run() {
//                    handle(socket);
//                }
//            }).start();


            // 使用线程池
            threadPool.execute(new Runnable() {
                public void run() {
                    handle(socket);
                }
            });
        }
    }


    static void handle(Socket socket) {
        try {
            byte[] bytes = new byte[1024];

            // 拿到Socket的InputStream, 用于从Socket中读取数据
            InputStream inputStream = socket.getInputStream();

            int len = -1;
            while ((len = inputStream.read(bytes)) != -1) { // read方法为阻塞方法
                // 直接输出
                System.out.println("当前处理读写的线程: id:" + Thread.currentThread().getId() + "   name:" + Thread.currentThread().getName());
                System.out.println("客户端说:" + new String(bytes, 0, len, Charset.forName("UTF-8")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println("出现异常, 连接关闭");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
