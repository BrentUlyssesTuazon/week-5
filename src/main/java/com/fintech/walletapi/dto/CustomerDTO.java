package com.fintech.walletapi.dto;

import jakarta.validation.constraints.NotBlank;

public class CustomerDTO {
    private Long id;
    @NotBlank(message = "Customer name is required")
    private String fullName;

    public CustomerDTO() {
    }

    public CustomerDTO(Long id, String fullName) {
        this.id = id;
        this.fullName = fullName;
    }

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
}
