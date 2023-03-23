package com.zhouj.rpc.boot.consumer;

import com.zhouj.rpc.client.registry.Consumer;
import com.zhouj.rpc.client.registry.ConsumerRegistry;
import com.zhouj.rpc.proxy.ClientFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 *
 * 扫描到的@RpcClient 注册到spring容器
 * @author zhouj
 * @since 2023-03-14
 */
public class ClientProcessor implements BeanDefinitionRegistryPostProcessor {

    public ClientProcessor(ConsumerRegistry consumerRegistry) {
        this.consumerRegistry = consumerRegistry;
    }

    private ConsumerRegistry consumerRegistry;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        List<Consumer> consumers = consumerRegistry.getConsumerCache();
        if (CollectionUtils.isEmpty(consumers)) {
            return;
        }
        for (Consumer consumer : consumers) {
            consumerRegistry.registry(consumer);
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(ClientFactory.class);
            beanDefinitionBuilder.addPropertyValue("type", consumer.getConsumerClass());
            beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            beanDefinitionBuilder.setPrimary(true);
            beanDefinitionRegistry.registerBeanDefinition(consumer.getServiceName(), beanDefinitionBuilder.getBeanDefinition());
        }
    }
}
