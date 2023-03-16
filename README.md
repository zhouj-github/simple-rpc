## simple-rpc

基于netty+zookeeper+jprotobuf实现的简单springboot rpc框架<br>
Features:
* Reactor模型
* 服务自动注册与发现
* 服务节点优雅下线
* 客户端负载均衡
* 支持protostuff协议
* 请求超时重试
* 异步获取响应结果

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
```java

@RpcClient
public class RemoteServiceImpl implements RemoteService {
     public String remote(){
         return "远程服务";
     }
    
}
```