package org.ogcs.monitor;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author TinyZ
 * @date 2016-08-25.
 */
public class ZookeeperMonitor {

    public static void main(String[] args) throws IOException, InterruptedException {


//        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, null);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline cp = socketChannel.pipeline();
                cp.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
                cp.addLast("decoder", new StringDecoder());
                cp.addLast("encoder", new StringEncoder());
                cp.addLast("handler", new SimpleChannelInboundHandler<String>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
                        System.out.println(s);
                    }

                    @Override
                    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                        System.out.println("断开连接了");
                        super.channelInactive(ctx);
                    }
                });
            }
        });

        String[] command = new String[]{
                "conf\n",
                "cons\n",
                "crst\n",
                "dump\n",
                "envi\n",
                "srst\n",
                "srvr\n",
                "stat\n",
                "wchs\n",

                "wchc\n",
                "dirs\n",
                "wchp\n",
                "mntr\n",
                "isro\n",
                "gtmk\n",
                "stmk\n",
        };

        InetSocketAddress address = new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 2181);
        Channel channel = null;
        for (String cmd : command) {
            if (channel == null || !channel.isActive()) {
                ChannelFuture sync = bootstrap.connect(address).sync();
                channel = sync.channel();
            }
            channel.writeAndFlush(cmd);
            Thread.sleep(1000);
        }

        while (true) {

        }
    }
}
