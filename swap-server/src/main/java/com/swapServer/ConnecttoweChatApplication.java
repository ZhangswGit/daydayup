package com.swapServer;

import com.swapServer.config.AliyunProperties;
import com.swapServer.config.NettyProperties;
import com.swapServer.netty.NettyServer;
import com.swapServer.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@Slf4j
@SpringBootApplication
@ComponentScan("com.swapServer")
@MapperScan("com.swapServer.mapper")
@EnableConfigurationProperties({LiquibaseProperties.class, AliyunProperties.class, NettyProperties.class})
public class ConnecttoweChatApplication implements CommandLineRunner {

	@Autowired
	NettyServer nettyServer;

	@Autowired
	UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(ConnecttoweChatApplication.class, args);
		log.info("-------------------------");
		log.info("-------------------------");
	}

	@Override
	public void run(String... args) throws Exception {
		nettyServer.start(userService);
	}
}
