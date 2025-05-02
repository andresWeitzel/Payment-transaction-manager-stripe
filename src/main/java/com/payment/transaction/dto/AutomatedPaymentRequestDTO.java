package com.payment.transaction.dto;

import com.payment.card.dto.CardDetailsDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AutomatedPaymentRequestDTO {
    @Valid
    @NotNull(message = "Payment details are required")
    private PaymentRequestDTO paymentRequest;
    
    @Valid
    @NotNull(message = "Card details are required")
    private CardDetailsDTO cardDetails;
    
    private boolean autoCapture = true; // Por defecto, capturamos automáticamente
} 