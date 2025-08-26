package com.bank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Risk assessment entity for storing risk assessment details
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Data
@Entity
@Table(name = "risk_assessments")
@EqualsAndHashCode(callSuper = true)
public class RiskAssessment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "risk_score", nullable = false)
    private Integer riskScore;

    @Column(name = "risk_level", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(name = "investment_amount", nullable = false)
    private Double investmentAmount;

    @Column(name = "questionnaire_answers", columnDefinition = "TEXT")
    private String questionnaireAnswers; // JSON string

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AssessmentStatus status = AssessmentStatus.PENDING;

    @Column(name = "remarks", length = 500)
    private String remarks;

    /**
     * Risk level enumeration
     */
    public enum RiskLevel {
        CONSERVATIVE,  // Conservative investor
        MODERATE,      // Moderate investor
        AGGRESSIVE     // Aggressive investor
    }

    /**
     * Assessment status enumeration
     */
    public enum AssessmentStatus {
        PENDING,       // Pending review
        UNDER_REVIEW,  // Under review
        APPROVED,      // Approved
        REJECTED,      // Rejected
        COMPLETED      // Completed
    }
}
