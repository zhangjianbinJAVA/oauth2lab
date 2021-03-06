package io.spring2go.implicitserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

//授权服务器配置
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServer extends
        AuthorizationServerConfigurerAdapter {

    @Override
    public void configure(ClientDetailsServiceConfigurer clients)
            throws Exception {
        clients.inMemory()
                .withClient("clientapp")
                .secret("112233")

                // 客户端回调地址
                .redirectUris("http://localhost:9001/callback")
                // 只支持简化模式
                .authorizedGrantTypes("implicit")
                .accessTokenValiditySeconds(120) //令牌的有效期 120秒
                .scopes("read_userinfo", "read_contacts");
    }

}
