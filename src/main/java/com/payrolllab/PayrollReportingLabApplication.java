package com.payrolllab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PayrollReportingLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(PayrollReportingLabApplication.class, args);
	}

}
