package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Show Login Page
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // Looks for login.html
    }

    // Show Register Page
    @GetMapping("/register")
    public String showRegisterForm() {
        return "register"; // Looks for register.html
    }

    // Handle Registration Logic
    @PostMapping("/register")
    public String registerUser(@RequestParam String email, @RequestParam String password) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            return "redirect:/register?error";
        }
        
        // Save new user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(passwordEncoder.encode(password)); // Encrypt password
        newUser.setRole("USER");
        
        userRepository.save(newUser);
        
        return "redirect:/login?success"; // Go to login page
    }
}
