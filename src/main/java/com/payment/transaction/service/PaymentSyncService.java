package com.payment.transaction.service;

import org.springframework.stereotype.Service;

import com.payment.transaction.dto.PaymentIntentDTO;
import com.payment.transaction.model.Payment;
import com.payment.transaction.model.PaymentStatusHistory;
import com.payment.transaction.repository.PaymentRepository;
import com.payment.transaction.repository.PaymentStatusHistoryRepository;
import com.stripe.model.PaymentIntent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentSyncService {

    private final PaymentRepository paymentRepository;
    private final PaymentStatusHistoryRepository statusHistoryRepository;
    private final StripeService stripeService;

    public Payment syncPaymentStatus(PaymentIntent paymentIntent) {
        Payment payment = paymentRepository.findByPaymentIntentId(paymentIntent.getId())
            .orElseGet(() -> {
                Payment newPayment = new Payment();
                newPayment.setPaymentIntentId(paymentIntent.getId());
                return newPayment;
            });

        String oldStatus = payment.getStatus();
        String newStatus = paymentIntent.getStatus();

        if (!newStatus.equals(oldStatus)) {
            payment.setStatus(newStatus);
            payment.setAmount(paymentIntent.getAmount());
            payment.setCurrency(paymentIntent.getCurrency());
            paymentRepository.save(payment);

            // Registrar el cambio de estado
            PaymentStatusHistory history = new PaymentStatusHistory();
            history.setPayment(payment);
            history.setStatus(newStatus);
            history.setStripeEventId(paymentIntent.getId());
            statusHistoryRepository.save(history);

            log.info("Payment status updated: {} -> {}", paymentIntent.getId(), newStatus);
        }

        return payment;
    }

    public void syncPaymentError(String paymentIntentId, String errorMessage) {
        Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId)
            .orElseThrow(() -> new RuntimeException("Payment not found"));

        PaymentStatusHistory history = new PaymentStatusHistory();
        history.setPayment(payment);
        history.setStatus("error");
        history.setErrorMessage(errorMessage);
        statusHistoryRepository.save(history);

        log.error("Payment error recorded: {} - {}", paymentIntentId, errorMessage);
    }
} 