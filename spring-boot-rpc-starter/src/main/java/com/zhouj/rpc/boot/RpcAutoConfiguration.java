package com.zhouj.rpc.boot;

import com.zhouj.rpc.boot.consumer.ClientProcessor;
import com.zhouj.rpc.boot.consumer.ConsumerScan;
import com.zhouj.rpc.boot.server.RpcServerLifeCycle;
import com.zhouj.rpc.boot.server.ServerBeanDefinitionRegistry;
import com.zhouj.rpc.client.discover.ServiceDiscover;
import com.zhouj.rpc.client.registry.ConsumerRegistry;
import com.zhouj.rpc.client.registry.DefaultConsumerRegistry;
import com.zhouj.rpc.config.RpcConfig;
import com.zhouj.rpc.constant.Constant;
import com.zhouj.rpc.registry.DefaultServiceRegistry;
import com.zhouj.rpc.registry.ServerRegister;
import com.zhouj.rpc.registry.ServiceRegistry;
import com.zhouj.rpc.server.Server;
import com.zhouj.rpc.zookeeper.ZookeeperClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author zhouj
 * @since 2023-03-08
 */
@Configuration(proxyBeanMethods = false)
@ComponentScan(Constant.BASE_PATH)
public class RpcAutoConfiguration implements EnvironmentAware {

    private Environment environment;


    @Bean
    public RpcConfig rpcConfig() {
        RpcConfig rpcConfig = new RpcConfig();
        rpcConfig.setZookeeperAddress(environment.getProperty("rpc.zookeeper.address"));
        rpcConfig.setPort(environment.getProperty("rpc.port"));
        rpcConfig.setClientPackage(environment.getProperty("rpc.clientPackage"));
        rpcConfig.setServerPackage(environment.getProperty("rpc.serverPackage"));
        return rpcConfig;
    }

    @Bean
    public ZookeeperClient zookeeperClient(RpcConfig rpcConfig) {
        return new ZookeeperClient(rpcConfig);
    }

    @Bean
    @ConditionalOnMissingBean
    public ConsumerRegistry consumerRegistry(RpcConfig rpcConfig) {
        ConsumerRegistry consumerRegistry = new DefaultConsumerRegistry();
        ConsumerScan consumerScan = new ConsumerScan(rpcConfig);
        consumerRegistry.registry(consumerScan.scanClient());
        return consumerRegistry;
    }

    @Bean
    @ConditionalOnMissingBean
    public ClientProcessor clientProcessor(ConsumerRegistry consumerRegistry) {
        ClientProcessor clientProcessor = new ClientProcessor(consumerRegistry);
        return clientProcessor;
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceDiscover serviceDiscover(ConsumerRegistry consumerRegistry, ZookeeperClient zookeeperClient) {
        ServiceDiscover serviceDiscover = new ServiceDiscover(consumerRegistry, zookeeperClient);
        return serviceDiscover;
    }

    @Bean
    public ServiceRegistry serviceRegistry() {
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        return serviceRegistry;
    }

    @Bean
    public ServerBeanDefinitionRegistry serverBeanDefinitionRegistry(ServiceRegistry serviceRegistry) {
        return new ServerBeanDefinitionRegistry(serviceRegistry);
    }

    @Bean
    public ServerRegister serverRegister(RpcConfig rpcConfig, ServiceRegistry serviceRegistry, ZookeeperClient zookeeperClient) {
        ServerRegister serverRegister = new ServerRegister(rpcConfig, serviceRegistry, zookeeperClient);
        return serverRegister;
    }

    @Bean
    @ConditionalOnMissingBean
    public Server rpcServer(RpcConfig rpcConfig, ServiceRegistry serviceRegistry, ServerRegister serverRegister) {
        rpcConfig.setPort(rpcConfig.getPort());
        Server rpcServer = new Server(serverRegister, serviceRegistry, rpcConfig);
        return rpcServer;
    }

    @Bean
    @ConditionalOnMissingBean
    public RpcServerLifeCycle rpcServerLifeCycle(Server rpcServer) {
        RpcServerLifeCycle rpcServerLifeCycle = new RpcServerLifeCycle(rpcServer);
        return rpcServerLifeCycle;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

}
