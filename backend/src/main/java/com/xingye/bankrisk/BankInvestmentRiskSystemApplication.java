package com.xingye.bankrisk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 银行投资风险审核系统主应用程序类
 *
 * 这个Spring Boot应用程序提供了完整的银行投资风险评估和多级审核功能，
 * 支持客户风险评估、投资组合生成、多级审核流程等核心业务。
 */
@SpringBootApplication
public class BankInvestmentRiskSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankInvestmentRiskSystemApplication.class, args);
    }
}
