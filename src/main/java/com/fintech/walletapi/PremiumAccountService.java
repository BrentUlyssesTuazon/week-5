package com.fintech.walletapi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PremiumAccountService implements AccountGreetingProvider {
    @Value("${bank.name}")
    private String bankName;

    @Override
    public String getGreeting() {
        return "Welcome, VIP Customer! Enjoy 2% cashback and zero transfer fees at " + bankName + "!";
    }

    public String getServerHealthStatus(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = localDateTime.format(formatter);
        return "Service is responsive. Current server time: " + formattedDateTime;
    }

}
