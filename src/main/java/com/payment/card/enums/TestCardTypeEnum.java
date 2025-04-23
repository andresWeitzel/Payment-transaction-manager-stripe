package com.payment.card.enums;

import java.util.Arrays;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestCardTypeEnum {

	VISA("Visa", "4242424242424242", "123", "12", "2030", "Tarjeta Visa exitosa", "pm_card_visa"),
	VISA_DEBIT("Visa Debit", "4000056655665556", "123", "12", "2030", "Tarjeta Visa Débito exitosa",
			"pm_card_visa_debit"),
	MASTERCARD("Mastercard", "5555555555554444", "123", "11", "2030", "Tarjeta Mastercard exitosa",
			"pm_card_mastercard"),
	MASTERCARD_2("Mastercard (2-series)", "2223003122003222", "123", "11", "2030", "Tarjeta Mastercard 2-series",
			"pm_card_mastercard"),
	AMEX("American Express", "378282246310005", "1234", "10", "2030", "Tarjeta American Express exitosa",
			"pm_card_amex"),
	DISCOVER("Discover", "6011111111111117", "123", "09", "2030", "Tarjeta Discover exitosa", "pm_card_discover"),
	DINERS("Diners Club", "30569309025904", "123", "08", "2030", "Tarjeta Diners Club internacional", "pm_card_diners"),
	JCB("JCB", "3566002020360505", "123", "07", "2030", "Tarjeta JCB de prueba", "pm_card_jcb"),
	UNIONPAY("UnionPay", "6200000000000005", "123", "06", "2030", "Tarjeta UnionPay de prueba", "pm_card_unionpay");

	private final String brand;
	private final String number;
	private final String cvc;
	private final String expMonth;
	private final String expYear;
	private final String description;
	private final String paymentMethodId;

	public static Optional<TestCardTypeEnum> fromCardNumber(String cardNumber) {
		return Arrays.stream(values()).filter(tc -> tc.getNumber().equals(cardNumber)).findFirst();
	}
}
