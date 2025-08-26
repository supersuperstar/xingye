package com.bank.service.impl;

import com.bank.dto.RiskAssessmentDto;
import com.bank.dto.RiskAssessmentResultDto;
import com.bank.entity.RiskAssessment;
import com.bank.repository.RiskAssessmentRepository;
import com.bank.service.RiskAssessmentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of RiskAssessmentService
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final ObjectMapper objectMapper;

    @Override
    public RiskAssessmentResultDto submitAssessment(RiskAssessmentDto assessmentDto) {
        log.info("[INFO]RiskAssessmentServiceImpl::submitAssessment: Submitting assessment for customer: {}", assessmentDto.getCustomerId());

        try {
            // Calculate risk score
            Integer riskScore = calculateRiskScore(assessmentDto.getQuestionnaireAnswers());

            // Determine risk level
            RiskAssessment.RiskLevel riskLevel = determineRiskLevel(riskScore);

            // Convert questionnaire answers to JSON
            String questionnaireAnswersJson = objectMapper.writeValueAsString(assessmentDto.getQuestionnaireAnswers());

            // Create risk assessment entity
            RiskAssessment assessment = new RiskAssessment();
            assessment.setCustomerId(assessmentDto.getCustomerId());
            assessment.setRiskScore(riskScore);
            assessment.setRiskLevel(riskLevel);
            assessment.setInvestmentAmount(assessmentDto.getInvestmentAmount());
            assessment.setQuestionnaireAnswers(questionnaireAnswersJson);
            assessment.setStatus(RiskAssessment.AssessmentStatus.PENDING);

            // Save assessment
            RiskAssessment savedAssessment = riskAssessmentRepository.save(assessment);

            log.info("[INFO]RiskAssessmentServiceImpl::submitAssessment: Assessment submitted successfully with ID: {}", savedAssessment.getId());

            return RiskAssessmentResultDto.fromEntity(savedAssessment);

        } catch (JsonProcessingException e) {
            log.error("[ERROR]RiskAssessmentServiceImpl::submitAssessment: Error processing questionnaire answers: {}", e.getMessage());
            throw new RuntimeException("Error processing questionnaire answers", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RiskAssessment> getAssessmentById(Long id) {
        log.debug("[DEBUG]RiskAssessmentServiceImpl::getAssessmentById: Getting assessment by ID: {}", id);
        return riskAssessmentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessment> getAssessmentByCustomerId(Long customerId) {
        log.debug("[DEBUG]RiskAssessmentServiceImpl::getAssessmentByCustomerId: Getting assessments for customer: {}", customerId);
        return riskAssessmentRepository.findByCustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RiskAssessment> getLatestAssessmentByCustomerId(Long customerId) {
        log.debug("[DEBUG]RiskAssessmentServiceImpl::getLatestAssessmentByCustomerId: Getting latest assessment for customer: {}", customerId);
        List<RiskAssessment> assessments = riskAssessmentRepository.findLatestByCustomerId(customerId);
        return assessments.isEmpty() ? Optional.empty() : Optional.of(assessments.get(0));
    }

    @Override
    public RiskAssessmentResultDto recalculateRiskScore(Long assessmentId) {
        log.info("[INFO]RiskAssessmentServiceImpl::recalculateRiskScore: Recalculating risk score for assessment: {}", assessmentId);

        RiskAssessment assessment = riskAssessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));

        try {
            // Parse questionnaire answers
            Map<String, Object> questionnaireAnswers = objectMapper.readValue(
                    assessment.getQuestionnaireAnswers(), Map.class);

            // Recalculate risk score
            Integer newRiskScore = calculateRiskScore(questionnaireAnswers);
            RiskAssessment.RiskLevel newRiskLevel = determineRiskLevel(newRiskScore);

            // Update assessment
            assessment.setRiskScore(newRiskScore);
            assessment.setRiskLevel(newRiskLevel);

            RiskAssessment updatedAssessment = riskAssessmentRepository.save(assessment);

            log.info("[INFO]RiskAssessmentServiceImpl::recalculateRiskScore: Risk score recalculated successfully");

            return RiskAssessmentResultDto.fromEntity(updatedAssessment);

        } catch (JsonProcessingException e) {
            log.error("[ERROR]RiskAssessmentServiceImpl::recalculateRiskScore: Error processing questionnaire answers: {}", e.getMessage());
            throw new RuntimeException("Error processing questionnaire answers", e);
        }
    }

    @Override
    public Integer calculateRiskScore(Map<String, Object> questionnaireAnswers) {
        log.debug("[DEBUG]RiskAssessmentServiceImpl::calculateRiskScore: Calculating risk score");

        int totalScore = 0;

        // Age range scoring (0-20 points)
        String ageRange = (String) questionnaireAnswers.get("ageRange");
        switch (ageRange) {
            case "18-25":
                totalScore += 20;
                break;
            case "26-35":
                totalScore += 15;
                break;
            case "36-45":
                totalScore += 10;
                break;
            case "46-55":
                totalScore += 5;
                break;
            case "56+":
                totalScore += 0;
                break;
        }

        // Income level scoring (0-25 points)
        String incomeLevel = (String) questionnaireAnswers.get("incomeLevel");
        switch (incomeLevel) {
            case "50000以下":
                totalScore += 5;
                break;
            case "50000-100000":
                totalScore += 10;
                break;
            case "100000-200000":
                totalScore += 15;
                break;
            case "200000-500000":
                totalScore += 20;
                break;
            case "500000以上":
                totalScore += 25;
                break;
        }

        // Investment experience scoring (0-20 points)
        String investmentExperience = (String) questionnaireAnswers.get("investmentExperience");
        switch (investmentExperience) {
            case "无经验":
                totalScore += 0;
                break;
            case "1年以下":
                totalScore += 5;
                break;
            case "1-3年":
                totalScore += 10;
                break;
            case "3-5年":
                totalScore += 15;
                break;
            case "5年以上":
                totalScore += 20;
                break;
        }

        // Risk tolerance scoring (0-25 points)
        String riskTolerance = (String) questionnaireAnswers.get("riskTolerance");
        switch (riskTolerance) {
            case "保守":
                totalScore += 0;
                break;
            case "稳健":
                totalScore += 8;
                break;
            case "平衡":
                totalScore += 15;
                break;
            case "积极":
                totalScore += 20;
                break;
            case "激进":
                totalScore += 25;
                break;
        }

        // Investment goal scoring (0-10 points)
        String investmentGoal = (String) questionnaireAnswers.get("investmentGoal");
        switch (investmentGoal) {
            case "保值":
                totalScore += 0;
                break;
            case "稳健增值":
                totalScore += 3;
                break;
            case "平衡增长":
                totalScore += 6;
                break;
            case "积极增长":
                totalScore += 8;
                break;
            case "高收益":
                totalScore += 10;
                break;
        }

        // Investment period scoring (0-10 points)
        String investmentPeriod = (String) questionnaireAnswers.get("investmentPeriod");
        switch (investmentPeriod) {
            case "1年以下":
                totalScore += 0;
                break;
            case "1-3年":
                totalScore += 3;
                break;
            case "3-5年":
                totalScore += 6;
                break;
            case "5-10年":
                totalScore += 8;
                break;
            case "10年以上":
                totalScore += 10;
                break;
        }

        log.debug("[DEBUG]RiskAssessmentServiceImpl::calculateRiskScore: Calculated risk score: {}", totalScore);
        return totalScore;
    }

    @Override
    public RiskAssessment.RiskLevel determineRiskLevel(Integer riskScore) {
        log.debug("[DEBUG]RiskAssessmentServiceImpl::determineRiskLevel: Determining risk level for score: {}", riskScore);

        if (riskScore <= 30) {
            return RiskAssessment.RiskLevel.CONSERVATIVE;
        } else if (riskScore <= 60) {
            return RiskAssessment.RiskLevel.MODERATE;
        } else {
            return RiskAssessment.RiskLevel.AGGRESSIVE;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessment> getAssessmentsByRiskLevel(RiskAssessment.RiskLevel riskLevel) {
        log.debug("[DEBUG]RiskAssessmentServiceImpl::getAssessmentsByRiskLevel: Getting assessments by risk level: {}", riskLevel);
        return riskAssessmentRepository.findByRiskLevel(riskLevel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessment> getAssessmentsByStatus(RiskAssessment.AssessmentStatus status) {
        log.debug("[DEBUG]RiskAssessmentServiceImpl::getAssessmentsByStatus: Getting assessments by status: {}", status);
        return riskAssessmentRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessment> getPendingAssessments() {
        log.debug("[DEBUG]RiskAssessmentServiceImpl::getPendingAssessments: Getting pending assessments");
        return riskAssessmentRepository.findPendingAssessments();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RiskAssessment> getUnderReviewAssessments() {
        log.debug("[DEBUG]RiskAssessmentServiceImpl::getUnderReviewAssessments: Getting assessments under review");
        return riskAssessmentRepository.findUnderReviewAssessments();
    }

    @Override
    public RiskAssessment updateAssessmentStatus(Long assessmentId, RiskAssessment.AssessmentStatus status) {
        log.info("[INFO]RiskAssessmentServiceImpl::updateAssessmentStatus: Updating assessment status to {} for assessment: {}", status, assessmentId);

        RiskAssessment assessment = riskAssessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment not found with ID: " + assessmentId));

        assessment.setStatus(status);
        RiskAssessment updatedAssessment = riskAssessmentRepository.save(assessment);

        log.info("[INFO]RiskAssessmentServiceImpl::updateAssessmentStatus: Assessment status updated successfully");

        return updatedAssessment;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getAssessmentStatistics() {
        log.debug("[DEBUG]RiskAssessmentServiceImpl::getAssessmentStatistics: Getting assessment statistics");

        Map<String, Object> statistics = new HashMap<>();

        // Count by risk level
        List<Object[]> riskLevelCounts = riskAssessmentRepository.countByRiskLevel();
        Map<String, Long> riskLevelStats = new HashMap<>();
        for (Object[] result : riskLevelCounts) {
            riskLevelStats.put(result[0].toString(), (Long) result[1]);
        }
        statistics.put("riskLevelCounts", riskLevelStats);

        // Count by status
        List<Object[]> statusCounts = riskAssessmentRepository.countByStatus();
        Map<String, Long> statusStats = new HashMap<>();
        for (Object[] result : statusCounts) {
            statusStats.put(result[0].toString(), (Long) result[1]);
        }
        statistics.put("statusCounts", statusStats);

        // Total assessments
        long totalAssessments = riskAssessmentRepository.count();
        statistics.put("totalAssessments", totalAssessments);

        return statistics;
    }
}
