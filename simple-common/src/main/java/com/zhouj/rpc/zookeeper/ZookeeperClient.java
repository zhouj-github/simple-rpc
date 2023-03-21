package com.zhouj.rpc.zookeeper;

import com.zhouj.rpc.config.RpcConfig;
import com.zhouj.rpc.constant.Constant;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhouj
 * @since 2023-03-15
 */
public class ZookeeperClient {

    Logger logger = LoggerFactory.getLogger(ZookeeperClient.class);

    private String address;

    private ZooKeeper zooKeeper;

    private RpcConfig rpcConfig;

    private static ZookeeperClient zookeeperClient;

    public ZookeeperClient(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
        this.address = rpcConfig.getZookeeperAddress();
        init();
        zookeeperClient = this;
    }

    public static ZookeeperClient getInstance() {
        return zookeeperClient;
    }


    private void init() {
        ZooKeeper zooKeeper = connect();
        this.zooKeeper = zooKeeper;
    }

    /**
     * zookeeper连接
     *
     * @return
     */
    private ZooKeeper connect() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper = new ZooKeeper(address, rpcConfig.getZookeeperSessionTimeOut() == 0 ? Constant.SESSION_TIMEOUT : rpcConfig.getZookeeperSessionTimeOut(), watchedEvent -> {
                if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
        } catch (IOException e) {
            logger.error("io异常:{}", e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error("线程中断异常:{}", e.getMessage(), e);
        }
        return zooKeeper;
    }

    /**
     * 创建顺序临时节点
     */
    public String createData(String path, String data) {
        try {
            return zooKeeper.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        } catch (KeeperException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 创建持久目录节点
     *
     * @param path
     * @return
     */
    public String createPath(String path) {
        try {
            return zooKeeper.create(path, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        } catch (KeeperException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 判断节点是否存在
     *
     * @param path
     * @return
     */
    public Boolean exist(String path) {
        try {
            Stat stat = zooKeeper.exists(path, true);
            if (stat != null) {
                return true;
            }
        } catch (KeeperException e) {
            logger.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 获取子节点数据
     *
     * @param path
     * @param watcher
     * @return
     */
    public List<String> getChildren(String path, Watcher watcher) {
        try {
            return zooKeeper.getChildren(path, watcher);
        } catch (KeeperException | InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    /**
     * 获取数据
     *
     * @param path
     * @return
     */
    public byte[] getData(String path) {
        try {
            return zooKeeper.getData(path, true, null);
        } catch (KeeperException | InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
