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
import com.payment.transaction.dto.PaymentIntentDTO;
import com.payment.transaction.exception.GlobalExceptionHandler;
import com.payment.transaction.service.PaymentSyncService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/payments/card")
@Tag(name = "Payment Card Controller", description = "Endpoints for cards and payment confirmations with Stripe test cards")
public class PaymentCardController extends GlobalExceptionHandler {

	private final PaymentCardService paymentStripecardService;
	private final PaymentSyncService paymentSyncService;

	@Autowired
	public PaymentCardController(PaymentCardService paymentStripecardService, 
			PaymentSyncService paymentSyncService) {
		this.paymentStripecardService = paymentStripecardService;
		this.paymentSyncService = paymentSyncService;
	}

	@PostMapping("/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with card", description = "Confirms the payment using either an existing payment method or card details.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentWithCard(@PathVariable String paymentIntentId,
			@RequestBody PaymentConfirmCardDetailsDTO paymentConfirmCardDetailsDTO) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentStripecardService.confirmPaymentIntentWithCardDetails(paymentIntentId,
				paymentConfirmCardDetailsDTO);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		paymentSyncService.syncPaymentStatus(paymentIntent);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	@GetMapping("/test/list")
	public List<TestCardTypeDTO> getTestCards() throws StripeException {
		return paymentStripecardService.getAllTestCards();
	}

	@PostMapping("/test/visa/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with preset card (pm_card_visa)", description = "Confirm a PaymentIntent manually.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentWithTestPmCardVisa(@PathVariable String paymentIntentId)
			throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentStripecardService.confirmPaymentIntentWithTestPmCardVisa(paymentIntentId);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		paymentSyncService.syncPaymentStatus(paymentIntent);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	@PostMapping("/test/visa/debit/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with preset card (pm_card_visa_debit)", description = "Confirm a PaymentIntent manually.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentIntentWithTestPmCardVisaDebit(
			@Parameter(description = "PaymentIntent ID") @PathVariable String paymentIntentId) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentStripecardService.confirmPaymentIntentWithTestPmCardVisaDebit(paymentIntentId);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		paymentSyncService.syncPaymentStatus(paymentIntent);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	@PostMapping("/test/mastercard/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with preset card (pm_card_mastercard)", description = "Confirm a PaymentIntent manually.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentIntentWithTestPmCardMastercard(
			@Parameter(description = "PaymentIntent ID") @PathVariable String paymentIntentId) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentStripecardService.confirmPaymentIntentWithTestPmCardMastercard(paymentIntentId);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		paymentSyncService.syncPaymentStatus(paymentIntent);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	@PostMapping("/test/mastercard/2/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with preset card (pm_card_mastercard 2-series)", description = "Confirm a PaymentIntent manually.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentIntentWithTestPmCardMastercard2(
			@Parameter(description = "PaymentIntent ID") @PathVariable String paymentIntentId) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentStripecardService.confirmPaymentIntentWithTestPmCardMastercard2(paymentIntentId);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		paymentSyncService.syncPaymentStatus(paymentIntent);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	@PostMapping("/test/amex/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with preset card (pm_card_amex)", description = "Confirm a PaymentIntent manually.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentIntentWithTestPmCardAmex(
			@Parameter(description = "PaymentIntent ID") @PathVariable String paymentIntentId) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentStripecardService.confirmPaymentIntentWithTestPmCardAmex(paymentIntentId);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		paymentSyncService.syncPaymentStatus(paymentIntent);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	@PostMapping("/test/discover/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with preset card (pm_card_discover)", description = "Confirm a PaymentIntent manually.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentIntentWithTestPmCardDiscover(
			@Parameter(description = "PaymentIntent ID") @PathVariable String paymentIntentId) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentStripecardService.confirmPaymentIntentWithTestPmCardDiscover(paymentIntentId);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		paymentSyncService.syncPaymentStatus(paymentIntent);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	@PostMapping("/test/diners/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with preset card (pm_card_diners)", description = "Confirm a PaymentIntent manually.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentIntentWithTestPmCardDiners(
			@Parameter(description = "PaymentIntent ID") @PathVariable String paymentIntentId) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentStripecardService.confirmPaymentIntentWithTestPmCardDiners(paymentIntentId);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		paymentSyncService.syncPaymentStatus(paymentIntent);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	@PostMapping("/test/jcb/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with preset card (pm_card_jcb)", description = "Confirm a PaymentIntent manually.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentIntentWithTestPmCardJcb(
			@Parameter(description = "PaymentIntent ID") @PathVariable String paymentIntentId) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentStripecardService.confirmPaymentIntentWithTestPmCardJcb(paymentIntentId);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		paymentSyncService.syncPaymentStatus(paymentIntent);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	@PostMapping("/test/unionpay/confirm/{paymentIntentId}")
	@Operation(summary = "Confirm a PaymentIntent with preset card (pm_card_unionpay)", description = "Confirm a PaymentIntent manually.")
	public ResponseEntity<PaymentIntentDTO> confirmPaymentIntentWithTestPmCardUnionpay(
			@Parameter(description = "PaymentIntent ID") @PathVariable String paymentIntentId) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentStripecardService.confirmPaymentIntentWithTestPmCardUnionpay(paymentIntentId);
		PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
		paymentSyncService.syncPaymentStatus(paymentIntent);
		return ResponseEntity.ok(paymentIntentDTO);
	}
}
