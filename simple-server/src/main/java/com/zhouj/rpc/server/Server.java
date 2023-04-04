package com.zhouj.rpc.server;

import com.zhouj.rpc.config.RpcConfig;
import com.zhouj.rpc.constant.Constant;
import com.zhouj.rpc.protocol.*;
import com.zhouj.rpc.registry.ServerRegister;
import com.zhouj.rpc.registry.ServiceRegistry;
import com.zhouj.rpc.util.IpUtils;
import com.zhouj.rpc.zookeeper.ZookeeperClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouj
 * @since 2020-08-04
 */
public class Server {

    private Logger log = LoggerFactory.getLogger(Server.class);

    private ServerRegister serverRegister;

    private String serverAddress;

    private ServiceRegistry serviceRegistry;

    private ChannelFuture channelFuture;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private RpcConfig rpcConfig;

    private ZookeeperClient zookeeperClient;


    public Server(ServerRegister serverRegister, ServiceRegistry serviceRegistry, RpcConfig rpcConfig) {
        this.serverRegister = serverRegister;
        this.serviceRegistry = serviceRegistry;
        this.rpcConfig = rpcConfig;
        this.serverAddress = IpUtils.findFirstNonLoopbackAddress().getHostAddress() + ":" + rpcConfig.getPort();

    }


    /**
     * 启动rpc服务
     */
    public void start() {

        ServerBootstrap serverBootStrap = new ServerBootstrap();
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup(1);
        serverBootStrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) {
                ByteBuf delimiter = Unpooled.buffer();
                delimiter.writeBytes(Constant.SPLIT.getBytes());
                socketChannel.pipeline().addLast(new HeadLengthEncoder());
                socketChannel.pipeline().addLast(new HeadLengthDecoder(Constant.MAX_FRAME_LENGTH, 0, 4, Request.class));
                socketChannel.pipeline().addLast(new ServerHandler(serviceRegistry));
            }
        });
        String[] array = serverAddress.split(":");
        channelFuture = serverBootStrap.bind(array[0], Integer.parseInt(array[1]));
        serverRegister.registry();
        channelFuture.channel().closeFuture().syncUninterruptibly();
    }

    public ChannelFuture channelFuture() {
        return this.channelFuture;
    }

    /**
     * 关闭rpc服务
     */
    public void stop() {
        try {
            if (channelFuture.channel() != null) {
                channelFuture.channel().close();
            }
        } catch (Throwable e) {
            log.error(e.getMessage(), e);
        }
        try {
            bossGroup.shutdownGracefully().syncUninterruptibly();
            workerGroup.shutdownGracefully().syncUninterruptibly();
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

}
