package org.kanootoko.authserver.utils;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.spec.SecretKeySpec;

import org.kanootoko.authserver.models.entities.User;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

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

@Component
public class JWTUtil implements Serializable {

	private static final long serialVersionUID = 456842083179721L;

	private static final Key secretKey = utils.getSecretKey();
	
	// Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
	// private void saveSecretKey() {
	// 	try {
	// 		System.out.println("Secret key algo: " + secretKey.getAlgorithm());
	// 		Files.write(Paths.get("src/main/resources/secretKey.txt"), secretKey.getEncoded());
	// 	} catch (Exception ex) {
	// 		ex.printStackTrace();
	// 	}
	// }

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;
	

	public static String getLoginFromToken(String token) {
		return getClaimFromToken(token, Claims::getId);
	}

	public static String getAuthorityFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public static Date getIssuedAtDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getIssuedAt);
	}

	public static Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public static <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private static Claims getAllClaimsFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
	}

	private static Boolean isTokenExpired(String token) {
		return getExpirationDateFromToken(token).before(new Date());
	}

	public static String generateToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		return Jwts.builder().setClaims(claims).setId(user.getLogin()).setSubject(user.getRole()).setIssuedAt(new Date(System.currentTimeMillis()))
		.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)).signWith(secretKey, SignatureAlgorithm.HS512).compact();
	}

	public static Boolean canTokenBeRefreshed(String token) {
		return !isTokenExpired(token);
	}

	public static Boolean validateToken(String token) {
		return !isTokenExpired(token);
	}
}
