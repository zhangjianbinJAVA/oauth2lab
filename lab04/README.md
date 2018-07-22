Android对接OAuth2授权服务器（基于Spring Security OAuth2+内存H2数据库）
======

# 操作方式

### 1. 启动mobile_authserver，端口8080

### 2. 在Android Studio中启动AuthCodeApp

### 3. 更新授权服务器ip地址
AuthCodeApp\app\src\main\java\spring2go\io\authcodeapp\client\ClientAPI中的BASE_URL地址为mobile_authserver的ip地址

### 4. 校验

1. 校验h2内存数据库生成的访问令牌 http://localhost:8080/h2-console
2. Android Studio中的Device File Explorer查看
```
data\data\spring2go.io.authcodeapp\shared_prefs
```


#### web页面登录 h2数据据
http://127.0.0.1:8080/h2-console/

#### 通过 postman 访问授权码模式
- http://localhost:8080/oauth/authorize?client_id=mobileclient&redirect_uri=http://localhost:9000/callback&response_type=code&scope=read_userinfo
- http://localhost:8080/oauth/token?code=82S75Z&grant_type=authorization_code&redirect_uri=http://localhost:9000/callback&scope=read_userinfo