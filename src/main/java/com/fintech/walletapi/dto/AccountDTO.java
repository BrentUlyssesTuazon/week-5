package com.fintech.walletapi.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AccountDTO {
    private Long id;
    private Long customerId;
    @NotBlank(message = "Account type is required")
    private String accountType;
    private String customerName;
    @NotNull
    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal balance;
    private String status;

    public AccountDTO() {
    }

    public AccountDTO(Long id, String customerName, BigDecimal balance, String status) {
        this.id = id;
        this.customerName = customerName;
        this.balance = balance;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
}
