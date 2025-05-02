package com.payment.transaction.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.transaction.dto.AutomatedPaymentRequestDTO;
import com.payment.transaction.dto.PaymentIntentDTO;
import com.payment.transaction.exception.GlobalExceptionHandler;
import com.payment.transaction.exception.PaymentProcessingException;
import com.payment.transaction.service.AutomatedPaymentService;
import com.stripe.exception.StripeException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments/automated")
@Tag(name = "Automated Payment Controller", description = "Endpoints for automated payment processing")
@RequiredArgsConstructor
public class AutomatedPaymentController extends GlobalExceptionHandler {

    private final AutomatedPaymentService automatedPaymentService;

    @PostMapping("/process")
    @Operation(summary = "Process a payment automatically", 
              description = "Creates a payment intent, confirms it with card details, and optionally captures it in a single step")
    public ResponseEntity<PaymentIntentDTO> processPayment(
            @Validated @RequestBody AutomatedPaymentRequestDTO request) throws StripeException {
        try {
            PaymentIntentDTO result = automatedPaymentService.processPayment(request);
            return ResponseEntity.ok(result);
        } catch (PaymentProcessingException e) {
            // En caso de 3D Secure, devolvemos el PaymentIntent para manejo manual
            return ResponseEntity.ok(e.getPaymentIntent());
        }
    }
} 