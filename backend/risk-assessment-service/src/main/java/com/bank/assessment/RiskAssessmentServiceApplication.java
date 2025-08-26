package com.bank.assessment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Risk Assessment Service Application - 风险评估服务主启动类
 *
 * @author Bank System
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class RiskAssessmentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskAssessmentServiceApplication.class, args);
        System.out.println("[INFO]RiskAssessmentServiceApplication: 风险评估服务启动成功");
    }
}
