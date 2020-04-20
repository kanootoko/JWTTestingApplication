package org.kanootoko.authserver.utils;

import org.kanootoko.authserver.services.UserService;
import org.kanootoko.authserver.services.defaults.DefaultUserService;

public class ServiceFactory {
    
    private static UserService userService = new DefaultUserService();

    public static UserService getUserService() {
        return userService;
    }
}