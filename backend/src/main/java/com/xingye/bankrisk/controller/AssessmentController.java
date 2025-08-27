package com.xingye.bankrisk.controller;

import com.xingye.bankrisk.entity.Questionnaire;
import com.xingye.bankrisk.service.RiskAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 风险评估控制器
 * 处理风险评估相关的请求
 */
@Slf4j
@RestController
@RequestMapping("/risk-assessments")
@RequiredArgsConstructor
@Tag(name = "风险评估", description = "风险评估相关接口")
public class AssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    /**
     * 提交风险评估
     */
    @PostMapping("/submit")
    @Operation(summary = "提交风险评估", description = "提交风险评估问卷并计算风险等级")
    public ResponseEntity<Map<String, Object>> submitAssessment(
            @RequestHeader("Authorization") String token,
            @RequestBody AssessmentSubmitRequest request) {

        log.info("[INFO]AssessmentController::submitAssessment: 提交风险评估 - UserID: {}", request.getUserId());

        try {
            Questionnaire questionnaire = riskAssessmentService.submitAssessment(
                    request.getUserId(),
                    request.getAnswers()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "风险评估提交成功");
            response.put("data", questionnaire);

            log.info("[INFO]AssessmentController::submitAssessment: 风险评估完成 - QuestionnaireID: {}", questionnaire.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AssessmentController::submitAssessment: 风险评估提交失败", e);
            return createErrorResponse("风险评估提交失败: " + e.getMessage());
        }
    }

    /**
     * 获取评估详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取评估详情", description = "获取指定风险评估的详细信息")
    public ResponseEntity<Map<String, Object>> getAssessment(@PathVariable Long id) {
        log.info("[INFO]AssessmentController::getAssessment: 获取评估详情 - ID: {}", id);

        try {
            Questionnaire questionnaire = riskAssessmentService.getAssessmentHistory(id).stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("评估记录不存在: " + id));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", questionnaire);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AssessmentController::getAssessment: 获取评估详情失败", e);
            return createErrorResponse("获取评估详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户评估历史
     */
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "获取用户评估历史", description = "获取指定用户的风险评估历史记录")
    public ResponseEntity<Map<String, Object>> getCustomerAssessments(@PathVariable Long customerId) {
        log.info("[INFO]AssessmentController::getCustomerAssessments: 获取用户评估历史 - CustomerID: {}", customerId);

        try {
            List<Questionnaire> assessments = riskAssessmentService.getAssessmentHistory(customerId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", assessments);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AssessmentController::getCustomerAssessments: 获取用户评估历史失败", e);
            return createErrorResponse("获取用户评估历史失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户最新评估
     */
    @GetMapping("/customer/{customerId}/latest")
    @Operation(summary = "获取用户最新评估", description = "获取指定用户的最新风险评估结果")
    public ResponseEntity<Map<String, Object>> getLatestAssessment(@PathVariable Long customerId) {
        log.info("[INFO]AssessmentController::getLatestAssessment: 获取用户最新评估 - CustomerID: {}", customerId);

        try {
            Questionnaire latestAssessment = riskAssessmentService.getLatestAssessment(customerId)
                    .orElseThrow(() -> new RuntimeException("用户暂无评估记录: " + customerId));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", latestAssessment);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AssessmentController::getLatestAssessment: 获取用户最新评估失败", e);
            return createErrorResponse("获取用户最新评估失败: " + e.getMessage());
        }
    }

    /**
     * 重新计算风险评分
     */
    @PostMapping("/{id}/recalculate")
    @Operation(summary = "重新计算风险评分", description = "重新计算指定评估的风险评分")
    public ResponseEntity<Map<String, Object>> recalculateScore(@PathVariable Long id) {
        log.info("[INFO]AssessmentController::recalculateScore: 重新计算风险评分 - ID: {}", id);

        try {
            Questionnaire questionnaire = riskAssessmentService.recalculateRiskScore(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "风险评分重新计算完成");
            response.put("data", questionnaire);

            log.info("[INFO]AssessmentController::recalculateScore: 风险评分重新计算完成 - ID: {}", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AssessmentController::recalculateScore: 重新计算风险评分失败", e);
            return createErrorResponse("重新计算风险评分失败: " + e.getMessage());
        }
    }

    /**
     * 计算风险评分（工具方法）
     */
    @PostMapping("/calculate-score")
    @Operation(summary = "计算风险评分", description = "根据问卷答案计算风险评分")
    public ResponseEntity<Map<String, Object>> calculateScore(@RequestBody Map<String, String> answers) {
        log.info("[INFO]AssessmentController::calculateScore: 计算风险评分");

        try {
            // 这里应该从token中获取用户信息，暂时使用默认用户
            Map<String, Object> scoreResult = new HashMap<>();
            int riskScore = riskAssessmentService.calculateRiskScore(null, answers);
            String riskLevel = riskAssessmentService.determineRiskLevel(riskScore).toString();

            scoreResult.put("riskScore", riskScore);
            scoreResult.put("riskLevel", riskLevel);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", scoreResult);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AssessmentController::calculateScore: 计算风险评分失败", e);
            return createErrorResponse("计算风险评分失败: " + e.getMessage());
        }
    }

    /**
     * 创建错误响应
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("code", "ASSESSMENT_ERROR");

        return ResponseEntity.badRequest().body(response);
    }

    // 请求DTO类

    /**
     * 评估提交请求
     */
    public static class AssessmentSubmitRequest {
        private Long userId;
        private Map<String, String> answers;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Map<String, String> getAnswers() { return answers; }
        public void setAnswers(Map<String, String> answers) { this.answers = answers; }
    }
}
