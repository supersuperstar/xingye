package com.bank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Bank Risk Assessment System
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
public class BankRiskAssessmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankRiskAssessmentApplication.class, args);
        System.out.println("[INFO]BankRiskAssessmentApplication::main: Bank Risk Assessment System started successfully!");
    }
}
