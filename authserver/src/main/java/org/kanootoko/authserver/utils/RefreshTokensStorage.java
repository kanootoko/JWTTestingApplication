package org.kanootoko.authserver.utils;

/**
 * RefreshTokensStorage is an interface which provides an access to some
 * database where users refresh tokens can be stored even if server reloads
 */
public interface RefreshTokensStorage {
    /**
     * put is a method that takes a pair of username and refresh token and put them
     * to the srotage.
     * 
     * @param username     - login of the user
     * @param refreshToken - JSON Web Token refresh token as String
     */
    public void put(String username, String refreshToken);

    /**
     * containsKey is a method that returns true if username is present in the
     * storage and false otherwise,
     * 
     * @param username - login of the user
     * @return true if username is present in the storage, false otherwise
     */
    public boolean containsKey(String username);

    /**
     * get is a method that returns the JWT refresh token associated with username
     * in the storage.
     * 
     * @param username - login of the user
     * @return JSON Web Token refresh token associated with username in the storage
     */
    public String get(String username);

    /**
     * deleteExpires is a method which tests all the tokens in the storage for
     * expiration date and removes the expired ones. Kind of maintence function.
     */
    public void deleteExpired();

    /**
     * flush is a method which ensures that storage have the most recent data saved.
     */
    public void flush();
}