package com.xingye.bankrisk.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 投资产品实体类
 * 对应数据库中的 products 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 产品名称
     */
    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    /**
     * 产品类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false,
            columnDefinition = "ENUM('CASH','BOND','CREDIT','ETF','STOCK','ALT','REITS','COMMODITY','OTHER')")
    private ProductType productType;

    /**
     * 风险等级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false,
            columnDefinition = "ENUM('conservative','moderate','aggressive')")
    private User.RiskLevel riskLevel;

    /**
     * 预期收益率（百分比）
     */
    @Column(name = "expected_return", precision = 6, scale = 2)
    private BigDecimal expectedReturn;

    /**
     * 预期波动率（百分比）
     */
    @Column(name = "expected_volatility", precision = 6, scale = 2)
    private BigDecimal expectedVolatility;

    /**
     * 夏普比率
     */
    @Column(name = "sharpe_ratio", precision = 8, scale = 4)
    private BigDecimal sharpeRatio;

    /**
     * 最大回撤（百分比）
     */
    @Column(name = "max_drawdown", precision = 6, scale = 2)
    private BigDecimal maxDrawdown;

    /**
     * 1年历史收益率（百分比）
     */
    @Column(name = "historical_return_1y", precision = 6, scale = 2)
    private BigDecimal historicalReturn1y;

    /**
     * 3年历史收益率（百分比）
     */
    @Column(name = "historical_return_3y", precision = 6, scale = 2)
    private BigDecimal historicalReturn3y;

    /**
     * 5年历史收益率（百分比）
     */
    @Column(name = "historical_return_5y", precision = 6, scale = 2)
    private BigDecimal historicalReturn5y;

    /**
     * 费率（百分比）
     */
    @Column(name = "expense_ratio", precision = 6, scale = 4)
    private BigDecimal expenseRatio;

    /**
     * 最低投资额
     */
    @Column(name = "minimum_investment", precision = 12, scale = 2)
    private BigDecimal minimumInvestment;

    /**
     * 流动性评分（1-10）
     */
    @Column(name = "liquidity_score")
    private Integer liquidityScore;

    /**
     * 市值规模
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "market_cap")
    private MarketCap marketCap;

    /**
     * 行业板块
     */
    @Column(name = "sector", length = 100)
    private String sector;

    /**
     * 产品代码
     */
    @Column(name = "code", length = 64)
    private String code;

    /**
     * 货币类型
     */
    @Column(name = "currency", length = 16, columnDefinition = "VARCHAR(16) DEFAULT 'CNY'")
    private String currency;

    /**
     * 是否激活
     */
    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    /**
     * 产品类型枚举
     */
    public enum ProductType {
        CASH,       // 现金
        BOND,       // 债券
        CREDIT,     // 信贷
        ETF,        // ETF基金
        STOCK,      // 股票
        ALT,        // 另类投资
        REITS,      // REITs
        COMMODITY,  // 商品
        OTHER       // 其他
    }

    /**
     * 市值规模枚举
     */
    public enum MarketCap {
        SMALL,      // 小盘股
        MID,        // 中盘股
        LARGE       // 大盘股
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (isActive == null) {
            isActive = true;
        }
        if (currency == null) {
            currency = "CNY";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
