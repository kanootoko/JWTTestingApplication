package org.kanootoko.authserver.services;

import org.kanootoko.authserver.exceptions.UserNotFoundException;
import org.kanootoko.authserver.models.entities.User;
import org.springframework.stereotype.Service;

/**
 * UserService is a sample user service interface which can return user by its
 * login.
 */
@Service
public interface UserService {
    public User getUserByLogin(String login) throws UserNotFoundException;
}
