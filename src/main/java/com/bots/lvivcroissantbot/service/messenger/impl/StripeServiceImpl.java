package com.bots.lvivcroissantbot.service.messenger.impl;

import com.bots.lvivcroissantbot.service.messenger.StripeService;
import org.springframework.beans.factory.annotation.Value;

public class StripeServiceImpl implements StripeService {
    @Value("${StripeService.apiKey}")
    private String STRIPE_API_KEY;

    @Override
    public void stripe() {
        com.stripe.Stripe.apiKey = STRIPE_API_KEY;

    }
}
