package com.fanshanhong.nettydemo.zerocopy;

import java.io.DataInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-27 21:39
 * @Modify: 对比 零拷贝 与 传统IO方式效率的差别
 * <p>
 * 对比方式:
 * 采用两种方式实现一个客户端发送一个文件到服务端, 服务端直接丢弃.
 * <p>
 * 实现方式:
 * 客户端发送文件, 需要先从磁盘读取文件, 然后再将文件发送到socket.这样就涉及到多次拷贝的情况
 */
public class OldServer {
    public static void main(String[] args) throws Exception {
        // 建立 ServerSocket
        ServerSocket serverSocket = new ServerSocket(8899);

        // 阻塞等待客户端来连接并接受连接
        Socket socket = serverSocket.accept();

        InputStream inputStream = socket.getInputStream();
        DataInputStream dataInputStream = new DataInputStream(inputStream);


        byte[] bytes = new byte[4096];
        int len = -1;

        while ((len = dataInputStream.read(bytes)) != -1) {
            //丢弃, 啥也不做
        }
    }
}
