package com.payment.card.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class PaymentConfirmCardDetailsDTO {

	@Schema(description = "The payment method ID from Stripe, if pre-existing", required = false)
	private String paymentMethodId;

	@Schema(description = "Card details for new payment method", required = false)
	private CardDetailsDTO cardDetails;

}
