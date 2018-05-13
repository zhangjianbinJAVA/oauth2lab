package io.spring2go.clientresttemplate.user;

import io.spring2go.clientresttemplate.oauth.AuthorizationCodeTokenService;
import io.spring2go.clientresttemplate.oauth.OAuth2Token;
import io.spring2go.clientresttemplate.security.ClientUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

@Controller
public class MainPage {
    @Autowired
    private AuthorizationCodeTokenService tokenService;

    @Autowired
    private UserRepository users;

    /**
     * 首页
     *
     * @return
     */
    @GetMapping("/")
    public String home() {
        return "index";
    }

    /**
     * 授权服务器 回调地址
     *
     * @param code
     * @param state
     * @return
     */
    @GetMapping("/callback")
    public ModelAndView callback(String code, String state) {
        ClientUserDetails userDetails = (ClientUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        ClientUser clientUser = userDetails.getClientUser();

        //通过 授权码 code 获取 access_token
        OAuth2Token token = tokenService.getToken(code);

        clientUser.setAccessToken(token.getAccessToken());

        Calendar tokenValidity = Calendar.getInstance();
        tokenValidity.setTime(new Date(Long.parseLong(token.getExpiresIn()))); // access_token 的有效期
        clientUser.setAccessTokenValidity(tokenValidity);

        // 保存 信息 ，通过 授权码code 已经交换到了 access_token 令牌了，并保存起来
        users.save(clientUser);

        // 重定向到 页面
        return new ModelAndView("redirect:/mainpage");
    }

    @GetMapping("/mainpage")
    public ModelAndView mainpage() {
        ClientUserDetails userDetails = (ClientUserDetails) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();

        // 获取 用户数据
        ClientUser clientUser = userDetails.getClientUser();

        if (StringUtils.isEmpty(clientUser.getAccessToken())) {//没用令牌时，需要去授权服务器进行授权 1.先获取授权码code 2。通过授权码获取token
            //获取 授权端点
            String authEndpoint = tokenService.getAuthorizationEndpoint();
            //重定向到 授权端点
            return new ModelAndView("redirect:" + authEndpoint);
        }

        clientUser.setEntries(Arrays.asList(
                new Entry("entry 1"),
                new Entry("entry 2")));

        ModelAndView mv = new ModelAndView("mainpage");
        mv.addObject("user", clientUser);

        //通过 access_token 获取 资源服务器上的资源信息
        tryToGetUserInfo(mv, clientUser.getAccessToken());

        return mv;
    }

    private void tryToGetUserInfo(ModelAndView mv, String token) {
        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Authorization", "Bearer " + token);

        // 资源服务器上的资源 url
        String endpoint = "http://localhost:8080/api/userinfo";

        try {
            RequestEntity<Object> request = new RequestEntity<>(
                    headers, HttpMethod.GET, URI.create(endpoint));

            ResponseEntity<UserInfo> userInfo = restTemplate.exchange(request, UserInfo.class);

            if (userInfo.getStatusCode().is2xxSuccessful()) {
                mv.addObject("userInfo", userInfo.getBody());
            } else {
                throw new RuntimeException("it was not possible to retrieve user profile");
            }
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("it was not possible to retrieve user profile");
        }
    }

}
