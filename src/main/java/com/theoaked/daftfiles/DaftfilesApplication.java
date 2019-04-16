package com.theoaked.daftfiles;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = { "com.daftmau5.daftfiles.dto" })
@EnableJpaRepositories(basePackages = { "com.daftmau5.daftfiles.repository" })
@ComponentScan(basePackages = {"com.daftmau5.daftfiles"})
public class DaftfilesApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DaftfilesApplication.class, args);
	}

}
