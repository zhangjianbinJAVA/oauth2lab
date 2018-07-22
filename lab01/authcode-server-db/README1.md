基于授权码模式+Spring Security OAuth2的最简授权服务器
======
### oauth中的角色
- client：调用资源服务器API的应用
- Oauth 2.0 Provider：包括Authorization Server和Resource Server
    1. Authorization Server：认证服务器，进行认证和授权
    2. Resource Server：资源服务器，保护受保护的资源
- user：资源的拥有者


### 详细介绍一下 Oauth 2.0 Provider

##### Authorization Server:
1. AuthorizationEndpoint:用来作为请求者获得授权的服务，Default URL: /oauth/authorize
   - 请求参数在 OAuth2Utils 类中
2. TokenEndpoint：用来作为请求者获得令牌（Token）的服务，Default URL: /oauth/token

##### Resource Server:
OAuth2AuthenticationProcessingFilter：用来作为认证令牌（Token）的一个处理流程过滤器。只有当过滤器通过之后，请求者才能获得受保护的资源

### 详细介绍一下Authorization Server
一般情况下，创建两个配置类，一个继承 AuthorizationServerConfigurerAdapter，一个继承 WebSecurityConfigurerAdapter，再去复写里面的方法

### 主要出现的两种注解：
#### @EnableAuthorizationServer：声明一个认证服务器，当用此注解后，应用启动后将自动生成几个Endpoint：
> 注：其实实现一个认证服务器就是这么简单，加一个注解就搞定，当然真正用到生产环境还是要进行一些配置和复写工作的。

````
/oauth/authorize：授权端点

/oauth/token：获取token

/oauth/confirm_access：用户确认授权提交端点

/oauth/error：授权服务错误信息端点

/oauth/check_token：用于资源服务访问的令牌解析端点。

/oauth/token_key：提供公有密匙的端点。如果jwt模式则可以用此来从认证服务器获取公钥
````

以上这些endpoint都在源码里的 endpoint 包里面。


####  配置类继承 AuthorizationServerConfigurer
AuthorizationServerConfigurer包含三种配置：    
(1) ClientDetailsServiceConfigurer：client客户端的信息配置，client信息包括：clientId、secret、scope、authorizedGrantTypes、authorities     

参数说明：  
- clientId：（必须的）用来标识客户的Id
- secret：（需要值得信任的客户端）客户端安全码，如果有的话
- scope：用来限制客户端的访问范围，如果为空（默认）的话，那么客户端拥有全部的访问范围，可选项，用户授权页面时进行选择     
- authorizedGrantTypes：此客户端可以使用的授权类型,有四种授权方式             
    - Authorization Code：用验证获取code，再用code去获取token（用的最多的方式，也是最安全的方式）
    - Implicit: 隐式授权模式（少了获取 code 的步骤）
    - Client Credentials ：客户端凭据(机器对机器，获取 Access Token)
    - Resource Owner Password Credentials （用户密码模式）
- authorities：此客户端可以使用的权限

#### scopes和authorities的区别：
scopes是client权限，至少授予一个scope的权限，否则报错。
authorities是用户权限。

这里的具体实现有多种，in-memory、JdbcClientDetailsService、jwt等。

(2) AuthorizationServerSecurityConfigurer：声明安全约束，哪些允许访问，哪些不允许访问

(3) AuthorizationServerEndpointsConfigurer：声明授权和token的端点以及token的服务的一些配置信息，比如采用什么存储方式、token的有效期等


### 介绍一下如何管理token:
AuthorizationServerTokenServices 接口:声明必要的关于token的操作

（1）当一个令牌被创建了，你必须对其进行保存，这样当一个客户端使用这个令牌对资源服务进行请求的时候才能够引用这个令牌。

（2）当一个令牌是有效的时候，它可以被用来加载身份信息，里面包含了这个令牌的相关权限。

接口的实现也有多种，DefaultTokenServices是其默认实现(可以使用该类来修改令牌的格式和令牌的存储)，默认使用的InMemoryTokenStore，不会持久化token；

#### token存储方式共有三种分别是：
（1）InMemoryTokenStore：存放内存中，不会持久化   
（2）JdbcTokenStore：存放数据库中    
（3）Jwt: json web token,详见 http://blog.leapoahead.com/2015/09/06/understanding-jwt/  

#### 配置授权类型（Grant Types）：
- authenticationManager：认证管理器，当你选择了资源所有者密码（password）授权类型的时候，请设置这个属性注入一个 AuthenticationManager 对象。
- userDetailsService：你设置了这个属性的话，那说明你有一个自己的 UserDetailsService 接口的实现，    
   或者你可以把这个东西设置到全局域上面去（例如 GlobalAuthenticationManagerConfigurer 这个配置对象），
   当你设置了这个之后，那么 "refresh_token" 即刷新令牌授权类型模式的流程中就会包含一个检查，用来确保这个账号是否仍然有效，假如说你禁用了这个账户的话。
- authorizationCodeServices：这个属性是用来设置授权码服务的（即 AuthorizationCodeServices 的实例对象），主要用于 "authorization_code" 授权码类型模式。
- implicitGrantService：这个属性用于设置隐式授权模式，用来管理隐式授权模式的状态。
- tokenGranter：这个属性就很牛B了，当你设置了这个东西（即 TokenGranter 接口实现），那么授权将会交由你来完全掌控，并且会忽略掉上面的这几个属性，
  这个属性一般是用作拓展用途的，即标准的四种授权模式已经满足不了你的需求的时候，才会考虑使用这个。


#### 配置授权端点的URL（Endpoint URLs）：
AuthorizationServerEndpointsConfigurer 这个配置对象 有一个叫做 pathMapping() 的方法用来配置端点URL链接，它有两个参数：
第一个参数：String 类型的，这个端点URL的默认链接。
第二个参数：String 类型的，你要进行替代的URL链接。
以上的参数都将以 "/" 字符为开始的字符串，框架的默认URL链接如下列表，可以作为这个 pathMapping() 方法的第一个参数：
/oauth/authorize：授权端点。
/oauth/token：令牌端点。
/oauth/confirm_access：用户确认授权提交端点。
/oauth/error：授权服务错误信息端点。
/oauth/check_token：用于资源服务访问的令牌解析端点。
/oauth/token_key：提供公有密匙的端点，如果你使用JWT令牌的话。

需要注意的是授权端点这个URL应该被Spring Security保护起来只供授权用户访问   

WebSecurityConfigurer 的实例，可以配置哪些路径不需要保护，哪些需要保护。默认全都保护

### 自定义错误处理（Error Handling）：
端点实际上就是一个特殊的Controller，它用于返回一些对象数据。
授权服务的错误信息是使用标准的Spring MVC来进行处理的，也就是 @ExceptionHandler 注解的端点方法，
你也可以提供一个 WebResponseExceptionTranslator 对象。最好的方式是改变响应的内容而不是直接进行渲染。
假如说在呈现令牌端点的时候发生了异常，那么异常委托了 HttpMessageConverters 对象（它能够被添加到MVC配置中）来进行输出。
假如说在呈现授权端点的时候未通过验证，则会被重定向到 /oauth/error 即错误信息端点中。
whitelabel error （即Spring框架提供的一个默认错误页面）错误端点提供了HTML的响应，
但是你大概可能需要实现一个自定义错误页面（例如只是简单的增加一个 @Controller 映射到请求路径上 @RequestMapping("/oauth/error")）。

### 自定义UI:

（1）有时候，我们可能需要自定义的登录页面和认证页面。登陆页面的话，只需要创建一个login为前缀名的网页即可，在代码里，设置为允许访问，这样，
  系统会自动执行你的登陆页。此登陆页的action要注意一下，必须是跳转到认证的地址。

（2）另外一个是授权页，让你勾选选项的页面。此页面可以参考源码里的实现，自己生成一个controller的类，再创建一个对应的web页面即可实现自定义的功能。

### Resource Server：保护资源，需要令牌才能访问
你可以通过 @EnableResourceServer 注解到一个 @Configuration 配置类上，并且必须使用 ResourceServerConfigurer 这个配置对象来进行配置
（可以选择继承自 ResourceServerConfigurerAdapter 然后覆写其中的方法，参数就是这个对象的实例），下面是一些可以配置的属性：

- tokenServices：ResourceServerTokenServices 类的实例，用来实现令牌服务。
- resourceId：这个资源服务的ID，这个属性是可选的，但是推荐设置并在授权服务中进行验证。
- 其他的拓展属性例如 tokenExtractor 令牌提取器用来提取请求中的令牌。
- 请求匹配器，用来设置需要进行保护的资源路径，默认的情况下是受保护资源服务的全部路径。
- 受保护资源的访问规则，默认的规则是简单的身份验证（plain authenticated）。
- 其他的自定义权限保护规则通过 HttpSecurity 来进行配置。

使用token的方式也有两种：
（1）Bearer Token（https传输方式保证传输过程的安全）:主流
（2）Mac（http+sign）

### 如何访问资源服务器中的API？
如果资源服务器和授权服务器在同一个应用程序中，并且您使用DefaultTokenServices，那么您不必太考虑这一点，
因为它实现所有必要的接口，因此它是自动一致的。如果您的资源服务器是一个单独的应用程序，那么您必须确保您匹配授权服务器的功能，
并提供知道如何正确解码令牌的ResourceServerTokenServices。与授权服务器一样，您可以经常使用DefaultTokenServices，
并且选项大多通过TokenStore（后端存储或本地编码）表示。

（1）在校验request中的token时，使用RemoteTokenServices去调用AuthServer中的/auth/check_token。
（2）共享数据库，使用Jdbc存储和校验token，避免再去访问AuthServer。
（3）使用JWT签名的方式，资源服务器自己直接进行校验，不借助任何中间媒介。

#### 相关接口相关接口
Spring Cloud Security OAuth2通过 DefaultTokenServices类 来完成token生成、过期等 OAuth2 标准规定的业务逻辑，
而 DefaultTokenServices 又是通过 TokenStore接口 完成对生成数据的持久化。

TokenStore 的默认实现为 InMemoryTokenStore，即内存存储。
对于Client信息，ClientDetailsService 接口 负责从存储仓库中读取数据

框架已经为我们写好JDBC实现了，即 JdbcTokenStore 和 JdbcClientDetailsService。