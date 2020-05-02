package org.kanootoko.authserver.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * RefreshTokensStorageFile is a temporary class which holds users tokens in
 * file, in format "<username>@%@<token>".
 */
public class RefreshTokensStorageFile implements RefreshTokensStorage {

    private Map<String, String> map;
    private final String filename;

    /**
     * RefreshTokenStorageFile basic constructor creates the class by the given
     * filename - that file will be loaded on start, and then rewrited on flush.
     * 
     * @param filename - filename of the file to store pairs of logins and refresh
     *                 tokens
     */
    public RefreshTokensStorageFile(String filename) {
        map = new HashMap<>();
        this.filename = filename;
        try (Reader reader = new FileReader(filename); BufferedReader br = new BufferedReader(reader)) {
            String[] keyAndValue = br.readLine().split("@%@");
            map.put(keyAndValue[0], keyAndValue[1]);
        } catch (IOException e) {
        }
    }

    /**
     * put is a method which a paif of username and refresh token to the map and
     * then calls flush to write changes to file.
     * 
     * @param username     - login of the user
     * @param refreshToken - JSON Web Token refresh token as String
     */
    @Override
    public void put(String username, String refreshToken) {
        map.put(username, refreshToken);
        flush();
    }

    /**
     * containsKey is a method which returns true if login is in map (file), false
     * otherwise.
     * 
     * @param username - login of the user
     */
    @Override
    public boolean containsKey(String username) {
        return map.containsKey(username);
    }

    /**
     * get is a method which returns JWT refresh token of the given username if it
     * is present in map (file).
     * 
     * @param username - login of the user
     */
    @Override
    public String get(String username) {
        return map.get(username);
    }

    /**
     * deleteExpired is a method which tests all of the JWTs in the map (file) for
     * the expiration and removes expired ones.
     */
    @Override
    public void deleteExpired() {
        Date now = new Date();
        map.entrySet().stream()
                .filter(entry -> JWTUtil.getExpirationDateFromToken(entry.getValue().substring(7)).before(now))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));
    }

    /**
     * flush is a method which ensures that file has the most recent data.
     */
    @Override
    public void flush() {
        try (Writer writer = new FileWriter(filename); BufferedWriter bw = new BufferedWriter(writer)) {
            for (Entry<String, String> entry : map.entrySet()) {
                bw.write(entry.getKey() + "@%@" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
        }
    }

}