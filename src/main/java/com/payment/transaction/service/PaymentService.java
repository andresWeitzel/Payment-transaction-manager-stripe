package com.payment.transaction.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.payment.transaction.dto.PaymentIntentDTO;
import com.payment.transaction.dto.PaymentRequestDTO;
import com.payment.transaction.enums.CapturablePaymentStatusEnum;
import com.payment.transaction.enums.PaymentIntentStatusEnum;
import com.payment.transaction.model.Payment;
import com.payment.transaction.repository.PaymentRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentListParams;
import com.stripe.param.PaymentIntentUpdateParams;

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

			// Convertir el monto de USD a centavos (multiplicar por 100)
			// Stripe usa centavos
			long amountInCents = (long) (request.getAmount() * 100);

			// Setear el pago
			PaymentIntentCreateParams params = PaymentIntentCreateParams.builder().setAmount(amountInCents)
					.setCurrency(request.getCurrency())
					.setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
							.setEnabled(true)
							.setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
							.build())
					.build();

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

	public PaymentIntentDTO capturePaymentIntent(String paymentIntentId) throws StripeException {
		try {
			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

			if (paymentIntent == null || paymentIntent.getId() == null) {
				throw new IllegalArgumentException("PaymentIntent not found with ID: " + paymentIntentId);
			}

			String status = paymentIntent.getStatus();

			if (!CapturablePaymentStatusEnum.isCapturable(status)) {
				throw new IllegalArgumentException("Cannot capture payment with status: " + status);
			}

			PaymentIntent capturedIntent = paymentIntent.capture();

			return new PaymentIntentDTO(capturedIntent.getId(), capturedIntent.getAmount(),
					capturedIntent.getCurrency(), capturedIntent.getStatus(), capturedIntent.getClientSecret());
		} catch (StripeException e) {
			throw e;
		}
	}

	public PaymentIntentDTO updatePaymentIntent(String paymentIntentId, PaymentRequestDTO request)
			throws StripeException {
		try {
			// Recuperar el PaymentIntent
			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

			if (paymentIntent == null || paymentIntent.getId() == null) {
				throw new IllegalArgumentException("Payment not found with ID: " + paymentIntentId);
			}
			if (!PaymentIntentStatusEnum.isUpdatable(paymentIntent.getStatus())) {
				throw new IllegalArgumentException(
						"Cannot update amount of payment intent with status: " + paymentIntent.getStatus());
			}

			// Convertir el monto de USD a centavos (multiplicar por 100)
			// Stripe usa centavos
			long amountInCents = (long) (request.getAmount() * 100);

			// Actualizar el pago
			PaymentIntentUpdateParams params = PaymentIntentUpdateParams.builder().setAmount(amountInCents).build();

			PaymentIntent updatedPaymentIntent = paymentIntent.update(params);

			return new PaymentIntentDTO(updatedPaymentIntent.getId(), updatedPaymentIntent.getAmount(),
					updatedPaymentIntent.getCurrency(), updatedPaymentIntent.getStatus(),
					updatedPaymentIntent.getClientSecret());

		} catch (StripeException e) {
			throw e;
		}
	}

	public boolean cancelPaymentIntent(String paymentIntentId) throws StripeException {
		try {
			// Recuperar el PaymentIntent
			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

			if (paymentIntent == null || paymentIntent.getId() == null) {
				throw new IllegalArgumentException("Payment not found with ID: " + paymentIntentId);
			}

			if (!PaymentIntentStatusEnum.isCancellable(paymentIntent.getStatus())) {
				throw new IllegalArgumentException(
						"Cannot cancel payment intent with status: " + paymentIntent.getStatus());
			}

			// Verificar si el PaymentIntent ya fue completado
			if ("succeeded".equals(paymentIntent.getStatus()) || "failed".equals(paymentIntent.getStatus())) {
				throw new IllegalStateException("Cannot cancel a Payment that has already been completed.");
			}

			// Cancelar el PaymentIntent
			paymentIntent.cancel();

			return true; // Indicar que se canceló con éxito

		} catch (StripeException e) {
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

	public PaymentIntentDTO getPaymentStatusById(String paymentIntentId) throws StripeException {
		try {
			PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

			if (paymentIntent == null || paymentIntent.getId() == null) {
				throw new IllegalArgumentException("Payment not found with ID: " + paymentIntentId);
			}

			return new PaymentIntentDTO(paymentIntent.getId(), paymentIntent.getAmount(), paymentIntent.getCurrency(),
					paymentIntent.getStatus(), paymentIntent.getClientSecret());
		} catch (StripeException e) {
			// Lanza el error original para que sea manejado por el GlobalExceptionHandler
			throw e;
		}
	}

}
