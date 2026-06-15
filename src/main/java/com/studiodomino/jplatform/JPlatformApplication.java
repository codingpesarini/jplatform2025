package com.studiodomino.jplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class JPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(JPlatformApplication.class, args);
	}

}
