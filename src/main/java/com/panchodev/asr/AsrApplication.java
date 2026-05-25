package com.panchodev.asr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AsrApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsrApplication.class, args);
	}

}
