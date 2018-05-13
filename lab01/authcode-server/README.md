基于授权码模式+Spring Security OAuth2的最简授权服务器
======

### 授权服务器和资源服务器在生产中实践是分开的写的，本例是为演示，所以写在一起了

# 操作方式

### 1. 获取授权码

浏览器请求：

http://localhost:8080/oauth/authorize?client_id=clientapp&redirect_uri=http://localhost:9001/callback&response_type=code&scope=read_userinfo

**参数**
- client_id
- redirect_uri :重定向的 uri
- response_type ：通过哪种方式授权，这里 通过授权码的方式进行授权
- scope：授权信息

**注意：state参数暂忽略**

响应案例：

http://localhost:9001/callback?code=8uYpdo

### 2. 获取访问令牌

curl -X POST --user clientapp:112233 http://localhost:8080/oauth/token -H
"content-type: application/x-www-form-urlencoded" -d
"code=8uYpdo&grant_type=authorization_code&redirect_uri=http://localhost:9001/callback&scope=read_userinfo"

案例响应：

```json
{
    "access_token": "36cded80-b6f5-43b7-bdfc-594788a24530",
    "token_type": "bearer",
    "expires_in": 43199,
    "scope": "read_userinfo"
}
```

**postman**
- basic auth  username:clientapp   password:112233
- heads  content-type:application/x-www-form-urlencoded
- post 请求url 及参数    
http://localhost:8080/oauth/token?code=Jar7Qz&grant_type=authorization_code&redirect_uri=http://localhost:9001/callback&scope=read_userinfo


```json
{
    "access_token": "0548f395-048d-409e-9d2c-a1e119e3d3c8",
    "token_type": "bearer",
    "expires_in": 40962,
    "scope": "read_userinfo"
}
```



### 3. 调用API

curl -X GET http://localhost:8080/api/userinfo -H "authorization: Bearer 36cded80-b6f5-43b7-bdfc-594788a24530"

案例响应：

```json
{
    "name": "bobo",
    "email": "bobo@spring2go.com"
}
```

**postman**
- get 请求   
http://localhost:8080/api/userinfo
- 认证填写 上面的 access_token 参数，才能访问正常的 url，如果 access_token 不正确，则不能正常的访问 api


