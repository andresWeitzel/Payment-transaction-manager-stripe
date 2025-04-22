package com.payment.exception;

import com.stripe.exception.StripeException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;

public abstract class GlobalExceptionHandler {

	// Manejar excepciones específicas de Stripe
	@ExceptionHandler(StripeException.class)
	public ResponseEntity<String> handleStripeException(StripeException ex) {
		if ("resource_missing".equals(ex.getCode())) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stripe resource not found.");
		} else if ("invalid_request_error".equals(ex.getCode())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Stripe request: " + ex.getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Stripe error: " + ex.getMessage());
	}

	// Manejar excepciones de argumentos inválidos
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid argument: " + ex.getMessage());
	}

	// Manejar excepciones de validaciones con Bean Validation
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<String> handleConstraintViolation(ConstraintViolationException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation failed: " + ex.getMessage());
	}

	// Manejar excepciones de validación de métodos en los controladores
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
		String errorMessage = ex.getBindingResult().getFieldErrors().stream()
				.map(field -> field.getField() + ": " + field.getDefaultMessage()).findFirst()
				.orElse("Validation failed");
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + errorMessage);
	}

	// Manejar excepciones de formato incorrecto en el cuerpo de la solicitud
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleNotReadable(HttpMessageNotReadableException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Malformed request body or missing required fields.");
	}

	// Manejar cualquier otra excepción inesperada
	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleGeneric(Exception ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + ex.getMessage());
	}
}
