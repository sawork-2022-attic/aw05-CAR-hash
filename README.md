# RESTful microPoS 

| 学号      | 姓名   |
| --------- | ------ |
| 191220162 | 张乐简 |

[TOC]

## 概述

​		参考spring-petclinic-microservices,实现了一个rest风格的微服务架构，采用断路器，并使用ehcache管理缓存。

## 内容

### 构建PosDataType

​	为方便多个模块共用数据，首先将一些共用的类在PosDataType模块中实现，接着将整个模块打包作为依赖导入到其他模块中去。该模块包含Item和Product两个类，具体实现细节与前次实验区别不大，不深入描述。

​	完成模块后，将其通过Idea提供的工具打包。

![image-20220420122128737](/ref/image-20220420122128737.png)

![image-20220420122225002](ref/image-20220420122225002.png)

​	打包完成后，将依赖添加到之后的模块中去。以pos-products为例，先将jar包放在resource下，接着在pom.xml中添加相关依赖。

![image-20220420122342734](ref/image-20220420122342734.png)

![image-20220420122408362](ref/image-20220420122408362.png)

​		这样便可以在其他模块里使用PosDataType中定义的公用类和数据结构。

### 搭建pos-config

​		首先搭建pos-config服务器，用于为其他所有服务提供配置文件。先建立Git仓库，将配置文件推送至远端。

![image-20220420122745234](ref/image-20220420122745234.png)

​		仓库链接为https://github.com/CAR-hash/aw05-config.git。

​		其中pos-cart-config、pos-product-config和pos-gateway-config为真正的服务，而pos-discovery-config是eureka server，是整个架构的基础设施服务。

​		eureka server的配置里主要包含端口号和eureka相关设置。

```properties
server.port=8761
eureka.client.registerWithEureka = false
eureka.client.fetchRegistry = false
```

​		以pos-cart-config为例，对实际使用的服务，为它们配置端口号、要链接的eureka server位置即可。

```properties
server.port=8081
eureka.client.serviceUrl.defaultZone  = http://localhost:8761/eureka
eureka.client.instance.preferIpAddress = true
```

​		然后开始搭建pos-config，即配置服务器。主要的工作分两步，首先指名这是个配置服务器：

```java
@EnableConfigServer
@SpringBootApplication
public class PosConfigServer {
    public static void main(String[] args) {
        SpringApplication.run(PosConfigServer.class, args);
    }
}
```

​		接着在配置文件里指名配置服务器自身运行的端口号、拉取配置的仓库、是否在启动时拉取配置、用户名和密码。

```properties
server.port=8888
spring.cloud.config.server.git.uri=https://github.com/CAR-hash/aw05-config.git
spring.cloud.config.server.git.clone-on-start=true
spring.security.user.name=root
spring.security.user.password=123
```

​		这样配置服务器便可以运行。

### 搭建pos-discovery

​		接着搭建eureka server。首先指明其为eureka server

```java
@SpringBootApplication
@EnableEurekaServer
public class PosEurekaServer {

    public static void main(String[] args) {
        SpringApplication.run(PosEurekaServer.class, args);
    }
}
```

​		再在配置文件里告知程序去哪里拉取配置。

```properties
spring.application.name=pos-discovery-config
spring.cloud.config.uri=http://localhost:8888
spring.config.import=optional:configserver:http://root:123@localhost:8888
```

​		这样eureka server便可以运行。

### 搭建pos-cart

​	搭建pos-cart模块，用于负责向购物车里添加和移除商品。由于采用Rest架构，尽量使用无状态服务器，所以无法提供打印购物车的API，仅仅提供向购物车添加物品和移除商品的API。为了维护购物车，需要在发送给服务器的HTTP请求中附上购物车的数据。

​	根据以上需求，构建openapi需要的配置文件。

```yml
paths:
  /cart/add:
    post:
      summary: Add item to cart
      operationId: add item
      requestBody:
        description: add item to cart
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ItemFields'
        required: true
      responses:
        '200':
          description: Add succeed
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Items"
        default:
          description: unexpected error
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"
  /cart/del:
    post:
      summary: Del the existence or decrease the quantity of certain item in the cart
      operationId: del item
      requestBody:
        description: del item in cart
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/ItemFields'
        required: true
      responses:
        '200':
          description: Del succeed
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/Items'
        default:
          description: unexpected error
          content:
            application/json:
              schema:
                $ref: "#/components/schemas/Error"

```

​		以上代码省略了开头和Componets。由于有HTTP Body，两个请求以POST的形式实现。生成CartApi接口并用Controller实现后，接着考虑service的问题。添加删除本身与aw04类似，主要问题在于要在添加删除前检测要添加/删除的商品是否存在，所以要用restTemplate去pos-products处问一下。

```java
    @Override
    public boolean checkProduct(String productId) {
        try {
            restTemplate.getForObject("http://pos-products-config/product/products/" + productId, Product.class);
        }
        catch (HttpClientErrorException e){
            return false;
        }
        return true;
    }
```

​		若没有相应的商品。请求会抛出HttpClientErrorException异常，否则便会正常执行。利用异常处理机制实现检测。

### 搭建pos-gateway

​	接着实现pos-gateway。在api.yml中为其提供product和cart所有服务的接口。

```yaml
openapi: "3.0.0"
info:
  version: 1.0.0
  title: Gateway
  license:
    name: MIT
paths:
  /product/products:
    get:
	...

  /product/products/{productId}:
    get:
	...

  /cart/add:
    post:
	...
  /cart/del:
    post:
	...

```

​		再在service中通过restTemplate访问相应的服务来实现接口。

#### 添加断路器

​		为pos-gateway添加断路器。添加相关配置。

```xaml
		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-circuitbreaker-resilience4j</artifactId>
            <version>1.0.2.RELEASE</version>
        </dependency>
```

```java
    @Autowired
    private CircuitBreakerFactory circuitBreakerFactory;
```

​		这样便可以自由创建CircuitBreaker对象。在每个服务前创建断路器并通过断路器来运行服务。

```java
    @Override
    public List<Product> getProducts() {
        CircuitBreaker circuitBreaker=circuitBreakerFactory.create("circuitBreaker");

        Object obj = circuitBreaker.run(()->restTemplate.getForObject("http://pos-products-config/product/products",Object.class));
        assert obj != null;

        ObjectMapper mapper=new ObjectMapper();

        List<Product> products;

        products=mapper.convertValue(obj, new TypeReference<List<Product>>() {
        });

        return products;
    }
```

### 添加ehcache

​	为products添加ehcache。首先创建CacheHelper。

```java
public class CacheHelper {

    private final CacheManager cacheManager;
    private final Cache<Integer, List> productCache;

    public CacheHelper(){
        cacheManager= CacheManagerBuilder.newCacheManagerBuilder().build();
        cacheManager.init();
        productCache=cacheManager.createCache("productCache", CacheConfigurationBuilder.newCacheConfigurationBuilder(Integer.class, List.class, ResourcePoolsBuilder.heap(100)));

    }

    public Cache<Integer,List> getProductCache(){
        return cacheManager.getCache("productCache",Integer.class,List.class);
    }
}
```

​	第一次访问京东时缓存，之后先确认缓存再看要不要访问。

```java
    @Override
    public List<Product> allProducts() {
        if(cacheHelper.getProductCache().containsKey(0)){
            return (List<Product>) cacheHelper.getProductCache().get(0);
        }

        try {
            if (products == null){
                products = parseJD("Java");
                cacheHelper.getProductCache().put(0,products);
            }
        } catch (IOException e) {
            products = new ArrayList<>();
        }
        return products;
    }
```

### 结果

​	利用Postman验证四个接口的实现情况。

![image-20220420124844941](ref/image-20220420124844941.png)

  GET products运行正常。

![image-20220420124924573](ref/image-20220420124924573.png)

​	访问单个product也运行正常。接着配置如下json文件作为HTTP Request的Request Body，测试POST add和POST del。

```json
{
        "cart":[
            {
                "productId": "12800420",
                "quantity": 15
            },
            {
                     "productId":"10058164",
                    "quantity":10   
            }
        ],
        "productId":"12800420",
        "quantity":3   
}
```

​	![image-20220420125104876](ref/image-20220420125104876.png)

​	修改一下json，测试一下添加新商品的功能。

```json
{
        "cart":[
            {
                "productId": "12800420",
                "quantity": 15
            },
            {
                     "productId":"10058164",
                    "quantity":10   
            }
        ],
        "productId":"13284888",
        "quantity":3   
}
```

![](ref/image-20220420125306942.png)	

​	POST add正常。再检测一下添加不存在商品时是否会出错。

```json
{
        "cart":[
            {
                "productId": "12800420",
                "quantity": 15
            },
            {
                     "productId":"10058164",
                    "quantity":10   
            }
        ],
        "productId":"13284888999",
        "quantity":3   
}
```

![image-20220420125436380](ref/image-20220420125436380.png)

​	出现错误，符合预期。

![image-20220420125128585](ref/image-20220420125128585.png)

​	POST del正常。测试结束。