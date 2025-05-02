package com.payment.transaction.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.transaction.dto.PaymentIntentDTO;
import com.payment.transaction.dto.PaymentRequestDTO;
import com.payment.transaction.exception.GlobalExceptionHandler;
import com.payment.transaction.model.Payment;
import com.payment.transaction.service.PaymentService;
import com.payment.transaction.service.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentIntentCollection;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment Controller", description = "Endpoints for payment management")
public class PaymentController extends GlobalExceptionHandler {

	private final PaymentService paymentService;
	private final StripeService stripeService;

	@Autowired
	public PaymentController(PaymentService paymentService, StripeService stripeService) {
		this.paymentService = paymentService;
		this.stripeService = stripeService;
	}

	// Endpoint para crear un PaymentIntent (simular un pago)
	@PostMapping("/create")
	@Operation(summary = "Create a new payment", description = "Creates a new payment in both Stripe and local database")
	public ResponseEntity<PaymentIntentDTO> createPayment(@RequestBody PaymentRequestDTO request) throws StripeException {
		PaymentIntentDTO createdPayment = paymentService.createPaymentIntent(request);
		return ResponseEntity.ok(createdPayment);
	}

	// Endpoint para actualizar un PaymentIntent
	@PutMapping("/update/{paymentIntentId}")
	@Operation(summary = "Update payment intent", description = "Update the amount of a payment intent.")
	public ResponseEntity<PaymentIntentDTO> updatePaymentIntent(@PathVariable String paymentIntentId,
			@RequestBody PaymentRequestDTO request) throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentService.updatePaymentIntent(paymentIntentId, request);
		return ResponseEntity.ok(paymentIntentDTO);

	}

	@PatchMapping("/capture/{paymentIntentId}")
	@Operation(summary = "Capture a PaymentIntent", description = "Captures a previously authorized PaymentIntent.")
	public ResponseEntity<PaymentIntentDTO> capturePayment(@PathVariable String paymentIntentId)
			throws StripeException {
		PaymentIntentDTO paymentIntentDTO = paymentService.capturePaymentIntent(paymentIntentId);
		return ResponseEntity.ok(paymentIntentDTO);
	}

	// Endpoint para cancelar un PaymentIntent
	@PostMapping("/cancel/{paymentIntentId}")
	@Operation(summary = "Cancel payment intent", description = "Cancel an uncompleted payment intent.")
	public ResponseEntity<String> cancelPaymentIntent(@PathVariable String paymentIntentId) throws StripeException {
		paymentService.cancelPaymentIntent(paymentIntentId);
		return ResponseEntity.ok("PaymentIntent cancelled successfully.");
	}

	// Endpoint para obtener todos los PaymentIntent
	@GetMapping("/list")
	@Operation(summary = "Get all paginated payment", description = "Fetches all paginated payment")
	public ResponseEntity<List<PaymentIntentDTO>> getAllPayments(@RequestParam(defaultValue = "30") int limit,
			@RequestParam(required = false) String startingAfter) throws StripeException {
		List<PaymentIntentDTO> paymentIntentDTOList = paymentService.getAllPayments(limit, startingAfter);
		return ResponseEntity.ok(paymentIntentDTOList);
	}

	
	@GetMapping("/database/list")
	@Operation(summary = "List all payments from database", description = "Retrieves all payments stored in the local database")
	public ResponseEntity<List<Payment>> getAllPaymentsFromDb() {
		List<Payment> payments = paymentService.getAllPaymentsFromDb();
		return ResponseEntity.ok(payments);
	}
	
	// Endpoint para obtener un PaymentIntent
	@GetMapping("/{paymentIntentId}")
	@Operation(summary = "Get payment by ID", description = "Retrieves a payment by its ID from both Stripe and local database")
	public ResponseEntity<PaymentIntentDTO> getPayment(@PathVariable String paymentIntentId) throws StripeException {
		PaymentIntentDTO payment = paymentService.getPayment(paymentIntentId);
		return ResponseEntity.ok(payment);
	}


	



	@PostMapping("/sync")
	@Operation(summary = "Sync payments from Stripe to database", description = "Finds payments in Stripe that are not in our database and adds them")
	public ResponseEntity<String> syncPayments() throws StripeException {
		// Get all payments from Stripe
		PaymentIntentCollection paymentIntents = PaymentIntent.list(Map.of("limit", 100));
		List<PaymentIntentDTO> stripePayments = paymentIntents.getData().stream()
			.map(stripeService::convertToDTO)
			.toList();

		// Get all payments from database
		List<Payment> dbPayments = paymentService.getAllPaymentsFromDb();
		List<String> dbPaymentIds = dbPayments.stream()
			.map(Payment::getPaymentIntentId)
			.toList();

		// Find payments that exist in Stripe but not in our DB
		List<PaymentIntentDTO> missingPayments = stripePayments.stream()
			.filter(payment -> !dbPaymentIds.contains(payment.getId()))
			.toList();

		// Save missing payments to database
		for (PaymentIntentDTO payment : missingPayments) {
			Payment newPayment = new Payment();
			newPayment.setPaymentIntentId(payment.getId());
			newPayment.setAmount((long) payment.getAmount());
			newPayment.setCurrency(payment.getCurrency());
			newPayment.setStatus(payment.getStatus());
			paymentService.savePayment(newPayment);
		}

		return ResponseEntity.ok("Synced " + missingPayments.size() + " payments from Stripe to database");
	}

}