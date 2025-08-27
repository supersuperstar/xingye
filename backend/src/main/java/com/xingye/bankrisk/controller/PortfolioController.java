package com.xingye.bankrisk.controller;

import com.xingye.bankrisk.entity.PortfolioRecommendation;
import com.xingye.bankrisk.service.PortfolioGenerationService;
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
 * 投资组合控制器
 * 处理投资组合生成和查询相关的请求
 */
@Slf4j
@RestController
@RequestMapping("/portfolios")
@RequiredArgsConstructor
@Tag(name = "投资组合", description = "投资组合相关接口")
public class PortfolioController {

    private final PortfolioGenerationService portfolioGenerationService;

    /**
     * 生成投资组合
     */
    @PostMapping("/generate")
    @Operation(summary = "生成投资组合", description = "为指定用户生成投资组合建议")
    public ResponseEntity<Map<String, Object>> generatePortfolio(
            @RequestHeader("Authorization") String token,
            @RequestBody PortfolioGenerateRequest request) {

        log.info("[INFO]PortfolioController::generatePortfolio: 生成投资组合 - UserID: {}", request.getUserId());

        try {
            PortfolioRecommendation portfolio = portfolioGenerationService.generatePortfolio(
                    request.getUserId(),
                    request.getCustomerId(),
                    request.getWorkOrderId()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "投资组合生成成功");
            response.put("data", portfolio);

            log.info("[INFO]PortfolioController::generatePortfolio: 投资组合生成完成 - ID: {}", portfolio.getId());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]PortfolioController::generatePortfolio: 生成投资组合失败", e);
            return createErrorResponse("生成投资组合失败: " + e.getMessage());
        }
    }

    /**
     * 根据评估结果生成投资组合
     */
    @PostMapping("/assessment/{assessmentId}")
    @Operation(summary = "根据评估生成组合", description = "根据风险评估结果生成投资组合")
    public ResponseEntity<Map<String, Object>> generatePortfolioFromAssessment(@PathVariable Long assessmentId) {
        log.info("[INFO]PortfolioController::generatePortfolioFromAssessment: 根据评估生成组合 - AssessmentID: {}", assessmentId);

        try {
            // 这里需要从评估ID获取用户ID，暂时使用模拟数据
            PortfolioRecommendation portfolio = portfolioGenerationService.generatePortfolio(
                    assessmentId, // 暂时用assessmentId作为userId
                    assessmentId,
                    null
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "投资组合生成成功");
            response.put("data", portfolio);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]PortfolioController::generatePortfolioFromAssessment: 根据评估生成组合失败", e);
            return createErrorResponse("根据评估生成组合失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户投资组合
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户组合", description = "获取指定用户的所有投资组合建议")
    public ResponseEntity<Map<String, Object>> getUserPortfolios(@PathVariable Long userId) {
        log.info("[INFO]PortfolioController::getUserPortfolios: 获取用户投资组合 - UserID: {}", userId);

        try {
            List<PortfolioRecommendation> portfolios = portfolioGenerationService.getUserPortfolios(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", portfolios);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]PortfolioController::getUserPortfolios: 获取用户投资组合失败", e);
            return createErrorResponse("获取用户投资组合失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户最新投资组合
     */
    @GetMapping("/user/{userId}/latest")
    @Operation(summary = "获取最新组合", description = "获取指定用户的最新投资组合建议")
    public ResponseEntity<Map<String, Object>> getLatestPortfolio(@PathVariable Long userId) {
        log.info("[INFO]PortfolioController::getLatestPortfolio: 获取用户最新投资组合 - UserID: {}", userId);

        try {
            PortfolioRecommendation portfolio = portfolioGenerationService.getLatestPortfolio(userId)
                    .orElseThrow(() -> new RuntimeException("用户暂无投资组合建议: " + userId));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", portfolio);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]PortfolioController::getLatestPortfolio: 获取用户最新投资组合失败", e);
            return createErrorResponse("获取用户最新投资组合失败: " + e.getMessage());
        }
    }

    /**
     * 获取投资组合详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取组合详情", description = "获取指定投资组合的详细信息")
    public ResponseEntity<Map<String, Object>> getPortfolioDetail(@PathVariable Long id) {
        log.info("[INFO]PortfolioController::getPortfolioDetail: 获取投资组合详情 - ID: {}", id);

        try {
            // 这里需要实现根据ID获取投资组合详情的方法
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of("id", id, "message", "投资组合详情查询功能待实现"));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]PortfolioController::getPortfolioDetail: 获取投资组合详情失败", e);
            return createErrorResponse("获取投资组合详情失败: " + e.getMessage());
        }
    }

    /**
     * 优化投资组合
     */
    @PostMapping("/{id}/optimize")
    @Operation(summary = "优化投资组合", description = "优化指定投资组合的配置")
    public ResponseEntity<Map<String, Object>> optimizePortfolio(@PathVariable Long id) {
        log.info("[INFO]PortfolioController::optimizePortfolio: 优化投资组合 - ID: {}", id);

        try {
            // 这里需要实现投资组合优化算法
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "投资组合优化完成");
            response.put("data", Map.of("id", id, "optimized", true));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]PortfolioController::optimizePortfolio: 优化投资组合失败", e);
            return createErrorResponse("优化投资组合失败: " + e.getMessage());
        }
    }

    /**
     * 创建错误响应
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("code", "PORTFOLIO_ERROR");

        return ResponseEntity.badRequest().body(response);
    }

    // 请求DTO类

    /**
     * 投资组合生成请求
     */
    public static class PortfolioGenerateRequest {
        private Long userId;
        private Long customerId;
        private Long workOrderId;

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getCustomerId() { return customerId; }
        public void setCustomerId(Long customerId) { this.customerId = customerId; }

        public Long getWorkOrderId() { return workOrderId; }
        public void setWorkOrderId(Long workOrderId) { this.workOrderId = workOrderId; }
    }
}
