package com.zhouj.rpc.registry;

/**
 * @author zhouj
 * @since 2023-03-10
 */
public class DefaultServiceInfo implements ServiceInfo {

    private Class aClass;

    private String className;

    @Override
    public ServiceInfo createService(Class<?> aClass) {
        this.aClass = aClass;
        this.className = aClass.getInterfaces()[0].getCanonicalName();
        return this;
    }

    @Override
    public String getInterfaceName() {
        return className;
    }

    @Override
    public Class getServiceClass() {
        return aClass;
    }

}
