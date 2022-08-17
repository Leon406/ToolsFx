package me.leon.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class ServerSocketChannels implements Runnable {

    private ServerSocketChannel serverSocketChannel;

    protected Selector selector;

    private volatile boolean stop;

    public ServerSocketChannels(int port) {

        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress(port), 1024);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("the time is start port = " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void stop() {
        this.stop = true;
    }

    @Override
    public void run() {
        while (!stop) {
            try {
                // selector.select()会一直阻塞到有一个通道在你注册的事件上就绪了
                // selector.select(1000)会阻塞到1s后然后接着执行，相当于1s轮询检查
                selector.select(1000);
                // 找到所有准备接续的key
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> it = selectionKeys.iterator();
                SelectionKey key = null;
                while (it.hasNext()) {
                    key = it.next();
                    it.remove();
                    try {
                        handle(key);
                    } catch (Exception e) {
                        if (key != null) {
                            key.cancel();
                            if (key.channel() != null) {
                                key.channel().close();
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        if (selector != null) {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void handle(SelectionKey key) throws IOException {
        // 如果key是有效的
        if (key.isValid()) {
            if (key.isAcceptable()) {
                doAccept(key);
            } else if (key.isReadable()) {
                doReadable(key);
            }
        }
    }

    /**
     * 处理客户端连接事件
     *
     * @param key
     * @throws IOException
     */
    public void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
        SocketChannel sc = ssc.accept();
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);
    }

    public void doReadable(SelectionKey key) throws IOException {
        // 获得通道对象
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        // 从channel读数据到缓冲区
        int readBytes = sc.read(readBuffer);
        if (readBytes > 0) {
            readBuffer.flip();
            // returns the number of elements between the current position and the  limit.
            // 要读取的字节长度
            byte[] bytes = new byte[readBuffer.remaining()];
            // 将缓冲区的数据读到bytes数组
            readBuffer.get(bytes);
            String body = new String(bytes, StandardCharsets.UTF_8);
            System.out.println(
                    Thread.currentThread().getName() + ": the time server receive order: " + body);
            String currenttime =
                    "query time order".equals(body)
                            ? new Date(System.currentTimeMillis()).toString()
                            : "bad order";
            doWrite(sc, currenttime);
        } else {
            key.channel();
            sc.close();
        }
    }

    public void doWrite(SocketChannel channel, String response) throws IOException {
        if (response != null && !"".equals(response)) {
            byte[] bytes = response.getBytes();
            ByteBuffer write = ByteBuffer.allocate(bytes.length);
            write.put(bytes);
            write.flip();
            // 将缓冲数据写入渠道，返回给客户端
            channel.write(write);
        }
    }

    public static void main(String[] args) {
        int port = 8010;
        ServerSocketChannels server = new ServerSocketChannels(port);
        server.run();
    }
}
