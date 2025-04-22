package com.payment.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.dto.PaymentIntentDTO;
import com.payment.dto.PaymentRequestDTO;
import com.payment.exception.GlobalExceptionHandler;
import com.payment.service.PaymentService;
import com.stripe.exception.StripeException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment Controller", description = "Operations related to payments and transactions")
public class PaymentController extends GlobalExceptionHandler {

	private final PaymentService paymentService;

	@Autowired
	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	// Endpoint para crear un PaymentIntent (simular un pago)
	@PostMapping("/create")
	@Operation(summary = "Create a new payment intent", description = "Create a new payment intent.")
	public ResponseEntity<PaymentIntentDTO> createPaymentIntent(@Validated @RequestBody PaymentRequestDTO request)
			throws StripeException {
		PaymentIntentDTO paymentIntent = paymentService.createPaymentIntent(request);

		PaymentIntentDTO dto = new PaymentIntentDTO(paymentIntent.getId(), paymentIntent.getAmount(),
				paymentIntent.getCurrency(), paymentIntent.getStatus(), paymentIntent.getClientSecret());

		return ResponseEntity.ok(dto);

	}

	// Endpoint para obtener un PaymentIntent
	@GetMapping("/status/{id}")
	@Operation(summary = "Get a payment by id", description = "Fetches a payment by their ID")
	public ResponseEntity<PaymentIntentDTO> getPaymentStatus(@PathVariable String id) throws StripeException {
		PaymentIntentDTO dto = paymentService.getPaymentStatusById(id);
		return ResponseEntity.ok(dto);

	}

	@GetMapping
	@Operation(summary = "Get all paginated payment", description = "Fetches all paginated payment")
	public ResponseEntity<List<PaymentIntentDTO>> getAllPayments(@RequestParam(defaultValue = "10") int limit,
			@RequestParam(required = false) String startingAfter) throws StripeException {
		List<PaymentIntentDTO> payments = paymentService.getAllPayments(limit, startingAfter);
		return ResponseEntity.ok(payments);
	}

}