package com.fintech.walletapi;

import org.springframework.stereotype.Service;

@Service
public class StandardAccountService implements AccountGreetingProvider {
    @Override
    public String getGreeting() {
        return "Welcome to your Standard Checking Account. No minimum balance required.";
    }
}
