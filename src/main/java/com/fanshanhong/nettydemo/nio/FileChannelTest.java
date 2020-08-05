package com.fanshanhong.nettydemo.nio;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-24 15:19
 * @Modify:
 */
public class FileChannelTest {

    public static void main(String[] args) throws Exception {

        // Channel 有这些方法
        // read
        // write
        // transferFrom
        // transferTo

        writeTest();
        readTest();

        copyFile();

        transferFromTest();

    }

    static void transferFromTest() throws Exception {

        FileInputStream fileInputStream = new FileInputStream("Mojave.jpg");
        FileChannel sourceChannel = fileInputStream.getChannel();


        FileOutputStream fileOutputStream = new FileOutputStream("Mojave2.jpg");
        FileChannel dstChannel = fileOutputStream.getChannel();

        dstChannel.transferFrom(sourceChannel, 0, sourceChannel.size());

        // 释放资源
        sourceChannel.close();
        dstChannel.close();
        fileInputStream.close();
        fileOutputStream.close();

    }

    static void copyFile() throws Exception {


        FileInputStream fileInputStream = new FileInputStream("file01.txt");
        FileChannel inChannel = fileInputStream.getChannel();

        FileOutputStream fileOutputStream = new FileOutputStream("file02.txt");
        FileChannel outChannel = fileOutputStream.getChannel();


        ByteBuffer byteBuffer = ByteBuffer.allocate(512);
        ByteBuffer byteBuffer1 = ByteBuffer.allocateDirect(512);


        while (inChannel.read(byteBuffer) != -1) { // read 完, 数据全都装在byteBuffer里. position:76 limit:512 capacity:512
            // outChannel.write是要从byteBuffer里读数据了.因此 要flip一下.
            byteBuffer.flip();// flip之后, position=0, limit=76, 然后开始从byteBuffer里面读.

            // outChannel.write: 从byteBuffer里读, 把读到的数据写到outChannel里.
            outChannel.write(byteBuffer); // 这句执行完之后, position=76 limit=76
            // 这里要记得clear, 把position=0, limit=512.  否则 position=limit=76, 下次再往byteBuffer里写, 是写不了了.
            byteBuffer.clear();
        }

    }

    static void readTest() throws Exception {


        FileInputStream fileInputStream = new FileInputStream("file01.txt");


        // 通过输入流, 获取其中内置的Channel. 这里拿到的channel , 实质是FileChannelImpl对象
        FileChannel channel = fileInputStream.getChannel();


        // 创建缓冲区, 缓冲区大小就跟文件大小一样
        ByteBuffer byteBuffer = ByteBuffer.allocate(fileInputStream.available());
        // 把数据从管道中读出来, 放在缓冲区里面
        channel.read(byteBuffer);

        // 后面直接使用了 byteBuffer.array() 方法, 所以这里不需要flip了. 如果是想要使用相对方法get(), 就需要flip一下
        // byteBuffer.flip();

        System.out.println(new String(byteBuffer.array()));


    }

    static void writeTest() throws Exception {

        String str = "hello, 中国\n";
        FileOutputStream fileOutputStream = new FileOutputStream("file01.txt", false);

        // 通过输出流获取对应的 fileChannel
        // fileChannel 是 fileOutputStream 对象中的一个属性, 可以理解为在原生的输出流中内置了一个管道, 用于NIO的读写.
        // channel 真实类型为 FileChannelImpl
        FileChannel channel = fileOutputStream.getChannel();


        // NIO中, 与Channel交互必须通过Buffer   ByteBuffer  ->  FileChannel  ->  文件


        ByteBuffer byteBuffer = ByteBuffer.wrap(str.getBytes("UTF-8"));
        // 这里不要flip了, 因此wrap之后, position就是0了
        // byteBuffer.flip();

        // Writes a sequence of bytes to this channel from the given buffer.
        // 从给定的这个buffer中, 将byte序列写入到Channel
        channel.write(byteBuffer);

        // Reads a sequence of bytes from this channel into the given buffer.
        // 从Channel中读取一个byte序列, 放到buffer中.
        // channel.read();

        // 如果不用wrap, 也可以 用put
        ByteBuffer byteBuffer1 = ByteBuffer.allocate(1024);
        byteBuffer1.put(str.getBytes("UTF-8"));
        // 一定要记得flip()啊
        byteBuffer1.flip();
        channel.write(byteBuffer1); // channel 直接write, 就是追加了.

        channel.close();
        fileOutputStream.close();
    }
}
