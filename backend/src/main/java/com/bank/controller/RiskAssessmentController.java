package com.bank.controller;

import com.bank.dto.RiskAssessmentDto;
import com.bank.dto.RiskAssessmentResultDto;
import com.bank.entity.RiskAssessment;
import com.bank.response.ApiResponse;
import com.bank.service.RiskAssessmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for risk assessment operations
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/risk-assessments")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    /**
     * Submit a new risk assessment
     */
    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<RiskAssessmentResultDto>> submitAssessment(@Valid @RequestBody RiskAssessmentDto assessmentDto) {
        log.info("[INFO]RiskAssessmentController::submitAssessment: Submitting assessment for customer: {}", assessmentDto.getCustomerId());

        try {
            RiskAssessmentResultDto result = riskAssessmentService.submitAssessment(assessmentDto);
            return ResponseEntity.ok(ApiResponse.success("Risk assessment submitted successfully", result));
        } catch (Exception e) {
            log.error("[ERROR]RiskAssessmentController::submitAssessment: Error submitting assessment: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    /**
     * Get assessment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RiskAssessment>> getAssessmentById(@PathVariable Long id) {
        log.info("[INFO]RiskAssessmentController::getAssessmentById: Getting assessment with ID: {}", id);

        return riskAssessmentService.getAssessmentById(id)
                .map(assessment -> ResponseEntity.ok(ApiResponse.success(assessment)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get assessment by customer ID
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<RiskAssessment>>> getAssessmentByCustomerId(@PathVariable Long customerId) {
        log.info("[INFO]RiskAssessmentController::getAssessmentByCustomerId: Getting assessments for customer: {}", customerId);

        List<RiskAssessment> assessments = riskAssessmentService.getAssessmentByCustomerId(customerId);
        return ResponseEntity.ok(ApiResponse.success(assessments));
    }

    /**
     * Get latest assessment by customer ID
     */
    @GetMapping("/customer/{customerId}/latest")
    public ResponseEntity<ApiResponse<RiskAssessment>> getLatestAssessmentByCustomerId(@PathVariable Long customerId) {
        log.info("[INFO]RiskAssessmentController::getLatestAssessmentByCustomerId: Getting latest assessment for customer: {}", customerId);

        return riskAssessmentService.getLatestAssessmentByCustomerId(customerId)
                .map(assessment -> ResponseEntity.ok(ApiResponse.success(assessment)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Recalculate risk score for an assessment
     */
    @PostMapping("/{id}/recalculate")
    public ResponseEntity<ApiResponse<RiskAssessmentResultDto>> recalculateRiskScore(@PathVariable Long id) {
        log.info("[INFO]RiskAssessmentController::recalculateRiskScore: Recalculating risk score for assessment: {}", id);

        try {
            RiskAssessmentResultDto result = riskAssessmentService.recalculateRiskScore(id);
            return ResponseEntity.ok(ApiResponse.success("Risk score recalculated successfully", result));
        } catch (Exception e) {
            log.error("[ERROR]RiskAssessmentController::recalculateRiskScore: Error recalculating risk score: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    /**
     * Get assessments by risk level
     */
    @GetMapping("/risk-level/{riskLevel}")
    public ResponseEntity<ApiResponse<List<RiskAssessment>>> getAssessmentsByRiskLevel(
            @PathVariable RiskAssessment.RiskLevel riskLevel) {
        log.info("[INFO]RiskAssessmentController::getAssessmentsByRiskLevel: Getting assessments by risk level: {}", riskLevel);

        List<RiskAssessment> assessments = riskAssessmentService.getAssessmentsByRiskLevel(riskLevel);
        return ResponseEntity.ok(ApiResponse.success(assessments));
    }

    /**
     * Get assessments by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<RiskAssessment>>> getAssessmentsByStatus(
            @PathVariable RiskAssessment.AssessmentStatus status) {
        log.info("[INFO]RiskAssessmentController::getAssessmentsByStatus: Getting assessments by status: {}", status);

        List<RiskAssessment> assessments = riskAssessmentService.getAssessmentsByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(assessments));
    }

    /**
     * Get pending assessments
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<RiskAssessment>>> getPendingAssessments() {
        log.info("[INFO]RiskAssessmentController::getPendingAssessments: Getting pending assessments");

        List<RiskAssessment> assessments = riskAssessmentService.getPendingAssessments();
        return ResponseEntity.ok(ApiResponse.success(assessments));
    }

    /**
     * Get assessments under review
     */
    @GetMapping("/under-review")
    public ResponseEntity<ApiResponse<List<RiskAssessment>>> getUnderReviewAssessments() {
        log.info("[INFO]RiskAssessmentController::getUnderReviewAssessments: Getting assessments under review");

        List<RiskAssessment> assessments = riskAssessmentService.getUnderReviewAssessments();
        return ResponseEntity.ok(ApiResponse.success(assessments));
    }

    /**
     * Update assessment status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<RiskAssessment>> updateAssessmentStatus(
            @PathVariable Long id,
            @RequestParam RiskAssessment.AssessmentStatus status) {
        log.info("[INFO]RiskAssessmentController::updateAssessmentStatus: Updating assessment status to {} for assessment: {}", status, id);

        try {
            RiskAssessment assessment = riskAssessmentService.updateAssessmentStatus(id, status);
            return ResponseEntity.ok(ApiResponse.success("Assessment status updated successfully", assessment));
        } catch (Exception e) {
            log.error("[ERROR]RiskAssessmentController::updateAssessmentStatus: Error updating assessment status: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    /**
     * Get assessment statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAssessmentStatistics() {
        log.info("[INFO]RiskAssessmentController::getAssessmentStatistics: Getting assessment statistics");

        Map<String, Object> statistics = riskAssessmentService.getAssessmentStatistics();
        return ResponseEntity.ok(ApiResponse.success(statistics));
    }

    /**
     * Calculate risk score from questionnaire answers (for testing)
     */
    @PostMapping("/calculate-score")
    public ResponseEntity<ApiResponse<Integer>> calculateRiskScore(@RequestBody Map<String, Object> questionnaireAnswers) {
        log.info("[INFO]RiskAssessmentController::calculateRiskScore: Calculating risk score from questionnaire");

        try {
            Integer riskScore = riskAssessmentService.calculateRiskScore(questionnaireAnswers);
            RiskAssessment.RiskLevel riskLevel = riskAssessmentService.determineRiskLevel(riskScore);

            Map<String, Object> result = Map.of(
                "riskScore", riskScore,
                "riskLevel", riskLevel
            );

            return ResponseEntity.ok(ApiResponse.success("Risk score calculated successfully", result));
        } catch (Exception e) {
            log.error("[ERROR]RiskAssessmentController::calculateRiskScore: Error calculating risk score: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }
}
