package io.spring2go.clientresttemplate.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 授权相关的逻辑
 */
@Service
public class AuthorizationCodeTokenService {
    @Autowired
    private AuthorizationCodeConfiguration configuration;

    /**
     * 获取 授权端点
     * <p>
     * 拼接授权 url
     *
     * @return
     */
    public String getAuthorizationEndpoint() {
        // 授权服务器 url
        String endpoint = "http://localhost:8080/oauth/authorize";

        // 授权相关参数
        Map<String, String> authParameters = new HashMap<>();
        authParameters.put("client_id", "clientapp");
        authParameters.put("response_type", "code"); //使用 授权码的方式 获取 access_token
        authParameters.put("redirect_uri",
                getEncodedUrl("http://localhost:9001/callback")); //回调的地址

        authParameters.put("scope", getEncodedUrl("read_userinfo"));

        return buildUrl(endpoint, authParameters);
    }

    private String buildUrl(String endpoint, Map<String, String> parameters) {
        List<String> paramList = new ArrayList<>(parameters.size());

        parameters.forEach((name, value) -> {
            paramList.add(name + "=" + value);
        });

        return endpoint + "?" + paramList.stream()
                .reduce((a, b) -> a + "&" + b).get();
    }

    private String getEncodedUrl(String url) {
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 通过 code(授权码) 获取  token
     *
     * @param authorizationCode
     * @return
     */
    public OAuth2Token getToken(String authorizationCode) {
        RestTemplate rest = new RestTemplate();

        //拼装 客户端认证 信息
        String authBase64 = configuration.encodeCredentials("clientapp",
                "112233");

        RequestEntity<MultiValueMap<String, String>> requestEntity = new RequestEntity<>(
                // 拼装 请求 body
                configuration.getBody(authorizationCode),
                // 拼装 请求头
                configuration.getHeader(authBase64),
                HttpMethod.POST,
                URI.create("http://localhost:8080/oauth/token"));

        // json 转为 实体
        ResponseEntity<OAuth2Token> responseEntity = rest.exchange(
                requestEntity, OAuth2Token.class);

        if (responseEntity.getStatusCode().is2xxSuccessful()) {
            return responseEntity.getBody();
        }

        throw new RuntimeException("error trying to retrieve access token");
    }
}
