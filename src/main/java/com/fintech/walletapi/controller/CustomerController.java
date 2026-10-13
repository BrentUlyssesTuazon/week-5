package com.fintech.walletapi.controller;

import com.fintech.walletapi.dto.ApiResponse;
import com.fintech.walletapi.dto.CustomerDTO;
import com.fintech.walletapi.entity.Customer;
import com.fintech.walletapi.entity.User;
import com.fintech.walletapi.repository.UserRepository;
import com.fintech.walletapi.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final UserRepository userRepository;

    @Autowired
    public CustomerController(CustomerService customerService, UserRepository userRepository) {
        this.customerService = customerService;
        this.userRepository = userRepository;
    }

    // Existing POST endpoint to create a customer
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

    // Week 5 "Who Am I?" Endpoint
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<CustomerDTO>> getMyProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(401, "Not authenticated"));
        }

        User user = userRepository.findByUsername(principal.getName()).orElseThrow();
        Customer customer = user.getCustomer();

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Customer profile not found"));
        }

        CustomerDTO dto = new CustomerDTO(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                user.getUsername()
        );

        return ResponseEntity.ok(new ApiResponse<>(200, "Success", dto));
    }
}