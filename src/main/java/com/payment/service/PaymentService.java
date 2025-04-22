package com.payment.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.payment.dto.PaymentIntentDTO;
import com.payment.dto.PaymentRequest;
import com.payment.model.Transaction;
import com.payment.repository.TransactionRepository;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {

	@Value("${STRIPE_API_KEY}")
	private String secretKey;

	@Value("${STRIPE_PUBLISHABLE_KEY}")
	private String publishableKey;

	private final TransactionRepository transactionRepository;

	public PaymentService(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	// Se ejecuta después de construir la instancia
	@PostConstruct
	public void init() {
		Stripe.apiKey = secretKey;
	}

	public PaymentIntent createPaymentIntent(PaymentRequest request) throws Exception {
		PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount((long) request.getAmount() * 100) // Stripe
																													// usa
																													// centavos
				.setCurrency(request.getCurrency()).build();

		PaymentIntent paymentIntent = PaymentIntent.create(params);

		// Guardar la transacción en la base de datos
		Transaction transaction = new Transaction();
		transaction.setPaymentIntentId(paymentIntent.getId());
		transaction.setStatus(paymentIntent.getStatus());
		transaction.setAmount(paymentIntent.getAmount());
		transaction.setCurrency(paymentIntent.getCurrency());
		transaction.setCreatedAt(LocalDateTime.now());

		transactionRepository.save(transaction);

		return paymentIntent;
	}
	
	public PaymentIntentDTO getPaymentStatusById(String id) throws Exception {
	    PaymentIntent paymentIntent = PaymentIntent.retrieve(id);

	    return new PaymentIntentDTO(
	        paymentIntent.getId(),
	        paymentIntent.getAmount(),
	        paymentIntent.getCurrency(),
	        paymentIntent.getStatus()
	    );
	}


}
