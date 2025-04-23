package com.payment.transaction.enums;

import java.util.List;

public enum CapturablePaymentStatusEnum {
	REQUIRES_CAPTURE("requires_capture");

	private final String status;

	CapturablePaymentStatusEnum(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public static boolean isCapturable(String status) {
		return List.of(REQUIRES_CAPTURE.status).contains(status);
	}

}
