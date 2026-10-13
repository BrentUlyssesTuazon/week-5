package com.fintech.walletapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class OnboardingController {
    private final AccountGreetingProvider greetingProvider;
    private final PremiumAccountService premiumAccountService;

    @Autowired
    public OnboardingController(@Qualifier("premiumAccountService") AccountGreetingProvider greetingProvider, PremiumAccountService premiumAccountService) {
        this.greetingProvider = greetingProvider;
        this.premiumAccountService = premiumAccountService;
    }

    @GetMapping("/api/onboarding")
    public String displayWelcome(){
        return greetingProvider.getGreeting();
    }

    @GetMapping("/api/server-time")
    public String getServerTime(){
        return this.premiumAccountService.getServerHealthStatus();
    }

}
