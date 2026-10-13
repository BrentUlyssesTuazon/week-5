package com.fintech.walletapi.service;

import com.fintech.walletapi.dto.CustomerDTO;
import com.fintech.walletapi.entity.Customer;
import com.fintech.walletapi.repository.CustomerRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // <-- Added here: blocks regular customers from creating detached profiles (they must use /api/v1/auth/register)
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer entity = new Customer();
        entity.setFullName(customerDTO.getFullName());

        Customer savedEntity = customerRepository.save(entity);

        return new CustomerDTO(savedEntity.getId(), savedEntity.getFullName());
    }
}