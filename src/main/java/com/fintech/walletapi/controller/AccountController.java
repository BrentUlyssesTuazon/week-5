package com.fintech.walletapi.controller;

import com.fintech.walletapi.dto.ApiResponse;
import com.fintech.walletapi.dto.AccountDTO;
import com.fintech.walletapi.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AccountDTO>>> getAllAccounts(
            @RequestParam(value = "status", required = false) String status) {

        List<AccountDTO> accounts;

        // 1. Check if the user provided a status to search for
        if (status != null && !status.isBlank()) {
            accounts = accountService.getAccountsByStatus(status);
        } else {
            accounts = accountService.getAllAccounts();
        }

        // 2. Wrap the result in our standard response
        ApiResponse<List<AccountDTO>> response = new ApiResponse<>(
                200, "Successfully fetched accounts", accounts
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDTO>> getAccountById(@PathVariable("id") Long id) {
        AccountDTO account = accountService.getAccountById(id);

        if (account == null) {
            // Return a formatted 404 error using the 2-parameter constructor
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Account not found with ID: " + id));
        }

        // Return a formatted 200 Success
        return ResponseEntity.ok(new ApiResponse<>(200, "Account found", account));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AccountDTO>> createAccount(@Valid @RequestBody AccountDTO newAccount){
        AccountDTO savedAccount = accountService.createAccount(newAccount);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(201, "Account created successfully", savedAccount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> closeAccount(@PathVariable("id") Long id){
        boolean hasClosed = accountService.closeAccount(id);

        if (!hasClosed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Account to close not found with ID: " + id));
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Account closed successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AccountDTO>> updatedAccount(
            @PathVariable("id") Long id, @RequestBody AccountDTO updatedAccount){

        AccountDTO updatedBalance = accountService.updateAccountBalance(id, updatedAccount);

        if (updatedBalance == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(404, "Account to update not found with ID: " + id));
        }

        return ResponseEntity.ok(new ApiResponse<>(200, "Account updated successfully", updatedBalance));
    }

    @GetMapping("/page")
    public ResponseEntity<ApiResponse<Page<AccountDTO>>> getAccountsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Page<AccountDTO> results = accountService.getPaginatedAccounts(page, size);
        return ResponseEntity.ok(new ApiResponse<>(200, "Page fetched", results));
    }
}
