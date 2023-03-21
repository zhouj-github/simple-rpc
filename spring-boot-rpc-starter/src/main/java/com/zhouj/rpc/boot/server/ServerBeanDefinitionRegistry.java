package com.zhouj.rpc.boot.server;

import com.zhouj.rpc.boot.annotation.RpcService;
import com.zhouj.rpc.registry.DefaultServiceInfo;
import com.zhouj.rpc.registry.ServiceInfo;
import com.zhouj.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 服务bean 注册
 *
 * @author zhouj
 * @since 2020-08-04
 */
public class ServerBeanDefinitionRegistry implements BeanDefinitionRegistryPostProcessor, EnvironmentAware {

    private Logger logger = LoggerFactory.getLogger(ServerBeanDefinitionRegistry.class);

    private ServiceRegistry serviceRegistry;

    private Environment environment;

    public ServerBeanDefinitionRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    return true;
                }
                return false;
            }
        };
        TypeFilter typeFilter = new AnnotationTypeFilter(RpcService.class);
        classPathScanningCandidateComponentProvider.addIncludeFilter(typeFilter);
        String packages = environment.getProperty("rpc.serverPackage");
        Set<BeanDefinition> beanDefinitionSet = classPathScanningCandidateComponentProvider.findCandidateComponents(packages == null || packages == "" ? "*" : packages);
        if (!CollectionUtils.isEmpty(beanDefinitionSet)) {
            Map<String, ServiceInfo> serviceInfoMap = new HashMap<>();

            beanDefinitionSet.forEach(beanDefinition -> {
                if (!beanDefinitionRegistry.containsBeanDefinition(beanDefinition.getBeanClassName())) {
                    beanDefinitionRegistry.registerBeanDefinition(beanDefinition.getBeanClassName(), beanDefinition);
                }
                String serviceName = beanDefinition.getBeanClassName();
                if (!serviceInfoMap.containsKey(serviceName)) {
                    ServiceInfo serviceInfo = new DefaultServiceInfo();
                    try {
                        serviceInfo.createService(Class.forName(beanDefinition.getBeanClassName()));
                    } catch (ClassNotFoundException e) {
                        logger.error(e.getMessage(), e);
                    }
                    serviceInfoMap.put(serviceInfo.getInterfaceName(), serviceInfo);
                }
            });
            serviceRegistry.registryService(serviceInfoMap);

        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
