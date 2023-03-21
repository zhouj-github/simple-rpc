package com.zhouj.rpc.server;

import com.zhouj.rpc.constant.Constant;
import com.zhouj.rpc.invoker.DefaultMethodInvoker;
import com.zhouj.rpc.invoker.MethodInvoker;
import com.zhouj.rpc.protocol.Request;
import com.zhouj.rpc.protocol.Response;
import com.zhouj.rpc.registry.ServerRegister;
import com.zhouj.rpc.registry.ServiceInfo;
import com.zhouj.rpc.registry.ServiceRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;


/**
 * @author zhouj
 * @since 2020-08-04
 */
public class ServerHandler extends SimpleChannelInboundHandler<Request> {

    Logger log = LoggerFactory.getLogger(ServerHandler.class);

    private MethodInvoker methodInvoker;

    private ServiceRegistry serviceRegistry;

    public ServerHandler(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        methodInvoker = new DefaultMethodInvoker();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Request request) {
        String interfaceName = request.getInterfaceName();
        ServiceInfo serviceInfo = serviceRegistry.getService(interfaceName);
        Class c = serviceInfo.getServiceClass();
        Object result = methodInvoker.invoke(request, c);
        Response response = new Response();
        response.setCode(Constant.SUCCESS);
        response.setRequestId(request.getRequestId());
        response.setResult(result);
        response.setTimestamp(request.getTimestamp());
        channelHandlerContext.writeAndFlush(response);
    }

}
