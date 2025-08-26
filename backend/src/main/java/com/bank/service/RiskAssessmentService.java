package com.bank.service;

import com.bank.dto.RiskAssessmentDto;
import com.bank.dto.RiskAssessmentResultDto;
import com.bank.entity.RiskAssessment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for risk assessment operations
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
public interface RiskAssessmentService {

    /**
     * Submit a new risk assessment
     */
    RiskAssessmentResultDto submitAssessment(RiskAssessmentDto assessmentDto);

    /**
     * Get assessment by ID
     */
    Optional<RiskAssessment> getAssessmentById(Long id);

    /**
     * Get assessment by customer ID
     */
    List<RiskAssessment> getAssessmentByCustomerId(Long customerId);

    /**
     * Get latest assessment by customer ID
     */
    Optional<RiskAssessment> getLatestAssessmentByCustomerId(Long customerId);

    /**
     * Recalculate risk score for an assessment
     */
    RiskAssessmentResultDto recalculateRiskScore(Long assessmentId);

    /**
     * Calculate risk score based on questionnaire answers
     */
    Integer calculateRiskScore(Map<String, Object> questionnaireAnswers);

    /**
     * Determine risk level based on risk score
     */
    RiskAssessment.RiskLevel determineRiskLevel(Integer riskScore);

    /**
     * Get assessments by risk level
     */
    List<RiskAssessment> getAssessmentsByRiskLevel(RiskAssessment.RiskLevel riskLevel);

    /**
     * Get assessments by status
     */
    List<RiskAssessment> getAssessmentsByStatus(RiskAssessment.AssessmentStatus status);

    /**
     * Get pending assessments
     */
    List<RiskAssessment> getPendingAssessments();

    /**
     * Get assessments under review
     */
    List<RiskAssessment> getUnderReviewAssessments();

    /**
     * Update assessment status
     */
    RiskAssessment updateAssessmentStatus(Long assessmentId, RiskAssessment.AssessmentStatus status);

    /**
     * Get assessment statistics
     */
    Map<String, Object> getAssessmentStatistics();
}
