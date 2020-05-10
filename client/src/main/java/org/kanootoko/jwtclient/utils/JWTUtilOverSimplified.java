package org.kanootoko.jwtclient.utils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Date;

import org.json.JSONObject;

/**
 * JWTUtilOverSimplified is a class which offers the ability to read from JSON
 * Web Tokens in a simple way. It does not need the secret key and does not
 * check the validity of the token.<br/>
 */
public class JWTUtilOverSimplified implements Serializable {

	private static final long serialVersionUID = 456842083591L;

	/**
	 * getAllClaimsFromToken is a method which parses token and return all the
	 * insides of JWT as JSON object.
	 * 
	 * @param token - JSON Web Token as String
	 * @return all the values inside given JWT as JSON
	 */
	public static JSONObject getAllClaimsFromToken(String token) {
		JSONObject res = null;
		try {
			res = new JSONObject(new String(
					Base64.getDecoder().decode(token.substring(token.indexOf('.') + 1, token.lastIndexOf('.'))),
					"UTF-8"));
		} catch (UnsupportedEncodingException e) {
		}
		return res;
	}

	/**
	 * getClaimFromToken is a method which gets one claim from token by its name.
	 * 
	 * @param token - JSON Web Token as String
	 * @return needed claim from JWT
	 */
	public static Object getClaimFromToken(String token, String claimName) {
		JSONObject jwtInsides = getAllClaimsFromToken(token);
		return jwtInsides.get(claimName);
	}

	/**
	 * getLoginFromToken is a method which returns the login from ID claim of JWT.
	 * 
	 * @param token - JSON Web Token as String
	 * @return user login from JWT
	 */
	public static String getLoginFromToken(String token) {
		return (String) getClaimFromToken(token, "jti");
	}

	/**
	 * getAuthorityFromToken is a method which returns the user role from Subject
	 * claim of JWT.
	 * 
	 * @param token - JSON Web Token as String
	 * @return user role from JWT
	 */
	public static String getAuthorityFromToken(String token) {
		return (String) getClaimFromToken(token, "sub");
	}

	/**
	 * getIssuedAtDateFromToken is a method which returns the time of creation from
	 * IssuedAt claim of JWT
	 * 
	 * @param token - JSON Web Token as String
	 * @return date of creation of JWT
	 */
	public static Date getIssuedAtDateFromToken(String token) {
		return new Date((Integer) getClaimFromToken(token, "iat") * 1000L);
	}

	/**
	 * getExpirationDateFromToken is a method which returns the time when token will
	 * become expired from Expiration claim of JWT
	 * 
	 * @param token - JSON Web Token as String
	 * @return date of creation of JWT
	 */
	public static Date getExpirationDateFromToken(String token) {
		return new Date((Integer) getClaimFromToken(token, "exp") * 1000L);
	}

	/**
	 * isTokenExpired is a method which returns true if token is expired and false
	 * otherwise.
	 * 
	 * @param token - JSON Web Token as String
	 * @return true if JWT is expired, false otherwise
	 */
	public static boolean isTokenExpired(String token) {
		return getExpirationDateFromToken(token).before(new Date(System.currentTimeMillis() - 3000));
	}
}
