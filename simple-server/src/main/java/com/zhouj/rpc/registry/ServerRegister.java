package com.zhouj.rpc.registry;

import com.zhouj.rpc.config.RpcConfig;
import com.zhouj.rpc.constant.Constant;
import com.zhouj.rpc.util.IpUtils;
import com.zhouj.rpc.zookeeper.ZookeeperClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Map;

/**
 * 服务信息注册到zookeeper
 *
 * @author zhouj
 * @since 2020-08-04
 */
public class ServerRegister {

    Logger log = LoggerFactory.getLogger(ServerRegister.class);


    private ZookeeperClient zookeeperClient;


    private ServiceRegistry serviceRegistry;

    private RpcConfig rpcConfig;

    public ServerRegister(RpcConfig rpcConfig, ServiceRegistry serviceRegistry, ZookeeperClient zookeeperClient) {
        this.rpcConfig = rpcConfig;
        this.serviceRegistry = serviceRegistry;
        this.zookeeperClient = zookeeperClient;
    }

    public void registry() {
        addRootNode();
        registerService();
    }

    /**
     * 添加root节点
     *
     * @param
     */
    public void addRootNode() {
        if (!zookeeperClient.exist(Constant.REGISTRY)) {
            zookeeperClient.createPath(Constant.REGISTRY);
        }
    }


    /**
     * 注册服务接口信息到zookeeper
     */
    public void registerService() {
        Map<String, ServiceInfo> serviceInfoMap = serviceRegistry.getServices();
        if (CollectionUtils.isEmpty(serviceInfoMap)) {
            return;
        }
        serviceInfoMap.entrySet().forEach(stringServiceInfoEntry -> {
            String node = node(stringServiceInfoEntry.getKey());
            if (!zookeeperClient.exist(node)) {
                log.info("创建接口节点:{}", node);
                zookeeperClient.createPath(node);
            }
            String address = IpUtils.findFirstNonLoopbackAddress().getHostAddress() + ":" + rpcConfig.getPort();
            String path = path(stringServiceInfoEntry.getKey());
            zookeeperClient.createData(path(stringServiceInfoEntry.getKey()), address);
            log.info("注册服务接口数据:{}", path + ":" + address);

        });

    }

    private String node(String serviceName) {
        return Constant.REGISTRY + "/" + serviceName;
    }

    private String path(String serviceName) {
        return Constant.REGISTRY + "/" + serviceName + Constant.NODE;
    }
}