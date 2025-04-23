package com.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
	@NotNull(message = "Amount is required")
	@Min(value = 1, message = "Amount must be greater than 0")
	@Schema(description = "The amount of a payment", example = "100")
	private double amount;

	@NotBlank(message = "Currency is required")
	@Schema(description = "The currency of a payment", example = "USD")
	private String currency;
}
