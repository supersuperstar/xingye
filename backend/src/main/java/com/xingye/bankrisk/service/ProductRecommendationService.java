package com.xingye.bankrisk.service;

import com.xingye.bankrisk.entity.Product;
import com.xingye.bankrisk.entity.ProductRating;
import com.xingye.bankrisk.entity.ProductTag;
import com.xingye.bankrisk.entity.User;
import com.xingye.bankrisk.repository.ProductRatingRepository;
import com.xingye.bankrisk.repository.ProductRepository;
import com.xingye.bankrisk.repository.ProductTagRelationRepository;
import com.xingye.bankrisk.repository.ProductTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 产品推荐服务类
 * 基于用户风险评分和偏好进行个性化产品推荐
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductRecommendationService {

    private final ProductRepository productRepository;
    private final ProductRatingRepository productRatingRepository;
    private final ProductTagRepository productTagRepository;
    private final ProductTagRelationRepository productTagRelationRepository;

    // 推荐算法权重配置
    private static final double RETURN_WEIGHT = 0.25;
    private static final double RISK_WEIGHT = 0.30;
    private static final double SHARPE_WEIGHT = 0.25;
    private static final double RATING_WEIGHT = 0.20;

    /**
     * 根据用户得分推荐产品组合
     */
    public ProductRecommendationResult recommendProducts(int userScore, User.RiskLevel riskLevel,
                                                        BigDecimal investAmount, Map<String, String> preferences) {
        log.info("[INFO]ProductRecommendationService::recommendProducts: 开始个性化产品推荐 - Score: {}, RiskLevel: {}, Amount: {}",
                userScore, riskLevel, investAmount);

        // 1. 确定推荐策略
        RecommendationStrategy strategy = determineStrategy(userScore, riskLevel, preferences);

        // 2. 获取候选产品
        List<Product> candidateProducts = getCandidateProducts(strategy, investAmount);

        // 3. 评分和排序产品
        List<ScoredProduct> scoredProducts = scoreAndRankProducts(candidateProducts, userScore, riskLevel);

        // 4. 生成投资组合
        PortfolioRecommendation portfolio = generatePortfolio(scoredProducts, strategy, investAmount);

        // 5. 创建推荐结果
        ProductRecommendationResult result = ProductRecommendationResult.builder()
                .strategy(strategy)
                .recommendedProducts(scoredProducts)
                .portfolio(portfolio)
                .explanation(generateExplanation(strategy, userScore, riskLevel))
                .build();

        log.info("[INFO]ProductRecommendationService::recommendProducts: 产品推荐完成 - 推荐产品数量: {}",
                scoredProducts.size());
        return result;
    }

    /**
     * 根据用户得分确定推荐策略
     */
    private RecommendationStrategy determineStrategy(int userScore, User.RiskLevel riskLevel,
                                                   Map<String, String> preferences) {
        RecommendationStrategy strategy = new RecommendationStrategy();
        strategy.setRiskLevel(riskLevel);

        // 根据用户得分调整策略参数
        if (userScore < 35) {
            // 保守型投资者
            strategy.setConservativeRatio(0.70);
            strategy.setBalancedRatio(0.25);
            strategy.setAggressiveRatio(0.05);
            strategy.setMinSharpeRatio(BigDecimal.valueOf(2.0));
            strategy.setMaxVolatility(BigDecimal.valueOf(8.0));
        } else if (userScore < 65) {
            // 稳健型投资者
            strategy.setConservativeRatio(0.30);
            strategy.setBalancedRatio(0.50);
            strategy.setAggressiveRatio(0.20);
            strategy.setMinSharpeRatio(BigDecimal.valueOf(2.5));
            strategy.setMaxVolatility(BigDecimal.valueOf(12.0));
        } else {
            // 激进型投资者
            strategy.setConservativeRatio(0.10);
            strategy.setBalancedRatio(0.30);
            strategy.setAggressiveRatio(0.60);
            strategy.setMinSharpeRatio(BigDecimal.valueOf(3.0));
            strategy.setMaxVolatility(BigDecimal.valueOf(20.0));
        }

        // 根据偏好调整策略
        if (preferences != null) {
            String liquidity = preferences.get("liquidity");
            if ("high".equals(liquidity)) {
                strategy.setMinLiquidityScore(8);
            }

            String investmentPeriod = preferences.get("investment_period");
            if ("short".equals(investmentPeriod)) {
                strategy.setConservativeRatio(strategy.getConservativeRatio() + 0.2);
                strategy.setAggressiveRatio(Math.max(0, strategy.getAggressiveRatio() - 0.2));
            }
        }

        return strategy;
    }

    /**
     * 获取候选产品
     */
    private List<Product> getCandidateProducts(RecommendationStrategy strategy, BigDecimal investAmount) {
        List<Product> candidates = new ArrayList<>();

        // 获取保守型产品
        List<Product> conservativeProducts = getProductsByRiskProfile(
                User.RiskLevel.CONSERVATIVE, strategy, investAmount);
        candidates.addAll(conservativeProducts);

        // 获取平衡型产品
        List<Product> moderateProducts = getProductsByRiskProfile(
                User.RiskLevel.MODERATE, strategy, investAmount);
        candidates.addAll(moderateProducts);

        // 获取激进型产品
        List<Product> aggressiveProducts = getProductsByRiskProfile(
                User.RiskLevel.AGGRESSIVE, strategy, investAmount);
        candidates.addAll(aggressiveProducts);

        // 去重并返回
        return candidates.stream()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 根据风险特征获取产品
     */
    private List<Product> getProductsByRiskProfile(User.RiskLevel riskLevel, RecommendationStrategy strategy,
                                                 BigDecimal investAmount) {
        return productRepository.findBestProductsByRiskLevel(riskLevel).stream()
                .filter(product -> {
                    // 过滤条件
                    if (product.getSharpeRatio() != null &&
                        product.getSharpeRatio().compareTo(strategy.getMinSharpeRatio()) < 0) {
                        return false;
                    }
                    if (product.getExpectedVolatility() != null &&
                        product.getExpectedVolatility().compareTo(strategy.getMaxVolatility()) > 0) {
                        return false;
                    }
                    if (product.getMinimumInvestment() != null &&
                        investAmount.compareTo(product.getMinimumInvestment()) < 0) {
                        return false;
                    }
                    if (product.getLiquidityScore() != null && strategy.getMinLiquidityScore() != null &&
                        product.getLiquidityScore() < strategy.getMinLiquidityScore()) {
                        return false;
                    }
                    return true;
                })
                .limit(10) // 每个风险等级最多10个产品
                .collect(Collectors.toList());
    }

    /**
     * 评分和排序产品
     */
    private List<ScoredProduct> scoreAndRankProducts(List<Product> products, int userScore, User.RiskLevel riskLevel) {
        return products.stream()
                .map(product -> {
                    double score = calculateProductScore(product, userScore, riskLevel);
                    return new ScoredProduct(product, score);
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .collect(Collectors.toList());
    }

    /**
     * 计算产品评分
     */
    private double calculateProductScore(Product product, int userScore, User.RiskLevel riskLevel) {
        double score = 0;

        // 收益率评分（归一化处理）
        if (product.getExpectedReturn() != null) {
            double normalizedReturn = normalizeValue(product.getExpectedReturn().doubleValue(), 0, 25);
            score += normalizedReturn * RETURN_WEIGHT;
        }

        // 风险评分（波动率越低评分越高）
        if (product.getExpectedVolatility() != null) {
            double riskScore = 1.0 - normalizeValue(product.getExpectedVolatility().doubleValue(), 0, 30);
            score += riskScore * RISK_WEIGHT;
        }

        // 夏普比率评分
        if (product.getSharpeRatio() != null) {
            double sharpeScore = normalizeValue(product.getSharpeRatio().doubleValue(), 0, 10);
            score += sharpeScore * SHARPE_WEIGHT;
        }

        // 产品评级评分
        Optional<BigDecimal> rating = productRatingRepository.findAverageRatingByProductId(product.getId());
        if (rating.isPresent()) {
            double ratingScore = normalizeValue(rating.get().doubleValue(), 0, 10);
            score += ratingScore * RATING_WEIGHT;
        }

        // 根据用户风险偏好调整评分
        double riskAdjustment = calculateRiskAdjustment(product, riskLevel);
        score *= riskAdjustment;

        return score;
    }

    /**
     * 归一化数值到0-1区间
     */
    private double normalizeValue(double value, double min, double max) {
        if (max == min) return 0.5;
        return Math.max(0, Math.min(1, (value - min) / (max - min)));
    }

    /**
     * 计算风险调整因子
     */
    private double calculateRiskAdjustment(Product product, User.RiskLevel userRiskLevel) {
        if (product.getRiskLevel() == userRiskLevel) {
            return 1.2; // 完全匹配 +20%
        } else if (isAdjacentRiskLevel(product.getRiskLevel(), userRiskLevel)) {
            return 1.0; // 相邻等级 +0%
        } else {
            return 0.8; // 不匹配 -20%
        }
    }

    /**
     * 判断是否为相邻风险等级
     */
    private boolean isAdjacentRiskLevel(User.RiskLevel productRisk, User.RiskLevel userRisk) {
        Map<User.RiskLevel, Integer> riskOrder = Map.of(
                User.RiskLevel.CONSERVATIVE, 1,
                User.RiskLevel.MODERATE, 2,
                User.RiskLevel.AGGRESSIVE, 3
        );

        int productOrder = riskOrder.get(productRisk);
        int userOrder = riskOrder.get(userRisk);

        return Math.abs(productOrder - userOrder) == 1;
    }

    /**
     * 生成投资组合
     */
    private PortfolioRecommendation generatePortfolio(List<ScoredProduct> scoredProducts,
                                                   RecommendationStrategy strategy, BigDecimal totalAmount) {

        PortfolioRecommendation portfolio = new PortfolioRecommendation();
        List<PortfolioItem> items = new ArrayList<>();

        // 计算各类产品的目标金额
        BigDecimal conservativeAmount = totalAmount.multiply(BigDecimal.valueOf(strategy.getConservativeRatio()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal balancedAmount = totalAmount.multiply(BigDecimal.valueOf(strategy.getBalancedRatio()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal aggressiveAmount = totalAmount.multiply(BigDecimal.valueOf(strategy.getAggressiveRatio()))
                .setScale(2, RoundingMode.HALF_UP);

        // 分配保守型产品
        List<ScoredProduct> conservativeProducts = scoredProducts.stream()
                .filter(sp -> sp.getProduct().getRiskLevel() == User.RiskLevel.CONSERVATIVE)
                .limit(3)
                .collect(Collectors.toList());
        allocateAmountToProducts(items, conservativeProducts, conservativeAmount);

        // 分配平衡型产品
        List<ScoredProduct> balancedProducts = scoredProducts.stream()
                .filter(sp -> sp.getProduct().getRiskLevel() == User.RiskLevel.MODERATE)
                .limit(3)
                .collect(Collectors.toList());
        allocateAmountToProducts(items, balancedProducts, balancedAmount);

        // 分配激进型产品
        List<ScoredProduct> aggressiveProducts = scoredProducts.stream()
                .filter(sp -> sp.getProduct().getRiskLevel() == User.RiskLevel.AGGRESSIVE)
                .limit(3)
                .collect(Collectors.toList());
        allocateAmountToProducts(items, aggressiveProducts, aggressiveAmount);

        portfolio.setItems(items);
        portfolio.setTotalAmount(totalAmount);
        portfolio.setExpectedReturn(calculatePortfolioReturn(items));
        portfolio.setExpectedRisk(calculatePortfolioRisk(items));

        return portfolio;
    }

    /**
     * 分配金额到产品
     */
    private void allocateAmountToProducts(List<PortfolioItem> items, List<ScoredProduct> products,
                                        BigDecimal totalAmount) {
        if (products.isEmpty() || totalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return;
        }

        BigDecimal amountPerProduct = totalAmount.divide(
                BigDecimal.valueOf(products.size()), 2, RoundingMode.HALF_UP);

        for (int i = 0; i < products.size(); i++) {
            ScoredProduct scoredProduct = products.get(i);
            BigDecimal allocatedAmount = (i == products.size() - 1) ?
                    totalAmount.subtract(amountPerProduct.multiply(BigDecimal.valueOf(i))) :
                    amountPerProduct;

            BigDecimal percentage = allocatedAmount.divide(totalAmount, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));

            items.add(PortfolioItem.builder()
                    .product(scoredProduct.getProduct())
                    .amount(allocatedAmount)
                    .percentage(percentage)
                    .build());
        }
    }

    /**
     * 计算组合预期收益率
     */
    private BigDecimal calculatePortfolioReturn(List<PortfolioItem> items) {
        BigDecimal totalReturn = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (PortfolioItem item : items) {
            if (item.getProduct().getExpectedReturn() != null) {
                BigDecimal weight = item.getAmount();
                totalReturn = totalReturn.add(
                        item.getProduct().getExpectedReturn()
                                .multiply(weight)
                                .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP)
                );
                totalWeight = totalWeight.add(weight);
            }
        }

        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            return totalReturn.divide(totalWeight, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return BigDecimal.ZERO;
    }

    /**
     * 计算组合预期风险
     */
    private BigDecimal calculatePortfolioRisk(List<PortfolioItem> items) {
        // 简化的风险计算（实际应该考虑相关性）
        BigDecimal totalRisk = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (PortfolioItem item : items) {
            if (item.getProduct().getExpectedVolatility() != null) {
                BigDecimal weight = item.getAmount();
                totalRisk = totalRisk.add(
                        item.getProduct().getExpectedVolatility()
                                .multiply(weight)
                );
                totalWeight = totalWeight.add(weight);
            }
        }

        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            return totalRisk.divide(totalWeight, 2, RoundingMode.HALF_UP);
        }

        return BigDecimal.ZERO;
    }

    /**
     * 生成推荐解释
     */
    private String generateExplanation(RecommendationStrategy strategy, int userScore, User.RiskLevel riskLevel) {
        StringBuilder explanation = new StringBuilder();
        explanation.append("根据您的风险评分(").append(userScore).append("/100)和")
                .append(riskLevel.toString()).append("风险偏好，");

        if (strategy.getConservativeRatio() > 0.5) {
            explanation.append("为您推荐以保守型产品为主的组合策略，");
        } else if (strategy.getAggressiveRatio() > 0.5) {
            explanation.append("为您推荐以激进型产品为主的组合策略，");
        } else {
            explanation.append("为您推荐均衡配置的组合策略，");
        }

        explanation.append("以实现风险与收益的最佳平衡。");
        return explanation.toString();
    }

    // 内部类定义

    /**
     * 推荐策略
     */
    @lombok.Data
    @lombok.Builder
    public static class RecommendationStrategy {
        private User.RiskLevel riskLevel;
        private double conservativeRatio;
        private double balancedRatio;
        private double aggressiveRatio;
        private BigDecimal minSharpeRatio;
        private BigDecimal maxVolatility;
        private Integer minLiquidityScore;
    }

    /**
     * 评分后的产品
     */
    @lombok.Data
    @lombok.Builder
    public static class ScoredProduct {
        private Product product;
        private double score;
    }

    /**
     * 投资组合
     */
    @lombok.Data
    @lombok.Builder
    public static class PortfolioRecommendation {
        private List<PortfolioItem> items;
        private BigDecimal totalAmount;
        private BigDecimal expectedReturn;
        private BigDecimal expectedRisk;
    }

    /**
     * 组合项目
     */
    @lombok.Data
    @lombok.Builder
    public static class PortfolioItem {
        private Product product;
        private BigDecimal amount;
        private BigDecimal percentage;
    }

    /**
     * 推荐结果
     */
    @lombok.Data
    @lombok.Builder
    public static class ProductRecommendationResult {
        private RecommendationStrategy strategy;
        private List<ScoredProduct> recommendedProducts;
        private PortfolioRecommendation portfolio;
        private String explanation;
    }
}
