package io.spring2go.clientresttemplate.service;


import io.spring2go.clientresttemplate.dao.UserRepository;
import io.spring2go.clientresttemplate.entity.ClientUser;
import io.spring2go.clientresttemplate.security.ClientUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository users;

    /**
     * 根据 用户名从数据库中获取 用户信息
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        Optional<ClientUser> optionalUser = users.findByUsername(username);

        if (!optionalUser.isPresent()) {
            throw new UsernameNotFoundException("invalid username or password");
        }

        /**
         * 将用户信息访问 客户端对象中
         */
        return new ClientUserDetails(optionalUser.get());
    }

}
