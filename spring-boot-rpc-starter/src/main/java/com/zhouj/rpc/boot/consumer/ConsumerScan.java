package com.zhouj.rpc.boot.consumer;

import com.zhouj.rpc.boot.annotation.RpcClient;
import com.zhouj.rpc.client.registry.Consumer;
import com.zhouj.rpc.client.registry.DefaultConsumer;
import com.zhouj.rpc.config.RpcConfig;
import com.zhouj.rpc.constant.Constant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author zhouj
 * @since 2023-03-14
 */
public class ConsumerScan {

    private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

    private RpcConfig rpcConfig;

    public ConsumerScan(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
    }


    /**
     * 扫描客户端@RpcClient
     * @return
     */
    public List<Consumer> scanClient() {
        Map<String, Consumer> consumerMap = new HashMap<>();
        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                ClassUtils.convertClassNameToResourcePath(StringUtils.isBlank(rpcConfig.getClientPackage()) ? Constant.BASE_PATH : rpcConfig.getClientPackage()) + "/**/*.class";
        try {
            Resource[] resources = resourcePatternResolver.getResources(packageSearchPath);
            if (resources == null || resources.length == 0) {
                return new ArrayList<>();
            }
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class aClass = ClassLoader.getSystemClassLoader().loadClass(className);
                Field[] fields = aClass.getDeclaredFields();
                if (fields == null || fields.length == 0) {
                    continue;
                }
                for (Field field : fields) {
                    if (field.isAnnotationPresent(RpcClient.class)) {
                        Class<?> fieldClass = field.getType();
                        String serviceName = fieldClass.getCanonicalName();
                        if (!consumerMap.containsKey(serviceName)) {
                            consumerMap.put(serviceName, new DefaultConsumer(fieldClass));
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("I/O failure during classpath scanning", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return consumerMap.values().stream().collect(Collectors.toList());

    }
}
