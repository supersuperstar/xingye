package com.xingye.bankrisk.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 产品评分实体类
 * 对应数据库中的 product_ratings 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_ratings")
public class ProductRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 产品ID
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * 评分日期
     */
    @Column(name = "rating_date", nullable = false)
    private LocalDate ratingDate;

    /**
     * 综合评分（1-10）
     */
    @Column(name = "overall_rating", precision = 3, scale = 1)
    private BigDecimal overallRating;

    /**
     * 风险调整后评分
     */
    @Column(name = "risk_adjusted_rating", precision = 3, scale = 1)
    private BigDecimal riskAdjustedRating;

    /**
     * 业绩评分
     */
    @Column(name = "performance_rating", precision = 3, scale = 1)
    private BigDecimal performanceRating;

    /**
     * 流动性评分
     */
    @Column(name = "liquidity_rating", precision = 3, scale = 1)
    private BigDecimal liquidityRating;

    /**
     * 评级机构
     */
    @Column(name = "rating_agency", length = 100)
    private String ratingAgency;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
