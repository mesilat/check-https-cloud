package com.mesilat.certs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// @EnableAutoConfiguration(exclude = {ErrorMvcAutoConfiguration.class})
public class CheckHttpsCertificateServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckHttpsCertificateServiceApplication.class, args);
    }
}
