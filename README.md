## simple-rpc

基于netty+zookeeper+jprotobuf实现的简单springboot rpc框架<br>
Features:
* 服务自动注册与发现
* 服务节点优雅下线
* 客户端负载均衡
* protostuff协议
* 请求超时重试

## 使用示例

```xml
        <dependency>
            <groupId>com.zhouj</groupId>
            <artifactId>spring-boot-rpc-starter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

```
定义接口
````java
public interface RemoteService{
    
    String remote();
}
````

客户端示例
```yaml
rpc:
  port: 8200 #服务端口配置
  clientPackage: com.zhouj.rpc.consumer #客户端包扫描路径
  zookeeper:
    address: 127.0.0.1:2181 #zookeeper地址
```

```java
@RestController
public class ConsumerController {


    @RpcClient
    private RemoteService remoteService;
    

    @RequestMapping("/remote")
    public String test() {
        return remoteService.remote();
    }
}
```
服务端示例
```yaml
rpc:
  port: 8300 #服务端端口
  serverPackage: com.zhouj.rpc.server #服务包路径扫描
  zookeeper:
    address: 127.0.0.1:2181  #zookeeper地址
```
```java

@RpcClient
public class RemoteServiceImpl implements RemoteService {
     public String remote(){
         return "远程服务";
     }
    
}
```