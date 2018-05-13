package io.spring2go.authcodeserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

//资源服务配置
@Configuration
@EnableResourceServer //启用资源服务器
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest()
            .authenticated()
        .and()
            .requestMatchers()

            //配置哪些资源 需要使用 oauth2 认证，这里 访问 /api　下的资源就必须带着　token 来访问
            .antMatchers("/api/**");
    }

}
