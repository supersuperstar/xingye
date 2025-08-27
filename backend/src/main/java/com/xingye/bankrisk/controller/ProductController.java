package com.xingye.bankrisk.controller;

import com.xingye.bankrisk.entity.Product;
import com.xingye.bankrisk.entity.ProductTag;
import com.xingye.bankrisk.entity.User;
import com.xingye.bankrisk.service.ProductRecommendationService;
import com.xingye.bankrisk.service.ProductRecommendationService.ProductRecommendationResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 产品控制器
 * 处理产品查询和个性化推荐相关的请求
 */
@Slf4j
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "产品管理", description = "产品查询和推荐相关接口")
public class ProductController {

    private final ProductRecommendationService productRecommendationService;

    /**
     * 获取个性化产品推荐
     */
    @PostMapping("/recommend")
    @Operation(summary = "获取个性化产品推荐", description = "根据用户风险评分和偏好获取个性化产品推荐")
    public ResponseEntity<Map<String, Object>> getPersonalizedRecommendations(
            @RequestBody RecommendationRequest request) {

        log.info("[INFO]ProductController::getPersonalizedRecommendations: 获取个性化推荐 - Score: {}", request.getUserScore());

        try {
            ProductRecommendationResult result = productRecommendationService.recommendProducts(
                    request.getUserScore(),
                    request.getRiskLevel(),
                    request.getInvestAmount(),
                    request.getPreferences()
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "个性化推荐生成成功");
            response.put("data", result);

            log.info("[INFO]ProductController::getPersonalizedRecommendations: 个性化推荐生成完成");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]ProductController::getPersonalizedRecommendations: 个性化推荐生成失败", e);
            return createErrorResponse("个性化推荐生成失败: " + e.getMessage());
        }
    }

    /**
     * 获取产品列表（支持筛选）
     */
    @GetMapping
    @Operation(summary = "获取产品列表", description = "获取产品列表，支持多种筛选条件")
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(required = false) List<Product.ProductType> productTypes,
            @RequestParam(required = false) List<User.RiskLevel> riskLevels,
            @RequestParam(required = false) BigDecimal minReturn,
            @RequestParam(required = false) BigDecimal maxReturn,
            @RequestParam(required = false) List<String> sectors,
            @RequestParam(defaultValue = "expectedReturn") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("[INFO]ProductController::getProducts: 获取产品列表 - Page: {}, Size: {}", page, size);

        try {
            // 这里应该调用ProductService来获取筛选后的产品
            // 暂时返回模拟数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "产品列表查询成功");
            response.put("data", Map.of(
                    "products", List.of(),
                    "total", 0,
                    "page", page,
                    "size", size
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]ProductController::getProducts: 产品列表查询失败", e);
            return createErrorResponse("产品列表查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取产品详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取产品详情", description = "获取指定产品的详细信息")
    public ResponseEntity<Map<String, Object>> getProductDetail(@PathVariable Long id) {
        log.info("[INFO]ProductController::getProductDetail: 获取产品详情 - ID: {}", id);

        try {
            // 这里应该调用ProductService来获取产品详情
            // 暂时返回模拟数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "id", id,
                    "name", "示例产品",
                    "description", "这是一个示例产品"
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]ProductController::getProductDetail: 获取产品详情失败", e);
            return createErrorResponse("获取产品详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取产品标签列表
     */
    @GetMapping("/tags")
    @Operation(summary = "获取产品标签", description = "获取所有产品标签")
    public ResponseEntity<Map<String, Object>> getProductTags() {
        log.info("[INFO]ProductController::getProductTags: 获取产品标签");

        try {
            // 这里应该调用ProductTagService来获取标签
            // 暂时返回模拟数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", List.of(
                    Map.of("name", "高流动性", "category", "流动性"),
                    Map.of("name", "低波动", "category", "风险"),
                    Map.of("name", "稳健增长", "category", "策略")
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]ProductController::getProductTags: 获取产品标签失败", e);
            return createErrorResponse("获取产品标签失败: " + e.getMessage());
        }
    }

    /**
     * 根据标签获取产品
     */
    @GetMapping("/by-tag/{tagId}")
    @Operation(summary = "根据标签获取产品", description = "获取指定标签下的所有产品")
    public ResponseEntity<Map<String, Object>> getProductsByTag(@PathVariable Long tagId) {
        log.info("[INFO]ProductController::getProductsByTag: 根据标签获取产品 - TagID: {}", tagId);

        try {
            // 这里应该调用ProductService来获取标签关联的产品
            // 暂时返回模拟数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", List.of());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]ProductController::getProductsByTag: 根据标签获取产品失败", e);
            return createErrorResponse("根据标签获取产品失败: " + e.getMessage());
        }
    }

    /**
     * 获取产品统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取产品统计", description = "获取产品相关的统计信息")
    public ResponseEntity<Map<String, Object>> getProductStatistics() {
        log.info("[INFO]ProductController::getProductStatistics: 获取产品统计");

        try {
            // 这里应该调用ProductService来获取统计数据
            // 暂时返回模拟数据
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "totalProducts", 15,
                    "byType", Map.of(
                            "CASH", 3,
                            "BOND", 3,
                            "ETF", 3,
                            "STOCK", 6
                    ),
                    "byRiskLevel", Map.of(
                            "CONSERVATIVE", 5,
                            "MODERATE", 6,
                            "AGGRESSIVE", 4
                    )
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]ProductController::getProductStatistics: 获取产品统计失败", e);
            return createErrorResponse("获取产品统计失败: " + e.getMessage());
        }
    }

    /**
     * 创建错误响应
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("code", "PRODUCT_ERROR");

        return ResponseEntity.badRequest().body(response);
    }

    // 请求DTO类

    /**
     * 推荐请求
     */
    public static class RecommendationRequest {
        private int userScore;
        private User.RiskLevel riskLevel;
        private BigDecimal investAmount;
        private Map<String, String> preferences;

        // Getters and Setters
        public int getUserScore() { return userScore; }
        public void setUserScore(int userScore) { this.userScore = userScore; }

        public User.RiskLevel getRiskLevel() { return riskLevel; }
        public void setRiskLevel(User.RiskLevel riskLevel) { this.riskLevel = riskLevel; }

        public BigDecimal getInvestAmount() { return investAmount; }
        public void setInvestAmount(BigDecimal investAmount) { this.investAmount = investAmount; }

        public Map<String, String> getPreferences() { return preferences; }
        public void setPreferences(Map<String, String> preferences) { this.preferences = preferences; }
    }
}
