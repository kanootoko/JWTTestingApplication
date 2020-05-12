package org.kanootoko.authserver.utils;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import javax.crypto.spec.SecretKeySpec;

import org.kanootoko.authserver.exceptions.TokenLogicException;
import org.kanootoko.authserver.exceptions.UserNotFoundException;
import org.kanootoko.authserver.models.Tokens;
import org.kanootoko.authserver.models.entities.User;
import org.kanootoko.authserver.services.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * utils is a temporary class which serves the task of loading secretKey from
 * file.
 */
class utils {
    private static final String filename = "target/classes/secretKey.txt";

    public static Key getSecretKey() {
        try {
            byte[] key_encoded = Files.readAllBytes(Paths.get(filename));
            return new SecretKeySpec(key_encoded, 0, key_encoded.length, "HmacSHA512");
        } catch (Exception ex) {
            throw new RuntimeException("Cannot read the secret key from " + filename);
        }
    }
}

/**
 * JWTUtil is a class which offers the ability to work with JSON Web Tokens in a
 * simple way. Every method that operate with tokens will throw one of the JWT
 * Exceptions (<i>UnsupportedJwtException, MalformedJwtException,
 * SignatureException,ExpiredJwtException</i>) if something goes wrong.
 */
public class JWTUtil implements Serializable {

    private static final long serialVersionUID = 456842083179721L;

    private static final Key secretKey = utils.getSecretKey();

    // Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    // private void saveSecretKey() {
    // try {
    // System.out.println("Secret key algo: " + secretKey.getAlgorithm());
    // Files.write(Paths.get("src/main/resources/secretKey.txt"),
    // secretKey.getEncoded());
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
    // }

    private int accessTokenValiditySeconds;
    private int refreshTokenValiditySeconds;

    private final RefreshTokensStorage refreshTokensByUsername;

    /**
     * generateRefreshToken is a method which generates JSON Web Token refresh token
     * for given user. Token starts with "Bearer ". Token contains username, current
     * date and expiration date.
     * 
     * @param user - user whose name will be used for token generation
     * @return JWT refresh token as String
     */
    private String generateRefreshToken(User user) {
        String token = "Bearer " + Jwts.builder().setClaims(new HashMap<String, Object>()).setId(user.getLogin())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValiditySeconds * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS512).compact();
        refreshTokensByUsername.put(user.getLogin(), token.substring(7));
        return token;
    }

    /**
     * generateAccessToken is a method which generates JSON Web Token access token
     * for given user. Token starts with "Bearer ". Token contains username, user
     * role current date and expiration date.
     * 
     * @param user - user whose name and role will be used for token generation
     * @return JWT access token as String
     */
    private String generateAccessToken(User user) {
        String token = "Bearer " + Jwts.builder().setClaims(new HashMap<String, Object>()).setId(user.getLogin())
                .setSubject(user.getRole()).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValiditySeconds * 1000))
                .signWith(secretKey, SignatureAlgorithm.HS512).compact();
        return token;
    }

    /**
     * JWTUtil basic constructor which sets validity time of access token for 5
     * minutes and validity time of refresh token for 15 days.
     * 
     * @param refreshTokensStorage - accessor for some storage where pairs "username
     *                             - token" can be stored as in Map<String, String>
     */
    public JWTUtil(RefreshTokensStorage refreshTokensStorage) {
        accessTokenValiditySeconds = 5 * 60;
        refreshTokenValiditySeconds = 15 * 24 * 60 * 60;
        refreshTokensByUsername = refreshTokensStorage;
        refreshTokensStorage.deleteExpired();
    }

    /**
     * JWTUtil constructor with setting the validity time of refresh and access
     * tokens.
     * 
     * @param refreshTokensStorage        - accessor for some storage where pairs
     *                                    "username - token" can be stored as in
     *                                    Map<String, String>
     * @param accessTokenValiditySeconds  - time in seconds after which access token
     *                                    will be count as expired (should be
     *                                    minutes - hours)
     * @param refreshTokenValiditySeconds - time in seconds after which refresh
     *                                    token will be count as expired (should be
     *                                    days)
     */
    public JWTUtil(RefreshTokensStorage refreshTokensStorage, int accessTokenValiditySeconds,
            int refreshTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
        this.refreshTokensByUsername = refreshTokensStorage;
        refreshTokensStorage.deleteExpired();
    }

    /**
     * generateTokens is a method which generates access and refresh tokens based on
     * user login and role.
     * 
     * @param user - user whose name and role will be used for token generation
     * @return
     */
    public Tokens generateTokens(User user) {
        return new Tokens(generateAccessToken(user), generateRefreshToken(user));
    }

    /**
     * refreshTokens is a method which returns new pair of auth and refresh tokens
     * by consuming the old refresh token making it invalid. Only one refresh token
     * can be vaild at a time.
     * 
     * @param refreshToken - old refresh token which must be valid to refresh
     * @return new pair of access and refresh JWT tokens
     * @throws TokenLogicException if refresh fails (user was not logged in, old
     *                             refresh token has expired, user is not in the
     *                             database anymore)
     */
    public Tokens refreshTokens(String refreshToken) throws TokenLogicException {
        if (refreshToken.startsWith("Bearer ")) {
            refreshToken = refreshToken.substring(7);
        }
        String username = getLoginFromToken(refreshToken);
        if (refreshTokensByUsername.containsKey(username)
                && refreshTokensByUsername.get(username).equals(refreshToken)) {
            if (isTokenExpired(refreshToken)) {
                throw new TokenLogicException("Refresh token is expired, need to perform login");
            }
            UserService userService = ServiceFactory.getUserService();
            User user;
            try {
                user = userService.getUserByLogin(username);
            } catch (UserNotFoundException e) {
                throw new TokenLogicException("User not found");
            }
            refreshToken = generateRefreshToken(user);
            refreshTokensByUsername.put(username, refreshToken.substring(7));
            return new Tokens(generateAccessToken(user), refreshToken);
        } else {
            throw new TokenLogicException("Refresh token is not found");
        }
    }

    /**
     * flush is a method which ensures that RefreshTokenStorage has the most fresh
     * data.
     */
    public void flush() {
        refreshTokensByUsername.flush();
    }

    /**
     * getAllClaimsFromToken is a method which parses token and return all the
     * insides of JWT.
     * 
     * @param token - JSON Web Token as String
     * @return all the values inside given JWT
     */
    public static Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    /**
     * getClaimFromToken is a method which gets one claim from token with function.
     * 
     * @param <T>            - type that will be returned and which function
     *                       claimsResolver must return
     * @param token          - JSON Web Token as String
     * @param claimsResolver - function which takes Claims as the only parameter and
     *                       returns the given type
     * @return value of the requested claim
     */
    public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * getLoginFromToken is a method which returns the login from ID claim of JWT.
     * 
     * @param token - JSON Web Token as String
     * @return user login from JWT
     */
    public static String getLoginFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return getClaimFromToken(token, Claims::getId);
    }

    /**
     * getAuthorityFromToken is a method which returns the user role from Subject
     * claim of JWT.
     * 
     * @param token - JSON Web Token as String
     * @return user role from JWT
     */
    public static String getAuthorityFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * getIssuedAtDateFromToken is a method which returns the time of creation from
     * IssuedAt claim of JWT
     * 
     * @param token - JSON Web Token as String
     * @return date of creation of JWT
     */
    public static Date getIssuedAtDateFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    /**
     * getExpirationDateFromToken is a method which returns the time when token will
     * become expired from Expiration claim of JWT
     * 
     * @param token - JSON Web Token as String
     * @return date of creation of JWT
     */
    public static Date getExpirationDateFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * isTokenExpired is a method which returns true if token is expired and false
     * otherwise.
     * 
     * @param token - JSON Web Token as String
     * @return true if JWT is expired, false otherwise
     */
    public static boolean isTokenExpired(String token) {
        return getExpirationDateFromToken(token).before(new Date());
    }

    /**
     * validateToken is a method which returns true if token is valid (not expired
     * now).
     * 
     * @param token - JSON Web Token as String
     * @return true if JWT is valid and not expired
     */
    public static boolean validateToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return !isTokenExpired(token);
    }
}
