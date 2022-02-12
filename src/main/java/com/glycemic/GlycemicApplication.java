package com.glycemic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class GlycemicApplication {

	public static void main(String[] args) {
		SpringApplication.run(GlycemicApplication.class, args);
	}

}
