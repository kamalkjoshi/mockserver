package org.kj.web.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackages = { "org.kj.web.service" })
public class MockServerApp {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(MockServerApp.class, args);
	}
}
