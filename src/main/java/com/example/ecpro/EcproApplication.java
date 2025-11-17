package com.example.ecpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
//@Profile(value="prod")
public class EcproApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcproApplication.class, args);
	}

}
