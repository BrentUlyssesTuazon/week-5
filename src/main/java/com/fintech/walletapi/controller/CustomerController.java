package com.fintech.walletapi.controller;

import com.fintech.walletapi.ApiResponse;
import com.fintech.walletapi.dto.CustomerDTO;
import com.fintech.walletapi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // POST endpoint to create a customer
    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDTO>> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO savedCustomer = customerService.createCustomer(customerDTO);

        ApiResponse<CustomerDTO> response = new ApiResponse<>(
                201,
                "Customer created successfully",
                savedCustomer
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
