package com.payment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	// Método para encontrar el pago por PaymentIntentId
	Optional<Payment> findByPaymentIntentId(String paymentIntentId);

}
