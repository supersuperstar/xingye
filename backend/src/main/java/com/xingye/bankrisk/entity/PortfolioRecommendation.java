package com.xingye.bankrisk.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;

/**
 * 投资组合推荐实体类
 * 对应数据库中的 portfolio_recommendations 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "portfolio_recommendations")
public class PortfolioRecommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 客户ID（冗余字段，便于按人查询）
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * 关联的工单ID
     */
    @Column(name = "work_order_id")
    private Long workOrderId;

    /**
     * 产品ID列表（JSON格式）
     */
    @Column(name = "product_ids", nullable = false, columnDefinition = "TEXT")
    private String productIds;

    /**
     * 分配比例列表（JSON格式）
     */
    @Column(name = "alloc_pcts", nullable = false, columnDefinition = "TEXT")
    private String allocPcts;

    /**
     * AI大模型生成或优化建议（JSON格式）
     */
    @Column(name = "llm_suggestion", columnDefinition = "TEXT")
    private String llmSuggestion;

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
