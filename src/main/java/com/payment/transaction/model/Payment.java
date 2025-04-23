package com.payment.transaction.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "payment_intent_id", unique = true)
	private String paymentIntentId;
	private String status;
	private Long amount;
	private String currency;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

}
