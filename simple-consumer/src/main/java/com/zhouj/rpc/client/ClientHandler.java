package com.zhouj.rpc.client;

import com.zhouj.rpc.call.Callable;
import com.zhouj.rpc.protocol.Request;
import com.zhouj.rpc.protocol.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.LockSupport;

/**
 * 消费者处理handler
 *
 * @author zhouj
 * @since 2020-08-04
 */
public class ClientHandler extends SimpleChannelInboundHandler<Response> {

    Logger log = LoggerFactory.getLogger(ClientHandler.class);

    private Channel channel;

    /**
     *
     */
    private Map<String, ResponseFuture> requestCache = new ConcurrentHashMap<>();

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
        log.info("===================>接收到rpc响应");
        //响应结果写回rpcFuture
        String requestId = response.getRequestId();
        ResponseFuture rpcFuture = requestCache.get(requestId);
        if (rpcFuture.getCallable() != null) {
            rpcFuture.getCallable().call(response);
        } else {
            rpcFuture.setDone(true);
            rpcFuture.setResponse(response);
            LockSupport.unpark(rpcFuture.getThread());
        }
        log.info("耗时:{}ms", System.currentTimeMillis() - response.getTimestamp());
    }

    /***
     * 向服务端发送消息
     * @param request
     * @return
     */
    public ResponseFuture sendRequest(Request request) {
        ResponseFuture rpcFuture = new ResponseFuture(request);
        rpcFuture.setThread(Thread.currentThread());
        requestCache.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request);
        return rpcFuture;
    }

    /***
     * 向服务端发送消息
     * @param request
     * @return
     */
    public ResponseFuture sendCall(Request request, Callable callable) {
        ResponseFuture rpcFuture = new ResponseFuture(request);
        rpcFuture.setCallable(callable);
        rpcFuture.setThread(Thread.currentThread());
        requestCache.put(request.getRequestId(), rpcFuture);
        channel.writeAndFlush(request);
        return rpcFuture;
    }

    public Channel channel() {
        return this.channel;
    }
}
