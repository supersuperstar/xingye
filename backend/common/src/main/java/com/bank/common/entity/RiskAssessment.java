package com.bank.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * RiskAssessment entity - 风险评估实体
 *
 * @author Bank System
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "risk_assessments")
@EqualsAndHashCode(callSuper = false)
public class RiskAssessment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 客户ID
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * 风险评分
     */
    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    /**
     * 风险等级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    /**
     * 投资金额
     */
    @Column(name = "investment_amount", precision = 15, scale = 2)
    private BigDecimal investmentAmount;

    /**
     * 问卷答案（JSON格式）
     */
    @Column(name = "questionnaire_answers", columnDefinition = "TEXT")
    private String questionnaireAnswers;

    /**
     * 评估状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private AssessmentStatus status = AssessmentStatus.PENDING;

    /**
     * 评估备注
     */
    @Column(name = "remarks", length = 500)
    private String remarks;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 风险等级枚举
     */
    public enum RiskLevel {
        CONSERVATIVE("保守型"),
        MODERATE("稳健型"),
        AGGRESSIVE("激进型");

        private final String description;

        RiskLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 评估状态枚举
     */
    public enum AssessmentStatus {
        PENDING("待评估"),
        ASSESSED("已评估"),
        REVIEWING("审核中"),
        APPROVED("已通过"),
        REJECTED("已拒绝");

        private final String description;

        AssessmentStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
