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

    public AccountDTO createAccount(AccountDTO dto){
        Account entity = new Account();
        entity.setAccountType(dto.getAccountType());
        entity.setBalance(dto.getBalance());
        entity.setStatus(dto.getStatus());

        // Fetch the Customer from DB using the ID, and link them!
        Customer owner = customerRepository.findById(dto.getCustomerId()).orElseThrow();
        entity.setCustomer(owner);

        Account savedEntity = accountRepository.save(entity);

        // Map back to DTO using our helper
        return convertToDTO(savedEntity);
    }

    public List<AccountDTO> getAllAccounts(){
        return accountRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public AccountDTO getAccountById(Long id){
        return accountRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Account not Found"));
    }

    public List<AccountDTO> getAccountsByStatus(String status){
        return accountRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean closeAccount(Long id){
        if(accountRepository.existsById(id)){
            accountRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public AccountDTO updateAccountBalance(Long id, AccountDTO updatedAccount){
        return accountRepository.findById(id)
                .map(existingAccount -> {
                    existingAccount.setBalance(updatedAccount.getBalance());
                    Account savedAccount = accountRepository.save(existingAccount);
                    return convertToDTO(savedAccount);
                })
                .orElse(null);
    }

    public Page<AccountDTO> getPaginatedAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return accountRepository.findAll(pageable).map(this::convertToDTO);
    }

    private AccountDTO convertToDTO(Account entity) {
        AccountDTO dto = new AccountDTO();

        dto.setId(entity.getId());
        dto.setAccountType(entity.getAccountType());
        dto.setBalance(entity.getBalance());
        dto.setStatus(entity.getStatus());

        if(entity.getCustomer() != null) {
            dto.setCustomerId(entity.getCustomer().getId());
            dto.setCustomerName(entity.getCustomer().getFullName());
        }
        return dto;
    }

}
