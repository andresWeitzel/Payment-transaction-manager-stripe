package com.payment.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.payment.transaction.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	// Método para encontrar el pago por PaymentIntentId
	Optional<Payment> findByPaymentIntentId(String paymentIntentId);

	List<Payment> findByStatusIn(List<String> statuses);

}
