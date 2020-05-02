package org.kanootoko.authserver.models;

/**
 * Tokens is a structure which stores a pair of JWT access token and refresh token
 */
public class Tokens {
    public String accessToken;
    public String refreshToken;

    public Tokens() {}
    public Tokens(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}