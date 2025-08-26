package com.bank.repository;

import com.bank.entity.RiskAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for RiskAssessment entity
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {

    /**
     * Find assessment by customer ID
     */
    List<RiskAssessment> findByCustomerId(Long customerId);

    /**
     * Find latest assessment by customer ID
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.customerId = :customerId ORDER BY ra.createdAt DESC")
    List<RiskAssessment> findLatestByCustomerId(@Param("customerId") Long customerId);

    /**
     * Find assessment by customer ID and status
     */
    List<RiskAssessment> findByCustomerIdAndStatus(Long customerId, RiskAssessment.AssessmentStatus status);

    /**
     * Find assessments by risk level
     */
    List<RiskAssessment> findByRiskLevel(RiskAssessment.RiskLevel riskLevel);

    /**
     * Find assessments by status
     */
    List<RiskAssessment> findByStatus(RiskAssessment.AssessmentStatus status);

    /**
     * Find assessments by risk score range
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.riskScore BETWEEN :minScore AND :maxScore")
    List<RiskAssessment> findByRiskScoreRange(@Param("minScore") Integer minScore, @Param("maxScore") Integer maxScore);

    /**
     * Find assessments by investment amount range
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.investmentAmount BETWEEN :minAmount AND :maxAmount")
    List<RiskAssessment> findByInvestmentAmountRange(@Param("minAmount") Double minAmount, @Param("maxAmount") Double maxAmount);

    /**
     * Find assessments created within date range
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.createdAt BETWEEN :startDate AND :endDate")
    List<RiskAssessment> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find pending assessments
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.status = 'PENDING' ORDER BY ra.createdAt ASC")
    List<RiskAssessment> findPendingAssessments();

    /**
     * Find assessments under review
     */
    @Query("SELECT ra FROM RiskAssessment ra WHERE ra.status = 'UNDER_REVIEW' ORDER BY ra.createdAt ASC")
    List<RiskAssessment> findUnderReviewAssessments();

    /**
     * Count assessments by risk level
     */
    @Query("SELECT ra.riskLevel, COUNT(ra) FROM RiskAssessment ra GROUP BY ra.riskLevel")
    List<Object[]> countByRiskLevel();

    /**
     * Count assessments by status
     */
    @Query("SELECT ra.status, COUNT(ra) FROM RiskAssessment ra GROUP BY ra.status")
    List<Object[]> countByStatus();
}
