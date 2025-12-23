package com.dilos.gpa.engine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.dilos.gpa")
public class CamundaEngineApplication {

  public static void main(String... args) {
    SpringApplication.run(CamundaEngineApplication.class, args);
  }

}