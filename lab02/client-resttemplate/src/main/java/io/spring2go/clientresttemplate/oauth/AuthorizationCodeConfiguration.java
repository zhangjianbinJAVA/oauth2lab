package io.spring2go.clientresttemplate.oauth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.Base64;

@Component
public class AuthorizationCodeConfiguration {

    /**
     * 客户端凭证
     *
     * @param username
     * @param password
     * @return
     */
    public String encodeCredentials(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = new String(Base64.getEncoder().encode(
                credentials.getBytes()));

        return encoded;
    }

    /**
     * 授权 请求体
     *
     * @param authorizationCode
     * @return
     */
    public MultiValueMap<String, String> getBody(String authorizationCode) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("scope", "read_userinfo");
        formData.add("code", authorizationCode);
        formData.add("redirect_uri", "http://localhost:9001/callback");
        return formData;
    }

    /**
     * 根据 token 获取数据 请求头
     *
     * @param clientAuthentication
     * @return
     */
    public HttpHeaders getHeader(String clientAuthentication) {
        HttpHeaders httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        httpHeaders.add("Authorization", "Basic " + clientAuthentication);

        return httpHeaders;
    }

}
