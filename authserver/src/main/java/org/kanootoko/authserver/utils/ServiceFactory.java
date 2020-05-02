package org.kanootoko.authserver.utils;

import org.kanootoko.authserver.services.UserService;
import org.kanootoko.authserver.services.defaults.DefaultUserService;

/**
 * ServiceFactory is a sample service factory which serves only one sample
 * UserService at the moment.
 */
public class ServiceFactory {

    private static UserService userService = new DefaultUserService();

    public static UserService getUserService() {
        return userService;
    }
}