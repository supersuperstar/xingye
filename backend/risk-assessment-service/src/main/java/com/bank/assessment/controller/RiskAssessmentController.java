package com.bank.assessment.controller;

import com.bank.assessment.dto.RiskAssessmentDto;
import com.bank.assessment.dto.RiskAssessmentResultDto;
import com.bank.assessment.service.RiskAssessmentService;
import com.bank.common.entity.RiskAssessment;
import com.bank.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Risk Assessment Controller - 风险评估控制器
 *
 * @author Bank System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/risk-assessments")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    /**
     * 提交风险评估
     */
    @PostMapping("/submit")
    public ApiResponse<RiskAssessmentResultDto> submitAssessment(@Valid @RequestBody RiskAssessmentDto assessmentDto) {
        log.info("[INFO]RiskAssessmentController::submitAssessment: 提交风险评估 - 客户ID: {}", assessmentDto.getCustomerId());
        RiskAssessmentResultDto result = riskAssessmentService.submitAssessment(assessmentDto);
        return ApiResponse.success("风险评估提交成功", result);
    }

    /**
     * 获取风险评估结果
     */
    @GetMapping("/{id}")
    public ApiResponse<RiskAssessment> getAssessment(@PathVariable Long id) {
        log.info("[INFO]RiskAssessmentController::getAssessment: 获取风险评估结果 - ID: {}", id);
        RiskAssessment assessment = riskAssessmentService.getAssessmentById(id);
        return ApiResponse.success(assessment);
    }

    /**
     * 根据客户ID获取风险评估
     */
    @GetMapping("/customer/{customerId}")
    public ApiResponse<RiskAssessment> getAssessmentByCustomerId(@PathVariable Long customerId) {
        log.info("[INFO]RiskAssessmentController::getAssessmentByCustomerId: 获取客户风险评估 - 客户ID: {}", customerId);
        RiskAssessment assessment = riskAssessmentService.getAssessmentByCustomerId(customerId);
        return ApiResponse.success(assessment);
    }

    /**
     * 重新计算风险评分
     */
    @PostMapping("/{id}/recalculate")
    public ApiResponse<RiskAssessmentResultDto> recalculateRiskScore(@PathVariable Long id) {
        log.info("[INFO]RiskAssessmentController::recalculateRiskScore: 重新计算风险评分 - ID: {}", id);
        RiskAssessmentResultDto result = riskAssessmentService.recalculateRiskScore(id);
        return ApiResponse.success("风险评分重新计算成功", result);
    }
}
