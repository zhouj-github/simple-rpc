package com.zhouj.rpc.client.discover;

import com.zhouj.rpc.client.ConnectManager;
import com.zhouj.rpc.client.registry.ConsumerRegistry;
import com.zhouj.rpc.constant.Constant;
import com.zhouj.rpc.zookeeper.ZookeeperClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务节点发现
 *
 * @author zhouj
 * @since 2020-08-03
 */
public class ServiceDiscover {

    Logger log = LoggerFactory.getLogger(ServiceDiscover.class);


    private ZookeeperClient zookeeperClient;

    private ConsumerRegistry consumerRegistry;

    public ServiceDiscover(ConsumerRegistry consumerRegistry, ZookeeperClient zookeeperClient) {
        this.consumerRegistry = consumerRegistry;
        this.zookeeperClient = zookeeperClient;
        initNodes();
    }


    /**
     * 添加root节点
     *
     * @param
     */
    private void addRootNode() {
        if (!zookeeperClient.exist(Constant.REGISTRY)) {
            zookeeperClient.createPath(Constant.REGISTRY);
        }
    }


    /**
     * 初始化节点
     */
    private void initNodes() {
        addRootNode();
        if (CollectionUtils.isEmpty(consumerRegistry.getConsumers())) {
            return;
        }
        consumerRegistry.getConsumers().forEach(consumer -> {
            String path = path(consumer.getServiceName());
            if (!zookeeperClient.exist(path)) {
                zookeeperClient.createPath(path);
            }
            watchNode(path);
        });
        ConnectManager.getInstance().setServiceDiscover(this);
    }

    /**
     * 监听节点变化
     *
     * @param path
     */
    public void watchNode(String path) {
        log.info("获取{}节点数据并添加监控", path);
        List<String> nodes = zookeeperClient.getChildren(path, watchedEvent -> {
            log.info("zookeeper节点变更,触发更新handler", path);
            watchNode(watchedEvent.getPath());
        });
        if (CollectionUtils.isEmpty(nodes)) {
            log.info("获取{}节点数据为空", path);
            return;
        }
        List<String> nodeList = new ArrayList<>();
        for (String node : nodes) {
            byte[] data = zookeeperClient.getData(path + "/" + node);
            String address = new String(data);
            log.info("服务节点数据变更:{}", path + "/" + node + "_" + address);
            nodeList.add(address);
        }
        String[] strings = path.split("/");
        String serviceName = strings[strings.length - 1];
        ConnectManager.getInstance().updateServices(nodeList, serviceName);

    }

    public String path(String serviceName) {
        return Constant.REGISTRY + "/" + serviceName;
    }


}
