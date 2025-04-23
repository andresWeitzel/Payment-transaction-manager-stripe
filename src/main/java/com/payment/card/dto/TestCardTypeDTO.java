package com.payment.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestCardTypeDTO {

	@Schema(example = "Visa", description = "Card brand or network")
	private String brand;

	@Schema(example = "4242424242424242", description = "Test card number")
	private String number;

	@Schema(example = "123", description = "Card security code (CVC/CVV)")
	private String cvc;

	@Schema(example = "12", description = "Card expiration month (MM)")
	private String expMonth;

	@Schema(example = "2030", description = "Card expiration year (YYYY)")
	private String expYear;

	@Schema(example = "Tarjeta Visa exitosa", description = "Short description of this test card scenario")
	private String description;

	@Schema(example = "pm_card_visa", description = "Stripe test payment method ID")
	private String paymentMethodId;

}
