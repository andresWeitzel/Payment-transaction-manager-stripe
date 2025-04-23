package com.payment.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CardDetailsDTO {
	@NotBlank(message = "Card number is required")
	@Pattern(regexp = "\\d{13,19}", message = "Card number must be between 13 and 19 digits")
	@Schema(description = "Card number (only use for test environments)", example = "4242424242424242", requiredMode = Schema.RequiredMode.REQUIRED)
	private String cardNumber;
	@NotNull(message = "Expiration month is required")
	@Min(value = 1, message = "Expiration month must be between 1 and 12")
	@Max(value = 12, message = "Expiration month must be between 1 and 12")
	@Schema(description = "Expiration month of the card", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer expMonth;
	@NotNull(message = "Expiration year is required")
	@Min(value = 2025, message = "Expiration year must be in the future")
	@Schema(description = "Expiration year of the card", example = "2026", requiredMode = Schema.RequiredMode.REQUIRED)
	private Integer expYear;
	@NotBlank(message = "CVC is required")
	@Pattern(regexp = "\\d{3,4}", message = "CVC must be 3 or 4 digits")
	@Schema(description = "Card verification code (CVC)", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
	private String cvc;
}
