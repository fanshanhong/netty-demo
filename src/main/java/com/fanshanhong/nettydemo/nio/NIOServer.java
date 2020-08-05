package com.fanshanhong.nettydemo.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * @Description:
 * @Author: fan
 * @Date: 2020-07-23 14:42
 * @Modify:
 */
public class NIOServer {
    public static void main(String[] args) throws Exception {

        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();// serverSocketChannel实质是:ServerSocketChannelImpl@521

        Selector selector = Selector.open();

        // 绑定端口, 并监听
        // 低版本不支持这样写.
        // serverSocketChannel.bind(new INetSocketAddress(9983));
        serverSocketChannel.socket().bind(new InetSocketAddress(9983));

        // 设置非阻塞
        serverSocketChannel.configureBlocking(false);

        // 将serverSocketChannel注册到Selector上, 关注的事件是: 有客户端来连接
        // 所有注册的, 都会放到selector 里的 keys 那个集合进行管理.
        // selectedKeys 是发生了事件的集合. 因此是keys的子集.
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        // 可以在register之后观察一下selector.keys() 的变化.此时集合里面有一个元素(SelectionKey), SelectionKey的 channel 是 ServerSocketChannelImpl@521, 监听9983端口,显然就是跟上面ServerSocketChannel对应的SelectionKey

        // 监听, 等待客户端连接
        while (selector.select() > 0) {
            // 如果select()方法返回, 就说明注册在这个Selector上的Channel 有事件发生了
            // 我们可以通过selectedKeys()  拿到有哪些Channel上发生事件了.\
            // 比如说, 我们有一个ServerSocketChannel S1, 和两个 客户端连接(SocketChannel) C1 , C2
            // ServerSocketChannel 关注的事件是 有人来连接(ACCEPT), SocketChannel 关注的事件是READ, 就是客户端发消息来了
            // 当select方法返回, 表示肯定有事件发生了, 然后我们就看看到底发生了什么事件
            // 假如, 发生了一个连接事件和一个READ事件, 那么 select返回2, 表示两个通道上有事件发生了. (注意:如果在通道上发生了不关心的事件,也不会通知的. 比如C1我们关注的是READ, 那么如果在C1上发生了不是READ的事件, 是不会通知我们的)
            // 然后selectedKeys 集合里面其实就有两个.
            // 遍历selectedKeys, 根据SelectionKey的事件就知道发生了什么, 并且可以拿到对应的channel进行处理
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            // 当有1个客户端来连接,这个 selector.selectedKey 数量就是1 ,里面原色SelectionKey的channel 是 ServerSocketChannelImpl@521
            // index=0, interestOps=16.   显然是将keys() 中的那个元素, 也加入到这个Selectionkeys里面了, 表示这个channel上有感兴趣的事件发生了
            // 当客户端发消息的时候, selectedKeys 里面是1个, SeletionKeyImpl@645, keys里面2个, SeletionKeyImpl@575(channel 是ServerSocketChannelImpl@521), SeletionKeyImpl@645(SocketChannelImpl@634)
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                // 有客户端来连接
                if (key.isAcceptable()) { // 表示客户端来连接的事件已经就绪,此时我们调用accept不会阻塞
                    // 这个if条件好像也能这么写.
                    // key.interestOps() == SelectionKey.OP_ACCEPT

                    SocketChannel socketChannel = ((ServerSocketChannel) key.channel()).accept();//socketChannel是SocketCHannelImpl@634
                    socketChannel.configureBlocking(false);

                    // 将这个与客户端的通道也注册到selector上, 让它帮我们检测, 有数据的时候, 也通知我们一下
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    // 给这个channel添加一个attachment(关联对象), 比如我们在这里给它关联了一个buffer, 后续它就能获取到这个buffer开始用了
                    // 个人认为没有必要??
                    // socketChannel.register(selector, SelectionKey.OP_READ, buffer);

                    // 可以在register之后观察一下selector.keys() 的变化
                    // register之后, keys元素是2个. keys[1] 是 SocketCHannelImpl@634, remote是127.0.0.1:52772, 显然代表客户端Channel

                } else if (key.isReadable()) { // 客户端发消息过来了, 我们的可以开始读啦.

                    SocketChannel channel = (SocketChannel) key.channel();

                    ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
                    channel.read(byteBuffer);

                    System.out.println("客户端说:" + new String(byteBuffer.array()));

                    byteBuffer.flip();

                    byte[] bytes = new byte[byteBuffer.limit() - byteBuffer.position()];
                    System.out.println("remaining:" + byteBuffer.remaining());

                    for (int i = byteBuffer.position(); i < byteBuffer.limit(); i++) {
                        byte b = byteBuffer.get();
                        bytes[i] = b;
                    }

                    System.out.println("整理过后, 客户端说:" + new String(bytes));


                }

                iterator.remove();
            }
        }
    }
}
