package com.payment.transaction.service;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.payment.transaction.dto.PaymentIntentDTO;
import com.payment.transaction.model.Payment;
import com.payment.transaction.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentStatusPollingService {

    private final PaymentRepository paymentRepository;
    private final PaymentSyncService paymentSyncService;
    private final StripeService stripeService;
    private final PaymentService paymentService;

    // Verifica cada 30 segundos los pagos pendientes
    @Scheduled(fixedRate = 30000)
    public void syncPayments() {
        try {
            // 1. Sincronizar pagos faltantes
            syncMissingPayments();
            
            // 2. Verificar estados de pagos pendientes
            List<Payment> pendingPayments = paymentService.getAllPaymentsFromDb();
            for (Payment payment : pendingPayments) {
                try {
                    PaymentIntent paymentIntent = PaymentIntent.retrieve(payment.getPaymentIntentId());
                    paymentSyncService.syncPaymentStatus(paymentIntent);
                } catch (StripeException e) {
                    log.error("Error syncing payment {}: {}", payment.getPaymentIntentId(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Error in payment sync job: {}", e.getMessage());
        }
    }

    private void syncMissingPayments() throws StripeException {
        // Get all payments from Stripe
        PaymentIntentCollection paymentIntents = PaymentIntent.list(Map.of("limit", 100));
        List<PaymentIntent> stripePayments = paymentIntents.getData();

        // Get all payments from database
        List<Payment> dbPayments = paymentService.getAllPaymentsFromDb();
        List<String> dbPaymentIds = dbPayments.stream()
            .map(Payment::getPaymentIntentId)
            .toList();

        // Find and save missing payments
        for (PaymentIntent stripePayment : stripePayments) {
            if (!dbPaymentIds.contains(stripePayment.getId())) {
                Payment newPayment = new Payment();
                newPayment.setPaymentIntentId(stripePayment.getId());
                newPayment.setAmount(stripePayment.getAmount());
                newPayment.setCurrency(stripePayment.getCurrency());
                newPayment.setStatus(stripePayment.getStatus());
                paymentService.savePayment(newPayment);
                log.info("Synced missing payment: {}", stripePayment.getId());
            }
        }
    }
} 