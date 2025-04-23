package com.payment;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentTransactionManagerApplication {

	public static void main(String[] args) {

		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

		// Set environment variables manually (opcional si usás @Value)
		System.setProperty("STRIPE_API_KEY", dotenv.get("STRIPE_API_KEY"));
		System.setProperty("STRIPE_PUBLISHABLE_KEY", dotenv.get("STRIPE_PUBLISHABLE_KEY"));
		SpringApplication.run(PaymentTransactionManagerApplication.class, args);
	}

}
