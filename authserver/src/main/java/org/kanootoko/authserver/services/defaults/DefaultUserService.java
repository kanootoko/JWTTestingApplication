package org.kanootoko.authserver.services.defaults;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.kanootoko.authserver.exceptions.UserNotFoundException;
import org.kanootoko.authserver.models.entities.User;
import org.kanootoko.authserver.models.entities.defaults.DefaultUser;
import org.kanootoko.authserver.services.UserService;
import org.springframework.stereotype.Service;

/**
 * utils is a temporary class which serves the function of getting users from
 * txt file where they are stored in format "login password role".
 */
class utils {
    public static Map<String, User> getUsersFromFile(String filename) {
        Map<String, User> users = new HashMap<>();
        try (Reader reader = new FileReader(filename); BufferedReader bf = new BufferedReader(reader)) {
            while (true) {
                String line = bf.readLine();
                if (line == null)
                    break;
                String[] loginPasswordRole = line.trim().split("\\s+");
                users.put(loginPasswordRole[0],
                        new DefaultUser(loginPasswordRole[0], loginPasswordRole[1], loginPasswordRole[2]));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error getting users from file: " + filename);
        }
        return users;
    }
}

/**
 * DefaultUserService is a sample user service class which reads users from txt
 * file, put them on the map and then check the presence by the map
 */
@Service
public class DefaultUserService implements UserService {

    private static String filename = "target/classes/users.txt";

    private static final Map<String, User> users = utils.getUsersFromFile(filename);

    @Override
    public User getUserByLogin(String login) throws UserNotFoundException {
        if (users.containsKey(login)) {
            return users.get(login);
        } else {
            throw new UserNotFoundException("User \"" + login + "\" not found");
        }
    }
}
