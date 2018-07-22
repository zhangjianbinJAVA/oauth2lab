基于授权码模式+Spring Security OAuth2的最简授权服务器
======

### 授权服务器和资源服务器在生产中实践是分开的写的，本例是为演示，所以写在一起了

# 操作方式

### 1. 获取授权码

浏览器请求：

http://localhost:8080/oauth/authorize?client_id=clientapp&redirect_uri=http://localhost:9001/callback&response_type=code&scope=read_userinfo

**参数**
- client_id :表示客户端的ID，用来标志授权请求的来源，必选项   
- redirect_uri :成功授权后的回调地址
- response_type ：通过哪种方式授权，这里 通过授权码的方式进行授权
- scope：表示申请的权限范围，可选项；
- state：表示客户端的当前状态，可以指定任意值，认证服务器会原封不动地返回这个值。

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
- post 请求url 及参数 申请令牌
http://localhost:8080/oauth/token?code=Jar7Qz&grant_type=authorization_code&redirect_uri=http://localhost:9001/callback&scope=read_userinfo

- grant_type：表示授权类型，此处的值固定为"authorization_code"，必选项；
- client_id：用来标志请求的来源，必选项；(如：表示从QQ互联平台申请到的客户端ID)
- client_secret：机密信息十分重要，必选项；(如： 这个是从QQ互联平台申请到的客户端认证密钥)
- redirect_uri：成功申请到令牌后的回调地址；
- code：上一步申请到的授权码。

```json
{
    "access_token": "0548f395-048d-409e-9d2c-a1e119e3d3c8",
    "token_type": "bearer",
    "expires_in": 40962,
    "scope": "read_userinfo"
}
```
- access_token：令牌
- expires_in：access token的有效期，单位为秒。
- refresh_token：在授权自动续期步骤中，获取新的Access_Token时需要提供的参数


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


