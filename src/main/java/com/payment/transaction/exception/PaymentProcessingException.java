package com.payment.transaction.exception;

import com.payment.transaction.dto.PaymentIntentDTO;
import lombok.Getter;

@Getter
public class PaymentProcessingException extends RuntimeException {
    private final PaymentIntentDTO paymentIntent;
    
    public PaymentProcessingException(String message, PaymentIntentDTO paymentIntent) {
        super(message);
        this.paymentIntent = paymentIntent;
    }
} 