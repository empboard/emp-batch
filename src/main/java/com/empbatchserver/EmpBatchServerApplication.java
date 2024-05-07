package com.empbatchserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
* [ Spring Batch v4 -> v5 migration guide ]
* https://github.com/spring-projects/spring-batch/wiki/Spring-Batch-5.0-Migration-Guide*
* */

@SpringBootApplication
public class EmpBatchServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(EmpBatchServerApplication.class, args);
	}
}