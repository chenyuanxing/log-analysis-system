package com.cad.web;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Iterator;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cad.elasticsearchservice.Service","com.cad.elasticsearchservice.Dao","com.cad.web.*","com.cad.flinkservice.*","com.cad.collectionservice.*"})
@EnableScheduling
public class WebApplication {

	public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
	}
}
