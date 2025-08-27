package com.xingye.bankrisk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingye.bankrisk.entity.PortfolioRecommendation;
import com.xingye.bankrisk.entity.Product;
import com.xingye.bankrisk.entity.Questionnaire;
import com.xingye.bankrisk.entity.User;
import com.xingye.bankrisk.repository.PortfolioRecommendationRepository;
import com.xingye.bankrisk.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 投资组合生成服务类
 * 根据用户风险等级和偏好生成个性化的投资组合建议
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioGenerationService {

    private final ProductRepository productRepository;
    private final PortfolioRecommendationRepository portfolioRecommendationRepository;
    private final ObjectMapper objectMapper;

    // 投资组合配置
    private static final Map<User.RiskLevel, int[]> PORTFOLIO_CONFIGS = Map.of(
            User.RiskLevel.CONSERVATIVE, new int[]{70, 20, 10}, // 现金:债券:股票 = 70:20:10
            User.RiskLevel.MODERATE, new int[]{30, 40, 30},      // 现金:债券:股票 = 30:40:30
            User.RiskLevel.AGGRESSIVE, new int[]{10, 20, 70}     // 现金:债券:股票 = 10:20:70
    );

    /**
     * 为用户生成投资组合建议
     */
    @Transactional
    public PortfolioRecommendation generatePortfolio(Long userId, Long customerId, Long workOrderId) {
        log.info("[INFO]PortfolioGenerationService::generatePortfolio: 开始生成投资组合 - UserID: {}", userId);

        User user = getUserById(userId);
        User.RiskLevel riskLevel = getUserRiskLevel(user);
        List<Product> suitableProducts = getSuitableProducts(riskLevel);

        if (suitableProducts.isEmpty()) {
            throw new RuntimeException("没有找到适合该风险等级的产品: " + riskLevel);
        }

        // 生成投资组合
        PortfolioAllocation allocation = generatePortfolioAllocation(riskLevel, suitableProducts);

        // 创建投资组合推荐
        PortfolioRecommendation recommendation = PortfolioRecommendation.builder()
                .userId(userId)
                .customerId(customerId)
                .workOrderId(workOrderId)
                .productIds(objectMapper.valueToTree(allocation.getProductIds()))
                .allocPcts(objectMapper.valueToTree(allocation.getAllocations()))
                .llmSuggestion(generateLLMSuggestion(user, riskLevel, allocation))
                .build();

        PortfolioRecommendation savedRecommendation = portfolioRecommendationRepository.save(recommendation);

        log.info("[INFO]PortfolioGenerationService::generatePortfolio: 投资组合生成完成 - ID: {}", savedRecommendation.getId());
        return savedRecommendation;
    }

    /**
     * 根据评估结果生成投资组合
     */
    @Transactional
    public PortfolioRecommendation generatePortfolioFromAssessment(Questionnaire assessment) {
        log.info("[INFO]PortfolioGenerationService::generatePortfolioFromAssessment: 根据评估生成投资组合 - AssessmentID: {}", assessment.getId());

        return generatePortfolio(assessment.getUserId(), assessment.getUserId(), null);
    }

    /**
     * 获取用户的投资组合推荐
     */
    public List<PortfolioRecommendation> getUserPortfolios(Long userId) {
        return portfolioRecommendationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 获取最新的投资组合推荐
     */
    public Optional<PortfolioRecommendation> getLatestPortfolio(Long userId) {
        return Optional.ofNullable(portfolioRecommendationRepository.findLatestByUserId(userId));
    }

    /**
     * 优化投资组合分配
     */
    public PortfolioAllocation optimizePortfolio(User.RiskLevel riskLevel, BigDecimal totalAmount,
                                                List<Product> products) {
        log.info("[INFO]PortfolioGenerationService::optimizePortfolio: 优化投资组合 - RiskLevel: {}, Amount: {}", riskLevel, totalAmount);

        int[] config = PORTFOLIO_CONFIGS.get(riskLevel);
        if (config == null) {
            throw new RuntimeException("不支持的风险等级: " + riskLevel);
        }

        List<Product> cashProducts = products.stream()
                .filter(p -> p.getProductType() == Product.ProductType.CASH)
                .toList();

        List<Product> bondProducts = products.stream()
                .filter(p -> p.getProductType() == Product.ProductType.BOND)
                .toList();

        List<Product> stockProducts = products.stream()
                .filter(p -> p.getProductType() == Product.ProductType.STOCK ||
                           p.getProductType() == Product.ProductType.ETF)
                .toList();

        PortfolioAllocation allocation = new PortfolioAllocation();

        // 现金分配
        if (!cashProducts.isEmpty()) {
            BigDecimal cashAmount = totalAmount.multiply(BigDecimal.valueOf(config[0]))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            allocateProducts(allocation, cashProducts, cashAmount, config[0]);
        }

        // 债券分配
        if (!bondProducts.isEmpty()) {
            BigDecimal bondAmount = totalAmount.multiply(BigDecimal.valueOf(config[1]))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            allocateProducts(allocation, bondProducts, bondAmount, config[1]);
        }

        // 股票分配
        if (!stockProducts.isEmpty()) {
            BigDecimal stockAmount = totalAmount.multiply(BigDecimal.valueOf(config[2]))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            allocateProducts(allocation, stockProducts, stockAmount, config[2]);
        }

        return allocation;
    }

    // 私有辅助方法

    private User getUserById(Long userId) {
        // 这里应该注入UserRepository，暂时使用模拟数据
        User user = new User();
        user.setId(userId);
        user.setRiskLevel(User.RiskLevel.MODERATE); // 默认中等风险
        user.setInvestAmount(BigDecimal.valueOf(100000)); // 默认投资金额
        return user;
    }

    private User.RiskLevel getUserRiskLevel(User user) {
        return user.getRiskLevel() != null ? user.getRiskLevel() : User.RiskLevel.MODERATE;
    }

    private List<Product> getSuitableProducts(User.RiskLevel riskLevel) {
        return productRepository.findSuitableProductsByRiskLevel(riskLevel);
    }

    private PortfolioAllocation generatePortfolioAllocation(User.RiskLevel riskLevel, List<Product> products) {
        // 使用默认投资金额
        BigDecimal totalAmount = BigDecimal.valueOf(100000);
        return optimizePortfolio(riskLevel, totalAmount, products);
    }

    private void allocateProducts(PortfolioAllocation allocation, List<Product> products,
                                BigDecimal amount, int percentage) {
        if (products.isEmpty() || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        // 按预期收益率排序，选择最佳产品
        products.sort((p1, p2) -> p2.getExpectedReturn().compareTo(p1.getExpectedReturn()));

        // 选择前3个产品进行分配
        int productCount = Math.min(3, products.size());
        BigDecimal amountPerProduct = amount.divide(BigDecimal.valueOf(productCount), 2, RoundingMode.HALF_UP);

        for (int i = 0; i < productCount; i++) {
            Product product = products.get(i);
            BigDecimal actualAmount = (i == productCount - 1) ?
                    amount.subtract(amountPerProduct.multiply(BigDecimal.valueOf(i))) :
                    amountPerProduct;

            allocation.addProduct(product.getId(), actualAmount, percentage / (double)productCount);
        }
    }

    private JsonNode generateLLMSuggestion(User user, User.RiskLevel riskLevel, PortfolioAllocation allocation) {
        Map<String, Object> suggestion = new HashMap<>();
        suggestion.put("risk_level", riskLevel.toString());
        suggestion.put("total_amount", user.getInvestAmount());
        suggestion.put("expected_return", calculateExpectedReturn(allocation));
        suggestion.put("risk_assessment", generateRiskAssessment(riskLevel));
        suggestion.put("recommendation_reason", generateRecommendationReason(riskLevel, allocation));

        return objectMapper.valueToTree(suggestion);
    }

    private BigDecimal calculateExpectedReturn(PortfolioAllocation allocation) {
        // 简化的预期收益率计算
        return BigDecimal.valueOf(8.5); // 默认年化收益率
    }

    private String generateRiskAssessment(User.RiskLevel riskLevel) {
        return switch (riskLevel) {
            case CONSERVATIVE -> "您的风险偏好较为保守，建议投资稳健型产品，重点关注本金安全。";
            case MODERATE -> "您的风险偏好适中，建议平衡型投资组合，既保证收益又控制风险。";
            case AGGRESSIVE -> "您的风险偏好较为激进，建议积极型投资组合，追求较高收益。";
        };
    }

    private String generateRecommendationReason(User.RiskLevel riskLevel, PortfolioAllocation allocation) {
        return String.format("根据您的风险评估结果(%s)，我们为您推荐了%d个优质产品，" +
                "组合预期年化收益率为%.2f%%，符合您的风险偏好和投资目标。",
                riskLevel, allocation.getProductIds().size(), calculateExpectedReturn(allocation));
    }

    /**
     * 投资组合分配内部类
     */
    public static class PortfolioAllocation {
        private List<Long> productIds = new ArrayList<>();
        private List<BigDecimal> allocations = new ArrayList<>();
        private BigDecimal totalAmount = BigDecimal.ZERO;

        public void addProduct(Long productId, BigDecimal amount, double percentage) {
            productIds.add(productId);
            allocations.add(amount);
            totalAmount = totalAmount.add(amount);
        }

        public List<Long> getProductIds() {
            return productIds;
        }

        public List<BigDecimal> getAllocations() {
            return allocations;
        }

        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
    }
}
