package com.payment.transaction.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.payment.transaction.dto.PaymentIntentDTO;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;

import jakarta.annotation.PostConstruct;

@Service
public class StripeService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public PaymentIntentDTO convertToDTO(PaymentIntent paymentIntent) {
        PaymentIntentDTO dto = new PaymentIntentDTO();
        dto.setId(paymentIntent.getId());
        dto.setStatus(paymentIntent.getStatus());
        dto.setAmount(paymentIntent.getAmount());
        dto.setCurrency(paymentIntent.getCurrency());
        return dto;
    }
} 