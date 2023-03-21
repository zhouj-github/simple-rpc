package com.zhouj.rpc.client;

import com.zhouj.rpc.client.discover.ServiceDiscover;
import com.zhouj.rpc.protocol.Response;
import com.zhouj.rpc.protocol.RpcDecode;
import com.zhouj.rpc.protocol.RpcEncode;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 消费者连接管理
 *
 * @author zhouj
 * @since 2020-08-04
 */
public class ConnectManager {

    Logger log = LoggerFactory.getLogger(ConnectManager.class);

    private static volatile ConnectManager connectManager;

    public static AtomicInteger atomicInteger = new AtomicInteger(0);

    private List<ClientHandler> handlers = new CopyOnWriteArrayList<>();
    private Map<String, List<ClientHandler>> handlerMap = new ConcurrentHashMap<>();

    private Bootstrap bootstrap;

    private ServiceDiscover serviceDiscover;

    public void setServiceDiscover(ServiceDiscover serviceDiscover) {
        this.serviceDiscover = serviceDiscover;
    }

    private ConnectManager() {
        initBootstrap();
    }

    public static ConnectManager getInstance() {
        if (connectManager == null) {
            synchronized (ConnectManager.class) {
                if (connectManager == null) {
                    connectManager = new ConnectManager();
                }
            }
        }
        return connectManager;
    }


    /**
     * 初始化bootstrap
     */
    private void initBootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup(1)).channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline channelPipeline = socketChannel.pipeline();
                        channelPipeline.addLast(new RpcEncode());
                        channelPipeline.addLast(new RpcDecode(Response.class));
                        channelPipeline.addLast(new ClientHandler());
                    }
                });
        this.bootstrap = bootstrap;
    }

    /**
     * 轮询获取服务端
     *
     * @param serviceName
     * @return
     * @throws Exception
     */
    public ClientHandler getRoundRobinHandle(String serviceName) {
        List<ClientHandler> handlers = handlerMap.get(serviceName);
        if (CollectionUtils.isEmpty(handlers)) {
            reWatch(serviceName);
            if (CollectionUtils.isEmpty(handlers)) {
                throw new RuntimeException("没有服务");
            }
        }
        int size = handlers.size();
        return handlers.get(atomicInteger.getAndAdd(1) % size);
    }

    /**
     * 移除clientHandler
     *
     * @param clientHandler
     */
    public void removeChannel(ClientHandler clientHandler) {
        this.handlers.remove(clientHandler);
        this.handlerMap.entrySet().forEach(stringListEntry -> stringListEntry.getValue().remove(clientHandler));

    }

    /**
     * 重新监听节点变化
     *
     * @param serviceName
     */
    public void reWatch(String serviceName) {
        serviceDiscover.watchNode(serviceDiscover.path(serviceName));

    }

    public void updateServices(List<String> nodeList, String serviceName) {
        if (!CollectionUtils.isEmpty(nodeList)) {
            nodeList.stream().forEach(node -> {
                if (!checkNode(serviceName, node)) {
                    return;
                }
                CountDownLatch countDownLatch = new CountDownLatch(1);
                String[] strings = node.split(":");
                InetSocketAddress inetSocketAddress = new InetSocketAddress(strings[0], Integer.parseInt(strings[1]));
                bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) channelFuture -> {
                    //连接建立后加入客户端连接列表
                    if (channelFuture.isSuccess()) {
                        ClientHandler clientHandler = channelFuture.channel().pipeline().get(ClientHandler.class);
                        log.info("建立连接:{}", channelFuture.channel().localAddress() + "------>" + channelFuture.channel().remoteAddress());
                        if (CollectionUtils.isEmpty(handlerMap.get(serviceName))) {
                            List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();
                            clientHandlers.add(clientHandler);
                            handlerMap.put(serviceName, clientHandlers);
                        } else {
                            handlerMap.get(serviceName).add(clientHandler);
                        }
                        handlers.add(clientHandler);
                        countDownLatch.countDown();
                    }
                });
                try {
                    countDownLatch.await();
                } catch (InterruptedException e) {
                    log.error(e.getMessage(), e);
                }
            });

        }
    }

    /**
     * 检查服务ip否已建立连接
     *
     * @param serviceName
     * @param node
     * @return
     */
    private boolean checkNode(String serviceName, String node) {
        for (ClientHandler handler : handlers) {
            if (handler.channel().remoteAddress().toString().contains(node)) {
                if (CollectionUtils.isEmpty(handlerMap.get(serviceName))) {
                    List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();
                    clientHandlers.add(handler);
                    handlerMap.put(serviceName, clientHandlers);
                } else {
                    if (checkHandler(serviceName, node)) {
                        handlerMap.get(serviceName).add(handler);
                    }
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 检查接口对应服务是否已建立连接
     *
     * @param serviceName
     * @param node
     * @return
     */
    public boolean checkHandler(String serviceName, String node) {
        List<ClientHandler> clientHandlers = handlerMap.get(serviceName);
        for (ClientHandler handler : clientHandlers) {
            if (handler.channel().remoteAddress().toString().contains(node)) {
                return false;
            }
        }
        return true;
    }

}
