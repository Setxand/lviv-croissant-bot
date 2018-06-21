package com.bots.lvivCroissantBot.service.messangerService.impl;

import com.bots.lvivCroissantBot.service.messangerService.StripeService;
import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;

public class StripeServiceImpl implements StripeService {
    @Value("${Stripe.apiKey}")
    private String STRIPE_API_KEY;
    @Override
    public void stripe() {
        Stripe.apiKey = STRIPE_API_KEY;
        
    }
}
