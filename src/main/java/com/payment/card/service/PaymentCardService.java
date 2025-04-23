package com.payment.card.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.payment.card.dto.PaymentConfirmCardDetailsDTO;
import com.payment.card.dto.TestCardTypeDTO;
import com.payment.card.enums.TestCardTypeEnum;
import com.payment.dto.PaymentIntentDTO;
import com.payment.enums.ConfirmablePaymentStatusEnum;
import com.payment.model.Payment;
import com.payment.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;

import jakarta.annotation.PostConstruct;

@Service
public class PaymentCardService {

	@Value("${STRIPE_API_KEY}")
	private String secretKey;

	@Value("${STRIPE_PUBLISHABLE_KEY}")
	private String publishableKey;

	private final PaymentRepository paymentRepository;

	public PaymentCardService(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	// Se ejecuta después de construir la instancia
	@PostConstruct
	public void init() {
		Stripe.apiKey = secretKey;
	}

	// Retorna la lista completa de tarjetas de prueba
	public List<TestCardTypeDTO> getAllTestCards() throws StripeException {
		return Arrays
				.stream(TestCardTypeEnum.values()).map(tc -> new TestCardTypeDTO(tc.getBrand(), tc.getNumber(),
						tc.getCvc(), tc.getExpMonth(), tc.getExpYear(), tc.getDescription(), tc.getPaymentMethodId()))
				.collect(Collectors.toList());
	}

	public PaymentIntentDTO confirmPaymentIntentWithTestCard(String paymentIntentId) throws StripeException {
		try {

			// Confirmar el PaymentIntent con un PaymentMethod de prueba
			PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
					.setPaymentMethod("pm_card_visa") // Método de prueba, ya que los datos de las terjetas no pasan por
														// el server
					.build();

			// 2. Confirmar el PaymentIntent con ese paymentMethod
			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

			String status = paymentIntent.getStatus();

			if (!ConfirmablePaymentStatusEnum.isConfirmable(status)) {
				throw new IllegalArgumentException("Cannot confirm payment with status: " + status);
			}

			paymentIntent = paymentIntent.confirm(confirmParams);

			// Intentar obtener el pago de la base de datos
			Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId).orElse(null);

			// Si el pago no se encuentra en la base de datos, manejamos la situación sin
			// lanzar una excepción
			if (payment == null) {
				System.out.println("Payment not found with ID: " + paymentIntentId);

				return new PaymentIntentDTO(paymentIntent.getId(), paymentIntent.getAmount(),
						paymentIntent.getCurrency(), paymentIntent.getStatus(), paymentIntent.getClientSecret());
			}

			// Si se encuentra el pago, actualizamos el estado
			payment.setStatus(paymentIntent.getStatus());
			payment.setUpdatedAt(LocalDateTime.now());

			paymentRepository.save(payment);

			return new PaymentIntentDTO(paymentIntent.getId(), paymentIntent.getAmount(), paymentIntent.getCurrency(),
					paymentIntent.getStatus(), paymentIntent.getClientSecret());
		} catch (StripeException e) {
			throw e;
		}
	}

	public PaymentIntentDTO confirmPaymentIntentWithCardDetails(String paymentIntentId,
			PaymentConfirmCardDetailsDTO paymentConfirmCardDetailsDTO) throws StripeException {
		try {

			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

			if (paymentIntent == null || paymentIntent.getId() == null) {
				throw new IllegalArgumentException("PaymentIntent not found with ID: " + paymentIntentId);
			}

			String status = paymentIntent.getStatus();

			if (!ConfirmablePaymentStatusEnum.isConfirmable(status)) {
				throw new IllegalArgumentException("Cannot confirm payment with status: " + status);
			}

			String cardNumber = paymentConfirmCardDetailsDTO.getCardDetails().getCardNumber();

			Optional<TestCardTypeEnum> testCard = TestCardTypeEnum.fromCardNumber(cardNumber);
			if (testCard.isEmpty()) {
				throw new IllegalArgumentException("Unsupported test card number: " + cardNumber);
			}

			// Usamos el paymentMethodId de prueba de Stripe
			String testPaymentMethodId = testCard.get().getPaymentMethodId();

			PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
					.setPaymentMethod(testPaymentMethodId).build();

			PaymentIntent updatedPaymentIntent = paymentIntent.confirm(confirmParams);

			Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId).orElse(null);

			if (payment != null) {
				payment.setStatus(updatedPaymentIntent.getStatus());
				payment.setUpdatedAt(LocalDateTime.now());
				paymentRepository.save(payment);
			}

			return new PaymentIntentDTO(updatedPaymentIntent.getId(), updatedPaymentIntent.getAmount(),
					updatedPaymentIntent.getCurrency(), updatedPaymentIntent.getStatus(),
					updatedPaymentIntent.getClientSecret());
		} catch (StripeException e) {
			throw e;
		}
	}

}
