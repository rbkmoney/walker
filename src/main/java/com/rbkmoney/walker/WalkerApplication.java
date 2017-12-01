package com.rbkmoney.walker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;


@SpringBootApplication(scanBasePackages = {"com.rbkmoney.walker", "com.rbkmoney.dbinit"})
@ServletComponentScan
public class WalkerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalkerApplication.class, args);
	}
}
