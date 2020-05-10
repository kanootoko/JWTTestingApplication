package org.kanootoko.jwtchecker.utils;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

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
 * JWTUtilSimplified is a class which offers the ability to work with JSON Web
 * Tokens in a simple way. Every method that operate with tokens will throw one
 * of the JWT Exceptions (<i>UnsupportedJwtException, MalformedJwtException,
 * SignatureException,ExpiredJwtException</i>) if something goes wrong.<br/>
 * The differ from full version is that it does not provide the ability to generate tokens.
 */
public class JWTUtilSimplified implements Serializable {

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
