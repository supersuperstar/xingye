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
}
