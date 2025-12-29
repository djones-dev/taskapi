package dev.danieljones.taskapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import dev.danieljones.taskapi.dto.LoginRequestDto;
import dev.danieljones.taskapi.dto.RegisterRequestDto;
import dev.danieljones.taskapi.dto.AuthResponseDto;
import dev.danieljones.taskapi.model.User;
import dev.danieljones.taskapi.security.JwtTokenProvider;
import dev.danieljones.taskapi.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto request) {
        // Create new user
        User user = userService.registerUser(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getUsername());
        
        // Return token and user info
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new AuthResponseDto(token, user.getUsername(), user.getEmail()));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto request) {
        // Find user by username
        User user = userService.findByUsername(request.getUsername());
        
        // Validate password
        if (!userService.validatePassword(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AuthResponseDto(null, null, null, "Invalid credentials"));
        }
        
        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getUsername());
        
        // Return token and user info
        return ResponseEntity.ok(new AuthResponseDto(token, user.getUsername(), user.getEmail()));
    }
    
    @GetMapping("/verify")
    public ResponseEntity<String> verifyToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Missing or invalid Authorization header");
        }
        
        String token = authHeader.substring(7);
        
        if (jwtTokenProvider.validateToken(token)) {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            return ResponseEntity.ok("Token is valid for user: " + username);
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Invalid or expired token");
    }
}