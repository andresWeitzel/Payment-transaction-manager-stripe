package com.payment.card.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.card.dto.PaymentConfirmCardDetailsDTO;
import com.payment.card.dto.TestCardTypeDTO;
import com.payment.card.service.PaymentCardService;
import com.payment.dto.PaymentIntentDTO;
import com.payment.exception.GlobalExceptionHandler;
import com.stripe.exception.StripeException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/payments/card")
@Tag(name = "Payment Card Controller", description = "Endpoints for cards and test card payment confirmations")
public class PaymentCardController extends GlobalExceptionHandler {

	private final PaymentCardService paymentcardService;

	@Autowired
	public PaymentCardController(PaymentCardService paymentcardService) {
		this.paymentcardService = paymentcardService;
	}

	@GetMapping("/test/list")
	public List<TestCardTypeDTO> getTestCards() throws StripeException {
		return paymentcardService.getAllTestCards();
	}

	// Endpoint para confirmar un pago con tarjeta de prueba preseteada
	@PostMapping("/test/visa/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with preset card (pm_card_visa)", description = "Confirm a PaymentIntent manually.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentWithTestCard(@PathVariable String paymentIntentId)
			throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentcardService.confirmPaymentIntentWithTestCard(paymentIntentId);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	// Endpoint para confirmar un pago con tarjeta
	@PostMapping("/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with card", description = "Confirms the payment using either an existing payment method or card details.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentWithCard(@PathVariable String paymentIntentId,
			@RequestBody PaymentConfirmCardDetailsDTO paymentConfirmCardDetailsDTO) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentcardService.confirmPaymentIntentWithCardDetails(paymentIntentId,
				paymentConfirmCardDetailsDTO);
		return ResponseEntity.ok(paymentIntentDTO);
	}

}
