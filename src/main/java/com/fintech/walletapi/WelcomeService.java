package com.fintech.walletapi;

import org.springframework.stereotype.Service;

@Service
public class WelcomeService {
    public String getWelcomeMessage(){
        return "Welcome to the Digital E-Wallet API!";
    }
}
