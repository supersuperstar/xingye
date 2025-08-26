package com.bank.customer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Customer Service Application - 客户服务主启动类
 *
 * @author Bank System
 * @since 1.0.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);
        System.out.println("[INFO]CustomerServiceApplication: 客户服务启动成功");
    }
}
