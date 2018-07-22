package io.spring2go.authcodeserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * 资源服务器，保护受保护的资源
 */
@Configuration
//启用资源服务器
@EnableResourceServer
public class OAuth2ResourceServer extends ResourceServerConfigurerAdapter {


    /**
     * 自定义的资源保护配置
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                // 对请求进行认证
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .requestMatchers()

                //配置哪些资源 需要使用 oauth2 认证，这里 访问 /api　下的资源就必须带着　token 来访问
                .antMatchers("/api/**");
    }

}
