package com.zhouj.rpc.boot.server;

import com.zhouj.rpc.server.Server;
import org.springframework.context.SmartLifecycle;

/**
 * 服务端启动容器
 *
 * @author zhouj
 * @since 2023-03-10
 */
public class RpcServerLifeCycle implements SmartLifecycle {

    private volatile boolean running;

    private Server rpcServer;

    public RpcServerLifeCycle(Server rpcServer) {
        this.rpcServer = rpcServer;
    }

    @Override
    public void start() {
        this.rpcServer.start();
        this.running = true;
    }

    @Override
    public void stop() {
        this.running = false;
        this.rpcServer.stop();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

}
