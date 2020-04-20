package org.kanootoko.authserver.services;

import org.kanootoko.authserver.exceptions.UserNotFoundException;
import org.kanootoko.authserver.models.entities.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public User getUserByLogin(String login) throws UserNotFoundException;
}
