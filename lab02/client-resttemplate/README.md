客户端以授权码方式访问OAuth2服务器案例，使用rest template
======

# 实验注意事项

1. 该实验开始前需要预先安装mysql数据库，再使用mysql workbench预先执行sql脚本src\main\resources\db\oauth_client.sql
2. 该案例需先启动lab01中的支持授权码模式的OAuth2服务器，端口在8080。
3. 再运行本案例web应用，端口在9001，浏览器访问http://locahost:9001， 按提示操作即可。
4. 该案例授权成功后，客户端会在数据库中缓存访问令牌（可通过mysql workbench查看），如果OAuth2服务器使用内存模式，则重启OAuth2服务器后原访问令牌将失效，需要清除数据库令牌再重新授权。


### 实际操作
1. 浏览器访问 http://localhost:9001  到主页
2. 点击连接 访问 /mainpage 时，转到 登录页
3. 登录后，重定向到 授权服务器，这时授权服务器进行登录
4. 授权服务器登录后，询问 是否授权 xxx 访问您的受保护资源
5. 允许授权时，重定向到 /mainpage 页面 并显示数据