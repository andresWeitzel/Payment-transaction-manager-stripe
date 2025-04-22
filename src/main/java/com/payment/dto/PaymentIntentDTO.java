package com.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentIntentDTO {	
	private String id;
	private Long amount;
	private String currency;
	private String status;
}
