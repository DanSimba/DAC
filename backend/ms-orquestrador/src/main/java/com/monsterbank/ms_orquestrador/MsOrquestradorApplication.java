package com.monsterbank.ms_orquestrador;

import com.monsterbank.ms_orquestrador.messaging.producer.ClienteCommandProducer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MsOrquestradorApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsOrquestradorApplication.class, args);

	}

	@Bean
	CommandLineRunner testarRabbit(ClienteCommandProducer producer) {
		return args -> {
			producer.enviar("hello rabbit");
		};
	}

}
