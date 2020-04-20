package org.kanootoko.authserver.controllers;

import org.json.JSONObject;
import org.kanootoko.authserver.exceptions.UserNotFoundException;
import org.kanootoko.authserver.models.AuthRequest;
import org.kanootoko.authserver.models.entities.User;
import org.kanootoko.authserver.services.UserService;
import org.kanootoko.authserver.utils.JWTUtil;
import org.kanootoko.authserver.utils.ServiceFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class LoginController {
    @PostMapping("/auth")
    public ResponseEntity<String> authenticate(@RequestBody AuthRequest loginAndPassword, Model model) {
        UserService userService = ServiceFactory.getUserService();
        try {
            User user = userService.getUserByLogin(loginAndPassword.getLogin());
            if (user.passwordOk(loginAndPassword.getPassword())) {
                String token = JWTUtil.generateToken(user);
                return ResponseEntity.ok(new JSONObject().put("message", "Authenticated").put("token", token).toString());
            } else {
                return ResponseEntity.ok(new JSONObject().put("message", "User not found or password is wrong").toString());
            }
        } catch(UserNotFoundException ex) {
            System.err.println("User not found: " + loginAndPassword.getLogin());
            return ResponseEntity.ok(new JSONObject().put("message", "User not found or password is wrong").toString());
        }
    }
}
