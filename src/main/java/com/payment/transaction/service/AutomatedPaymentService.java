package com.payment.transaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.payment.card.dto.CardDetailsDTO;
import com.payment.card.dto.PaymentConfirmCardDetailsDTO;
import com.payment.card.service.PaymentCardService;
import com.payment.transaction.dto.AutomatedPaymentRequestDTO;
import com.payment.transaction.dto.PaymentIntentDTO;
import com.payment.transaction.enums.CapturablePaymentStatusEnum;
import com.payment.transaction.exception.PaymentProcessingException;
import com.stripe.exception.StripeException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutomatedPaymentService {
    
    private final PaymentService paymentService;
    private final PaymentCardService stripeCardService;
    
    @Transactional
    public PaymentIntentDTO processPayment(AutomatedPaymentRequestDTO request) throws StripeException {
        try {
            log.info("Starting automated payment process for amount: {}", request.getPaymentRequest().getAmount());
            
            // 1. Crear PaymentIntent
            PaymentIntentDTO paymentIntent = paymentService.createPaymentIntent(request.getPaymentRequest());
            log.info("PaymentIntent created with ID: {}", paymentIntent.getId());
            
            // 2. Preparar detalles de confirmación
            PaymentConfirmCardDetailsDTO confirmDetails = new PaymentConfirmCardDetailsDTO();
            confirmDetails.setCardDetails(request.getCardDetails());
            
            // 3. Confirmar el pago
            PaymentIntentDTO confirmedPayment = stripeCardService.confirmPaymentIntentWithCardDetails(
                paymentIntent.getId(), 
                confirmDetails
            );
            log.info("Payment confirmed with status: {}", confirmedPayment.getStatus());
            
            // 4. Manejar 3D Secure si es necesario
            if ("requires_action".equals(confirmedPayment.getStatus())) {
                log.warn("3D Secure authentication required. Manual intervention needed.");
                throw new PaymentProcessingException("3D Secure authentication required", confirmedPayment);
            }
            
            // 5. Captura automática si está habilitada y el estado lo permite
            if (request.isAutoCapture() && CapturablePaymentStatusEnum.isCapturable(confirmedPayment.getStatus())) {
                log.info("Auto-capturing payment");
                confirmedPayment = paymentService.capturePaymentIntent(confirmedPayment.getId());
                log.info("Payment captured successfully");
            }
            
            return confirmedPayment;
            
        } catch (StripeException e) {
            log.error("Error processing payment: {}", e.getMessage(), e);
            throw e;
        }
    }
} 