package com.bots.lviv_croissant_bot.service.messenger.impl;

import com.bots.lviv_croissant_bot.service.messenger.StripeService;
import org.springframework.beans.factory.annotation.Value;

public class StripeServiceImpl implements StripeService {
    @Value("${StripeService.apiKey}")
    private String STRIPE_API_KEY;
    @Override
    public void stripe() {
        com.stripe.Stripe.apiKey = STRIPE_API_KEY;
        
    }
}
