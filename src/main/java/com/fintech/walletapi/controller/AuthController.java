package com.fintech.walletapi.controller;

import com.fintech.walletapi.dto.ApiResponse;
import com.fintech.walletapi.dto.LoginDTO;
import com.fintech.walletapi.dto.RegisterDTO;
import com.fintech.walletapi.entity.Customer;
import com.fintech.walletapi.entity.User;
import com.fintech.walletapi.repository.UserRepository;
import com.fintech.walletapi.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterDTO request) {
        // Guard: reject duplicate usernames
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(409, "Username already exists"));
        }

        // 1. Create User (credentials)
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // CRITICAL: Hash!
        user.setRole("ROLE_CUSTOMER"); // Default role for self-registration

        // 2. Create Customer (bank profile) and link it to the User
        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setUser(user); // set the back-reference
        user.setCustomer(customer); // set the owning side

        userRepository.save(user); // CascadeType.ALL saves Customer automatically

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "User registered successfully"));
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> registerAdmin(@Valid @RequestBody RegisterDTO request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse<>(409, "Username already exists"));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_ADMIN");

        Customer customer = new Customer();
        customer.setFullName(request.getFullName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setUser(user);
        user.setCustomer(customer);

        userRepository.save(user);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Admin registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginDTO request) {
        // 1. Verify credentials — throws BadCredentialsException if wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 2. Credentials are valid — generate the JWT wristband
        String token = jwtUtil.generateToken(request.getUsername());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ApiResponse<>(200, "Login successful", token));
    }
}