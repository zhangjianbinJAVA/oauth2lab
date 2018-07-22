package io.spring2go.clientresttemplate.dao;

import io.spring2go.clientresttemplate.entity.ClientUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<ClientUser, Long> {

    Optional<ClientUser> findByUsername(String username);

}
