package com.payment.enums;

import java.util.List;

public enum ConfirmablePaymentStatusEnum {

	REQUIRES_CONFIRMATION("requires_confirmation"), REQUIRES_PAYMENT_METHOD("requires_payment_method");

	private final String status;

	ConfirmablePaymentStatusEnum(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public static boolean isConfirmable(String status) {
		return List.of(REQUIRES_CONFIRMATION.status, REQUIRES_PAYMENT_METHOD.status).contains(status);
	}
}
