package io.spring2go.authcodeserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

/**
 * 授权服务器配置
 */
@Configuration
//启用授权服务器
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends
        AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients.inMemory() //授权信息、客户信息、token 存在内容中
                .withClient("clientapp")//客户的名称
                .secret("112233") // clientapp 和 secret 这两个信息表是客户凭证，一般来讲是授权服务器注册的
                .redirectUris("http://localhost:9001/callback") //回调url ,拿到授权码后怎么跳回到客户端
                // 授权码模式
                .authorizedGrantTypes("authorization_code") //这里只支持授权码模式
                .scopes("read_userinfo", "read_contacts"); //scopes 细分权限，这里可以读 用户信息，也可以读 联系人信息
    }

}
