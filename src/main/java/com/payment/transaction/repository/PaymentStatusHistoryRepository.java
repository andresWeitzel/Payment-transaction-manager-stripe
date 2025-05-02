package com.payment.transaction.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.transaction.model.Payment;
import com.payment.transaction.model.PaymentStatusHistory;

public interface PaymentStatusHistoryRepository extends JpaRepository<PaymentStatusHistory, Long> {
    List<PaymentStatusHistory> findByPaymentOrderByCreatedAtDesc(Payment payment);
} 