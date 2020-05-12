package org.kanootoko.jwtchecker.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class StringBody {
    public String string;
}

@RestController
public class DefaultController {

    List<String> listOfStrings = new ArrayList<>();

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

    @GetMapping("/nameme")
    public ResponseEntity<String> nameMe(@RequestParam String name) {
        return ResponseEntity.ok("{\"message\":\"hello, " + name + "\"}");
    }

    @PostMapping("/addstring")
    public ResponseEntity<String> addString(@RequestBody StringBody string) {
        if (string == null) {
            return ResponseEntity.badRequest().body("{\"message\":\"Failed to add string\"}");
        }
        listOfStrings.add(string.string);
        return ResponseEntity.ok("{\"message\":\"Added successfully\"}");
    }

    @GetMapping("list")
    public ResponseEntity<String> list() {
        return ResponseEntity.ok("{\"message\":\"" + listOfStrings.toString() + "\"}");
    }
}