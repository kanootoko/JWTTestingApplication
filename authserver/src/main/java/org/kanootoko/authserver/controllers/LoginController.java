package org.kanootoko.authserver.controllers;

import org.json.JSONObject;
import org.kanootoko.authserver.exceptions.TokenLogicException;
import org.kanootoko.authserver.exceptions.UserNotFoundException;
import org.kanootoko.authserver.models.AuthRequest;
import org.kanootoko.authserver.models.Tokens;
import org.kanootoko.authserver.models.entities.User;
import org.kanootoko.authserver.services.UserService;
import org.kanootoko.authserver.utils.JWTUtil;
import org.kanootoko.authserver.utils.RefreshTokensStorageFile;
import org.kanootoko.authserver.utils.ServiceFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * LoginController is the only controller on the authorization service
 * currently, it listens on /auth for a json with pair of login-password for
 * authentication, or refreshToken for refresh tokens action.
 */
@RestController
public class LoginController {

    private static final JWTUtil jwtUtil = new JWTUtil(new RefreshTokensStorageFile("tokens.txt"));

    @PostMapping("/auth")
    public ResponseEntity<String> authenticate(@RequestBody AuthRequest loginAndPasswordOrToken, Model model) {
        if (loginAndPasswordOrToken.getRefreshToken() != null) {
            try {
                Tokens tokens = jwtUtil.refreshTokens(loginAndPasswordOrToken.getRefreshToken());
                return ResponseEntity.ok(new JSONObject().put("accessToken", tokens.accessToken)
                        .put("refreshToken", tokens.refreshToken).toString());
            } catch (TokenLogicException e) {
                return ResponseEntity.ok(new JSONObject().put("message", e.getMessage()).toString());
            }
        } else {
            try {
                UserService userService = ServiceFactory.getUserService();
                User user = userService.getUserByLogin(loginAndPasswordOrToken.getLogin());
                if (user.passwordOk(loginAndPasswordOrToken.getPassword())) {
                    Tokens tokens = jwtUtil.generateTokens(user);
                    return ResponseEntity
                            .ok(new JSONObject().put("message", "Authenticated").put("accessToken", tokens.accessToken)
                                    .put("refreshToken", tokens.refreshToken).toString());
                } else {
                    return ResponseEntity
                            .ok(new JSONObject().put("message", "User not found or password is wrong").toString());
                }
            } catch (UserNotFoundException ex) {
                System.err.println("User not found: " + loginAndPasswordOrToken.getLogin());
                return ResponseEntity
                        .ok(new JSONObject().put("message", "User not found or password is wrong").toString());
            }
        }
    }
}
