package com.zhouj.rpc.client;

import com.zhouj.rpc.protocol.Request;
import com.zhouj.rpc.protocol.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消费者处理handler
 *
 * @author zhouj
 * @since 2020-08-04
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {

    Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private Channel channel;

    private Map<String, RpcFuture> map = new ConcurrentHashMap<>();

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
        log.info("channel注册======>>:{}", ctx.channel().localAddress());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelInactive();
        //连接断开 移除clientHandler
        ConnectManager.getInstance().removeChannel(this);
        log.info("连接断开======>>>");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Response response) throws Exception {
        //响应结果写回rpcFuture
        String requestId = response.getRequestId();
        RpcFuture rpcFuture = map.get(requestId);
        log.info("耗时:{}ms", System.currentTimeMillis() - response.getTimestamp());
        if (rpcFuture != null) {
            map.remove(requestId);
            rpcFuture.done(response);
        }
    }

    /***
     * 向服务端发送消息
     * @param request
     * @return
     */
    public RpcFuture sendRequest(Request request) {
        RpcFuture rpcFuture = new RpcFuture(request);
        map.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request);
        return rpcFuture;
    }

    public Channel channel() {
        return this.channel;
    }
}
