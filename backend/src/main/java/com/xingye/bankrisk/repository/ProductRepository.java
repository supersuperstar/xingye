package com.xingye.bankrisk.repository;

import com.xingye.bankrisk.entity.Product;
import com.xingye.bankrisk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 投资产品数据访问层
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * 根据产品类型查找产品
     */
    List<Product> findByProductType(Product.ProductType productType);

    /**
     * 根据风险等级查找产品
     */
    List<Product> findByRiskLevel(User.RiskLevel riskLevel);

    /**
     * 查找激活的产品
     */
    List<Product> findByIsActiveTrue();

    /**
     * 根据产品类型和风险等级查找产品
     */
    List<Product> findByProductTypeAndRiskLevel(Product.ProductType productType, User.RiskLevel riskLevel);

    /**
     * 根据产品代码查找产品
     */
    List<Product> findByCode(String code);

    /**
     * 根据预期收益率范围查找产品
     */
    @Query("SELECT p FROM Product p WHERE p.expectedReturn BETWEEN :minReturn AND :maxReturn")
    List<Product> findByExpectedReturnRange(@Param("minReturn") BigDecimal minReturn,
                                           @Param("maxReturn") BigDecimal maxReturn);

    /**
     * 根据风险等级查找激活的产品
     */
    @Query("SELECT p FROM Product p WHERE p.riskLevel = :riskLevel AND p.isActive = true")
    List<Product> findActiveProductsByRiskLevel(@Param("riskLevel") User.RiskLevel riskLevel);

    /**
     * 根据产品类型查找激活的产品
     */
    @Query("SELECT p FROM Product p WHERE p.productType IN :productTypes AND p.isActive = true")
    List<Product> findActiveProductsByTypes(@Param("productTypes") List<Product.ProductType> productTypes);

    /**
     * 查找适合特定风险等级的产品（基于产品类型和风险等级匹配）
     */
    @Query("SELECT p FROM Product p WHERE p.riskLevel = :riskLevel AND p.isActive = true ORDER BY p.expectedReturn ASC")
    List<Product> findSuitableProductsByRiskLevel(@Param("riskLevel") User.RiskLevel riskLevel);

    /**
     * 统计各产品类型的数量
     */
    @Query("SELECT p.productType, COUNT(p) FROM Product p GROUP BY p.productType")
    List<Object[]> countProductsByType();

    /**
     * 统计各风险等级的产品数量
     */
    @Query("SELECT p.riskLevel, COUNT(p) FROM Product p GROUP BY p.riskLevel")
    List<Object[]> countProductsByRiskLevel();

    /**
     * 根据名称模糊查询
     */
    @Query("SELECT p FROM Product p WHERE p.productName LIKE %:name%")
    List<Product> findByNameLike(@Param("name") String name);

    /**
     * 查找预期收益率最高的产品
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.expectedReturn DESC")
    List<Product> findTopProductsByReturn();

    /**
     * 查找预期收益率最低的产品（保守型）
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.expectedReturn ASC")
    List<Product> findConservativeProducts();

    /**
     * 检查产品代码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 根据货币类型查找产品
     */
    List<Product> findByCurrency(String currency);

    /**
     * 查找CNY货币的产品
     */
    @Query("SELECT p FROM Product p WHERE p.currency = 'CNY' AND p.isActive = true")
    List<Product> findCnyProducts();

    /**
     * 根据夏普比率范围查找产品
     */
    @Query("SELECT p FROM Product p WHERE p.sharpeRatio BETWEEN :minSharpe AND :maxSharpe AND p.isActive = true")
    List<Product> findBySharpeRatioRange(@Param("minSharpe") java.math.BigDecimal minSharpe,
                                        @Param("maxSharpe") java.math.BigDecimal maxSharpe);

    /**
     * 根据波动率范围查找产品
     */
    @Query("SELECT p FROM Product p WHERE p.expectedVolatility BETWEEN :minVolatility AND :maxVolatility AND p.isActive = true")
    List<Product> findByVolatilityRange(@Param("minVolatility") java.math.BigDecimal minVolatility,
                                       @Param("maxVolatility") java.math.BigDecimal maxVolatility);

    /**
     * 根据最大回撤范围查找产品
     */
    @Query("SELECT p FROM Product p WHERE p.maxDrawdown BETWEEN :minDrawdown AND :maxDrawdown AND p.isActive = true")
    List<Product> findByMaxDrawdownRange(@Param("minDrawdown") java.math.BigDecimal minDrawdown,
                                        @Param("maxDrawdown") java.math.BigDecimal maxDrawdown);

    /**
     * 根据行业板块查找产品
     */
    List<Product> findBySector(String sector);

    /**
     * 根据市值规模查找产品
     */
    List<Product> findByMarketCap(Product.MarketCap marketCap);

    /**
     * 根据最低投资额范围查找产品
     */
    @Query("SELECT p FROM Product p WHERE p.minimumInvestment BETWEEN :minInvestment AND :maxInvestment AND p.isActive = true")
    List<Product> findByMinimumInvestmentRange(@Param("minInvestment") java.math.BigDecimal minInvestment,
                                             @Param("maxInvestment") java.math.BigDecimal maxInvestment);

    /**
     * 根据流动性评分查找产品
     */
    @Query("SELECT p FROM Product p WHERE p.liquidityScore >= :minScore AND p.isActive = true ORDER BY p.liquidityScore DESC")
    List<Product> findByMinimumLiquidityScore(@Param("minScore") Integer minScore);

    /**
     * 查找高评分产品（基于夏普比率）
     */
    @Query("SELECT p FROM Product p WHERE p.sharpeRatio > :threshold AND p.isActive = true ORDER BY p.sharpeRatio DESC")
    List<Product> findHighSharpeRatioProducts(@Param("threshold") java.math.BigDecimal threshold);

    /**
     * 查找低波动产品
     */
    @Query("SELECT p FROM Product p WHERE p.expectedVolatility < :threshold AND p.isActive = true ORDER BY p.expectedVolatility ASC")
    List<Product> findLowVolatilityProducts(@Param("threshold") java.math.BigDecimal threshold);

    /**
     * 根据综合评分查找最佳产品
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true ORDER BY " +
           "(p.sharpeRatio * 0.4 + (10 - p.expectedVolatility) * 0.3 + p.expectedReturn * 0.3) DESC")
    List<Product> findBestProductsByCompositeScore();

    /**
     * 根据风险等级查找最佳产品（综合评分）
     */
    @Query("SELECT p FROM Product p WHERE p.riskLevel = :riskLevel AND p.isActive = true ORDER BY " +
           "(p.sharpeRatio * 0.4 + (10 - p.expectedVolatility) * 0.3 + p.expectedReturn * 0.3) DESC")
    List<Product> findBestProductsByRiskLevel(@Param("riskLevel") User.RiskLevel riskLevel);

    /**
     * 查找产品（支持多条件筛选）
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true " +
           "AND (:productTypes IS NULL OR p.productType IN :productTypes) " +
           "AND (:riskLevels IS NULL OR p.riskLevel IN :riskLevels) " +
           "AND (:minReturn IS NULL OR p.expectedReturn >= :minReturn) " +
           "AND (:maxReturn IS NULL OR p.expectedReturn <= :maxReturn) " +
           "AND (:sectors IS NULL OR p.sector IN :sectors) " +
           "ORDER BY p.expectedReturn DESC")
    List<Product> findProductsWithFilters(@Param("productTypes") List<Product.ProductType> productTypes,
                                         @Param("riskLevels") List<User.RiskLevel> riskLevels,
                                         @Param("minReturn") java.math.BigDecimal minReturn,
                                         @Param("maxReturn") java.math.BigDecimal maxReturn,
                                         @Param("sectors") List<String> sectors);
}
