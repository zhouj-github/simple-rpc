package com.zhouj.rpc.client;

import com.zhouj.rpc.constant.Constant;
import com.zhouj.rpc.protocol.Request;
import com.zhouj.rpc.protocol.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author zhouj
 * @since 2020-08-04
 */
public class ResponseFuture implements Future<Response> {

    Logger log = LoggerFactory.getLogger(ResponseFuture.class);

    private Request request;

    private volatile Response response;

    private Thread thread;

    private volatile boolean done = false;

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public Thread getThread() {
        return thread;
    }

    public ResponseFuture(Request request) {
        this.request = request;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return this.done;
    }

    @Override
    public Response get() {
        if (!isDone()) {
            LockSupport.park();
        }
        return response;
    }

    @Override
    public Response get(long timeout, TimeUnit unit) {
        if (!isDone()) {
            LockSupport.parkNanos(unit.toNanos(timeout));
        }
        if (!isDone()) {
            log.info("请求超时返回============");
            response = new Response();
            response.setCode(Constant.TIME_OUT);
            return response;
        }
        return response;
    }


}
