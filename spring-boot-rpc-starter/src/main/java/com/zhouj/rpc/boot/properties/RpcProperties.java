package com.zhouj.rpc.boot.properties;

import com.zhouj.rpc.constant.Constant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouj
 * @since 2020-08-03
 */
@Configuration
@ConfigurationProperties(prefix = "rpc")
public class RpcProperties {
    private String port;

    private Zookeeper zookeeper;

    private String clientPackage = Constant.BASE_PATH;

    private String serverPackage = Constant.BASE_PATH;

    public Zookeeper getZookeeper() {
        return zookeeper;
    }

    public void setZookeeper(Zookeeper zookeeper) {
        this.zookeeper = zookeeper;
    }

    public String getClientPackage() {
        return clientPackage;
    }

    public void setClientPackage(String clientPackage) {
        this.clientPackage = clientPackage;
    }

    public String getServerPackage() {
        return serverPackage;
    }

    public void setServerPackage(String serverPackage) {
        this.serverPackage = serverPackage;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
