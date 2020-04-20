package org.kanootoko.jwtchecker.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DefaultController {

    @GetMapping("/anyone")
    public ResponseEntity<String> helloAnyone() {
        return ResponseEntity.ok("{\"message\":\"hello, anyone\"}");
    }

    @GetMapping("/anyUser")
    @PreAuthorize("!isAnonymous()")
    public ResponseEntity<String> helloAnyUser() {
        return ResponseEntity.ok("{\"message\":\"hello, any user\"}");
    }

    @GetMapping("/user")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<String> helloUser() {
        return ResponseEntity.ok("{\"message\":\"hello\"}");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> helloAdmin() {
        return ResponseEntity.ok("{\"message\":\"hello, admin`\"}");
    }
}