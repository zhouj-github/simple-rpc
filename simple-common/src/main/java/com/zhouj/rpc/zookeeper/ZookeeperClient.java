package com.zhouj.rpc.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhouj
 * @since 2023-03-15
 */
public class ZookeeperClient {

    private String address;

    private ZooKeeper zooKeeper;

    private static ZookeeperClient zookeeperClient;

    public ZookeeperClient(String address) {
        this.address = address;
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
            zooKeeper = new ZooKeeper(address, 5000, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Boolean exist(String path) {
        try {
            Stat stat = zooKeeper.exists(path, true);
            if (stat != null) {
                return true;
            }
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<String> getChildren(String path, Watcher watcher) {
        try {
            return zooKeeper.getChildren(path, watcher);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public byte[] getData(String path) {
        try {
            return zooKeeper.getData(path, true, null);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

}
