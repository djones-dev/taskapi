package dev.danieljones.taskapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dev.danieljones.taskapi.exception.UserAlreadyExistsException;
import dev.danieljones.taskapi.exception.UserNotFoundException;
import dev.danieljones.taskapi.model.User;
import dev.danieljones.taskapi.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(String username, String email, String password) {
        // Check if user already exists
        if (userRepository.existsByUsername(username)) {
            throw new UserAlreadyExistsException("Username already taken");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        
        // Create new user with hashed password
        User user = new User(username, email, passwordEncoder.encode(password));
        return userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}