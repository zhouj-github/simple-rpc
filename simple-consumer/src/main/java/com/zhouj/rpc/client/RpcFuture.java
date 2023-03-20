package com.zhouj.rpc.client;

import com.zhouj.rpc.protocol.Request;
import com.zhouj.rpc.protocol.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author zhouj
 * @since 2020-08-04
 */
public class RpcFuture implements Future<Response> {

    Logger log = LoggerFactory.getLogger(RpcFuture.class);

    private Request request;

    private Response response;

    private FutureSync sync;

    public RpcFuture(Request request) {
        this.request = request;
        this.sync = new FutureSync();
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
        return sync.isDone();
    }

    @Override
    public Response get() throws InterruptedException, ExecutionException {
        sync.acquire(0);
        return response;
    }

    @Override
    public Response get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        if (!sync.tryAcquireNanos(-1, unit.toNanos(timeout))) {
            log.info("请求超时返回============");
            response = new Response();
            response.setCode(300);
            return response;
        }
        return response;
    }


    public void done(Response response) {
        this.response = response;
        sync.release(1);
    }

    public static class FutureSync extends AbstractQueuedSynchronizer {

        //future status
        private final int done = 1;

        private final int pending = 0;

        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == done;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == pending) {
                if (compareAndSetState(pending, done)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        }

        protected boolean isDone() {
            return getState() == done;
        }


    }
}
