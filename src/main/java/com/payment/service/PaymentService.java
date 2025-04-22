package com.payment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.payment.dto.PaymentIntentDTO;
import com.payment.dto.PaymentRequestDTO;
import com.payment.model.Payment;
import com.payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentListParams;

import jakarta.annotation.PostConstruct;

@Service
public class PaymentService {

	@Value("${STRIPE_API_KEY}")
	private String secretKey;

	@Value("${STRIPE_PUBLISHABLE_KEY}")
	private String publishableKey;

	private final PaymentRepository paymentRepository;

	public PaymentService(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	// Se ejecuta después de construir la instancia
	@PostConstruct
	public void init() {
		Stripe.apiKey = secretKey;
	}

	public PaymentIntentDTO createPaymentIntent(PaymentRequestDTO request) throws StripeException {
		try {

			PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
					.setAmount((long) request.getAmount() * 100) // Stripe
					// usa
					// centavos
					.setCurrency(request.getCurrency()).build();

			PaymentIntent paymentIntent = PaymentIntent.create(params);

			// Guardar la transacción en la base de datos
			Payment transaction = new Payment();
			transaction.setPaymentIntentId(paymentIntent.getId());
			transaction.setStatus(paymentIntent.getStatus());
			transaction.setAmount(paymentIntent.getAmount());
			transaction.setCurrency(paymentIntent.getCurrency());
			transaction.setCreatedAt(LocalDateTime.now());

			paymentRepository.save(transaction);

			return new PaymentIntentDTO(paymentIntent.getId(), paymentIntent.getAmount(), paymentIntent.getCurrency(),
					paymentIntent.getStatus(), paymentIntent.getClientSecret());
		} catch (StripeException e) {
			// Lanza el error original para que sea manejado por el GlobalExceptionHandler
			throw e;
		}
	}

	public PaymentIntentDTO getPaymentStatusById(String id) throws StripeException {
		try {
			PaymentIntent paymentIntent = PaymentIntent.retrieve(id);

			if (paymentIntent == null || paymentIntent.getId() == null) {
				throw new IllegalArgumentException("PaymentIntent not found with ID: " + id);
			}

			return new PaymentIntentDTO(paymentIntent.getId(), paymentIntent.getAmount(), paymentIntent.getCurrency(),
					paymentIntent.getStatus(), paymentIntent.getClientSecret());
		} catch (StripeException e) {
			// Lanza el error original para que sea manejado por el GlobalExceptionHandler
			throw e;
		}
	}

	public List<PaymentIntentDTO> getAllPayments(int limit, String startingAfter) throws StripeException {
		try {
			PaymentIntentListParams.Builder paramsBuilder = PaymentIntentListParams.builder().setLimit((long) limit);

			if (startingAfter != null && !startingAfter.isBlank()) {
				paramsBuilder.setStartingAfter(startingAfter);
			}

			PaymentIntentCollection paymentIntents = PaymentIntent.list(paramsBuilder.build());

			return paymentIntents.getData().stream().map(pi -> new PaymentIntentDTO(pi.getId(), pi.getAmount(),
					pi.getCurrency(), pi.getStatus(), pi.getClientSecret())).toList();
		} catch (StripeException e) {
			// Lanza el error original para que sea manejado por el GlobalExceptionHandler
			throw e;
		}

	}

}
