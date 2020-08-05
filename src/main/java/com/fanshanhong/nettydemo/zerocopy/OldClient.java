package com.fanshanhong.nettydemo.zerocopy;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-27 21:51
 * @Modify:
 */
public class OldClient {

    public static void main(String[] args) throws Exception {

        // 先与服务端建立连接(socket)


        // 然后不断读取磁盘上的文件数据, 读到之后, 写到socket的 输出流
        // 当循环结束, 就实现了把文件从磁盘读取并发送到网络的过程


        // new  Socket  如果带参数,
        // Creates a stream socket and connects it to the specified port number on the named host.
        // 这样就不需要调用connect了

        Socket socket = new Socket("127.0.0.1", 8899);

        // 这一句等价于下面两句话
        Socket socket1 = new Socket();
        socket1.connect(new InetSocketAddress("127.0.0.1", 8899));


        String filename = "/Users/shanhongfan/Downloads/XJ190331122-樊山红-徐诗玲.rar";


        FileInputStream fileInputStream = new FileInputStream(filename);
        OutputStream outputStream = socket.getOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

        byte[] bytes = new byte[4096];
        int len = -1;
        long total = 0;


        long start = System.currentTimeMillis();

        while ((len = fileInputStream.read(bytes)) != -1) {
            dataOutputStream.write(bytes, 0, len);
            total += len;
        }

        System.out.println("发送总字节数:" + total + "  耗时:" + (System.currentTimeMillis() - start));

        fileInputStream.close();
        dataOutputStream.close();
        socket.close();

    }
}
