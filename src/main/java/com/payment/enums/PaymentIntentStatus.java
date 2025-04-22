package com.payment.enums;

import java.util.List;

public enum PaymentIntentStatus {
	REQUIRES_PAYMENT_METHOD("requires_payment_method"), REQUIRES_CONFIRMATION("requires_confirmation"),
	REQUIRES_ACTION("requires_action"), PROCESSING("processing"), SUCCEEDED("succeeded"), CANCELED("canceled"),
	REQUIRES_CAPTURE("requires_capture"), FAILED("failed");

	private final String status;

	PaymentIntentStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public static boolean isCancellable(String status) {
		return List.of(REQUIRES_PAYMENT_METHOD.status, REQUIRES_CONFIRMATION.status, REQUIRES_ACTION.status)
				.contains(status);
	}

	public static boolean isUpdatable(String status) {
		return List.of(REQUIRES_PAYMENT_METHOD.status, REQUIRES_CONFIRMATION.status).contains(status);
	}

}
