package com.connecttoweChat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.connecttoweChat")
@EnableConfigurationProperties({LiquibaseProperties.class})
@Slf4j
public class ConnecttoweChatApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConnecttoweChatApplication.class, args);
		log.info("-------------------------");
		log.info("-------------------------");
	}

}
