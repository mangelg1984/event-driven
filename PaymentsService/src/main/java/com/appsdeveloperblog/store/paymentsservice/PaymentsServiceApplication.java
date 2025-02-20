package com.appsdeveloperblog.store.paymentsservice;

import configuration.AxonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({AxonConfig.class})
public class PaymentsServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(PaymentsServiceApplication.class, args);
	}
}