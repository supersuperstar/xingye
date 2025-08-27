package com.xingye.bankrisk.repository;

import com.xingye.bankrisk.entity.ProductRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 产品评分数据访问层
 */
@Repository
public interface ProductRatingRepository extends JpaRepository<ProductRating, Long> {

    /**
     * 根据产品ID查找所有评分
     */
    List<ProductRating> findByProductIdOrderByRatingDateDesc(Long productId);

    /**
     * 根据产品ID和日期查找评分
     */
    Optional<ProductRating> findByProductIdAndRatingDate(Long productId, LocalDate ratingDate);

    /**
     * 获取产品的最新评分
     */
    @Query("SELECT pr FROM ProductRating pr WHERE pr.productId = :productId ORDER BY pr.ratingDate DESC LIMIT 1")
    Optional<ProductRating> findLatestRatingByProductId(@Param("productId") Long productId);

    /**
     * 根据评级机构查找评分
     */
    List<ProductRating> findByRatingAgency(String ratingAgency);

    /**
     * 根据综合评分范围查找评分
     */
    @Query("SELECT pr FROM ProductRating pr WHERE pr.overallRating BETWEEN :minRating AND :maxRating")
    List<ProductRating> findByOverallRatingRange(@Param("minRating") java.math.BigDecimal minRating,
                                                @Param("maxRating") java.math.BigDecimal maxRating);

    /**
     * 获取评分最高的N个产品
     */
    @Query("SELECT pr FROM ProductRating pr WHERE pr.ratingDate = " +
           "(SELECT MAX(pr2.ratingDate) FROM ProductRating pr2 WHERE pr2.productId = pr.productId) " +
           "ORDER BY pr.overallRating DESC")
    List<ProductRating> findTopRatedProducts();

    /**
     * 获取指定日期范围内的评分
     */
    @Query("SELECT pr FROM ProductRating pr WHERE pr.ratingDate BETWEEN :startDate AND :endDate")
    List<ProductRating> findByRatingDateBetween(@Param("startDate") LocalDate startDate,
                                               @Param("endDate") LocalDate endDate);

    /**
     * 统计各评级机构的评分数量
     */
    @Query("SELECT pr.ratingAgency, COUNT(pr) FROM ProductRating pr GROUP BY pr.ratingAgency")
    List<Object[]> countRatingsByAgency();

    /**
     * 获取产品的平均评分
     */
    @Query("SELECT AVG(pr.overallRating) FROM ProductRating pr WHERE pr.productId = :productId")
    Optional<java.math.BigDecimal> findAverageRatingByProductId(@Param("productId") Long productId);

    /**
     * 查找高于指定评分的最新评分
     */
    @Query("SELECT pr FROM ProductRating pr WHERE pr.overallRating >= :minRating " +
           "AND pr.ratingDate = (SELECT MAX(pr2.ratingDate) FROM ProductRating pr2 WHERE pr2.productId = pr.productId)")
    List<ProductRating> findLatestHighRatedProducts(@Param("minRating") java.math.BigDecimal minRating);
}
