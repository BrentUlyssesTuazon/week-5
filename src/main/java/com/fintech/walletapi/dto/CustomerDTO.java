package com.fintech.walletapi.dto;

import jakarta.validation.constraints.NotBlank;

public class CustomerDTO {
    private Long id;

    @NotBlank(message = "Customer name is required")
    private String fullName;

    // Added fields for Week 5
    private String email;
    private String phone;
    private String username; // From the linked User: login name, not the whole User object

    // Default constructor (required by Jackson/frameworks)
    public CustomerDTO() {
    }

    // Existing 2-argument constructor (still used by CustomerService)
    public CustomerDTO(Long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

    // New 5-argument constructor (used by CustomerController's /me endpoint)
    public CustomerDTO(Long id, String fullName, String email, String phone, String username) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.username = username;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}