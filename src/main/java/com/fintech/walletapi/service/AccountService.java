package com.fintech.walletapi.service;

import com.fintech.walletapi.dto.AccountDTO;
import com.fintech.walletapi.entity.Account;
import com.fintech.walletapi.entity.Customer;
import com.fintech.walletapi.repository.AccountRepository;
import com.fintech.walletapi.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository, CustomerRepository customerRepository) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    // Step 12.4: Admin can open for anyone; customers can only use their own customerId
    @PreAuthorize("hasRole('ADMIN') or @accountSecurity.isOwnCustomer(#dto.customerId, authentication.name)")
    public AccountDTO createAccount(AccountDTO dto){
        Account entity = new Account();
        entity.setAccountType(dto.getAccountType());
        entity.setBalance(dto.getBalance());
        entity.setStatus(dto.getStatus());

        // Step 12.4c: Throws IllegalArgumentException for a clean 404 if customer not found
        Customer owner = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not Found"));
        entity.setCustomer(owner);

        Account savedEntity = accountRepository.save(entity);

        return convertToDTO(savedEntity);
    }

    // Step 12.3: Admin only
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountDTO> getAllAccounts(){
        return accountRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    // Step 12.2: Row-level security — Admin or owner of the account
    @PostAuthorize("hasRole('ADMIN') or returnObject.username == authentication.name")
    public AccountDTO getAccountById(Long id){
        return accountRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Account not Found"));
    }

    // Step 12.3: Admin only
    @PreAuthorize("hasRole('ADMIN')")
    public List<AccountDTO> getAccountsByStatus(String status){
        return accountRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Step 12.3: Admin only
    @PreAuthorize("hasRole('ADMIN')")
    public boolean closeAccount(Long id){
        if(accountRepository.existsById(id)){
            accountRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Step 12.3: Admin only
    @PreAuthorize("hasRole('ADMIN')")
    public AccountDTO updateAccountBalance(Long id, AccountDTO updatedAccount){
        return accountRepository.findById(id)
                .map(existingAccount -> {
                    existingAccount.setBalance(updatedAccount.getBalance());
                    Account savedAccount = accountRepository.save(existingAccount);
                    return convertToDTO(savedAccount);
                })
                .orElse(null);
    }

    // Step 12.1: Admin only
    @PreAuthorize("hasRole('ADMIN')")
    public Page<AccountDTO> getPaginatedAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return accountRepository.findAll(pageable).map(this::convertToDTO);
    }

    // Step 11: Convert entity to DTO with username populated for @PostAuthorize
    private AccountDTO convertToDTO(Account entity) {
        AccountDTO dto = new AccountDTO();

        dto.setId(entity.getId());
        dto.setAccountType(entity.getAccountType());
        dto.setBalance(entity.getBalance());
        dto.setStatus(entity.getStatus());

        if(entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
            dto.setCustomerName(entity.getCustomer().getFullName());

            // Traverses Customer -> User -> username
            if (entity.getCustomer().getUser() != null) {
                dto.setUsername(entity.getCustomer().getUser().getUsername());
            }
        }
        return dto;
    }

}