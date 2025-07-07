package com.hcc.controllers;

import com.hcc.entities.User;
import com.hcc.services.UserService;
import com.hcc.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService; // Replaced UserRepository with UserService

    // Login endpoint to authenticate and generate a JWT token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        try {
            // Authenticate the user with provided credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
        } catch (Exception e) {
            // Return 401 Unauthorized for invalid credentials
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }

        // Use UserService to retrieve the user
        User user = userService.findByUsername(username); // Replaced UserRepository call with UserService
        String token = jwtUtil.generateToken(user);

        // Return the token in the response
        return ResponseEntity.ok(Map.of("token", token));
    }

    // Endpoint to validate a JWT token
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        String jwt = token.replace("Bearer ", "");
        boolean isValid = jwtUtil.validateToken(jwt, null); // UserDetails validation can also be added if required.
        return ResponseEntity.ok(Map.of("isValid", isValid)); // Return the token's validity
    }
}