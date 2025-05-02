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
import com.payment.transaction.dto.PaymentIntentDTO;
import com.payment.transaction.enums.ConfirmablePaymentStatusEnum;
import com.payment.transaction.model.Payment;
import com.payment.transaction.repository.PaymentRepository;
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


	// Endpoint para confirmar un pago con tarjeta visa de prueba preseteada
	public PaymentIntentDTO confirmPaymentIntentWithTestPmCardVisa(String paymentIntentId) throws StripeException {
		return confirmPaymentIntentWithTestCard(paymentIntentId, TestCardTypeEnum.VISA);
	}

	public PaymentIntentDTO confirmPaymentIntentWithTestPmCardVisaDebit(String paymentIntentId) throws StripeException {
		return confirmPaymentIntentWithTestCard(paymentIntentId, TestCardTypeEnum.VISA_DEBIT);
	}

	public PaymentIntentDTO confirmPaymentIntentWithTestPmCardMastercard(String paymentIntentId) throws StripeException {
		return confirmPaymentIntentWithTestCard(paymentIntentId, TestCardTypeEnum.MASTERCARD);
	}

	public PaymentIntentDTO confirmPaymentIntentWithTestPmCardMastercard2(String paymentIntentId) throws StripeException {
		return confirmPaymentIntentWithTestCard(paymentIntentId, TestCardTypeEnum.MASTERCARD_2);
	}

	public PaymentIntentDTO confirmPaymentIntentWithTestPmCardAmex(String paymentIntentId) throws StripeException {
		return confirmPaymentIntentWithTestCard(paymentIntentId, TestCardTypeEnum.AMEX);
	}

	public PaymentIntentDTO confirmPaymentIntentWithTestPmCardDiscover(String paymentIntentId) throws StripeException {
		return confirmPaymentIntentWithTestCard(paymentIntentId, TestCardTypeEnum.DISCOVER);
	}

	public PaymentIntentDTO confirmPaymentIntentWithTestPmCardDiners(String paymentIntentId) throws StripeException {
		return confirmPaymentIntentWithTestCard(paymentIntentId, TestCardTypeEnum.DINERS);
	}

	public PaymentIntentDTO confirmPaymentIntentWithTestPmCardJcb(String paymentIntentId) throws StripeException {
		return confirmPaymentIntentWithTestCard(paymentIntentId, TestCardTypeEnum.JCB);
	}

	public PaymentIntentDTO confirmPaymentIntentWithTestPmCardUnionpay(String paymentIntentId) throws StripeException {
		return confirmPaymentIntentWithTestCard(paymentIntentId, TestCardTypeEnum.UNIONPAY);
	}

	private PaymentIntentDTO confirmPaymentIntentWithTestCard(String paymentIntentId, TestCardTypeEnum cardType) throws StripeException {
		try {
			PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
					.setPaymentMethod(cardType.getPaymentMethodId())
					.build();

			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

			String status = paymentIntent.getStatus();

			if (!ConfirmablePaymentStatusEnum.isConfirmable(status)) {
				throw new IllegalArgumentException("Cannot confirm payment with status: " + status);
			}

			paymentIntent = paymentIntent.confirm(confirmParams);

			Payment payment = paymentRepository.findByPaymentIntentId(paymentIntentId).orElse(null);

			if (payment == null) {
				System.out.println("Payment not found with ID: " + paymentIntentId);
				return new PaymentIntentDTO(paymentIntent.getId(), paymentIntent.getAmount(),
						paymentIntent.getCurrency(), paymentIntent.getStatus(), paymentIntent.getClientSecret());
			}

			payment.setStatus(paymentIntent.getStatus());
			payment.setUpdatedAt(LocalDateTime.now());
			paymentRepository.save(payment);

			return new PaymentIntentDTO(paymentIntent.getId(), paymentIntent.getAmount(), paymentIntent.getCurrency(),
					paymentIntent.getStatus(), paymentIntent.getClientSecret());
		} catch (StripeException e) {
			throw e;
		}
	}

	// Endpoint para confirmar un pago con tarjeta
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
