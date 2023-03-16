package com.zhouj.rpc.config;

/**
 * @author zhouj
 * @since 2020-08-03
 */
public class RpcConfig {

    private String zookeeperAddress;

    private String port;

    private String clientPackage;

    private String serverPackage;

    public String getZookeeperAddress() {
        return zookeeperAddress;
    }

    public void setZookeeperAddress(String zookeeperAddress) {
        this.zookeeperAddress = zookeeperAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
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
}
