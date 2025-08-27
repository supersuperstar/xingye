package com.xingye.bankrisk.repository;

import com.xingye.bankrisk.entity.PortfolioRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 投资组合推荐数据访问层
 */
@Repository
public interface PortfolioRecommendationRepository extends JpaRepository<PortfolioRecommendation, Long> {

    /**
     * 根据用户ID查找投资组合推荐
     */
    List<PortfolioRecommendation> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 根据客户ID查找投资组合推荐
     */
    List<PortfolioRecommendation> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    /**
     * 根据工单ID查找投资组合推荐
     */
    List<PortfolioRecommendation> findByWorkOrderId(Long workOrderId);

    /**
     * 查找指定时间范围内的投资组合推荐
     */
    @Query("SELECT pr FROM PortfolioRecommendation pr WHERE pr.createdAt BETWEEN :startTime AND :endTime")
    List<PortfolioRecommendation> findByCreatedAtBetween(@Param("startTime") java.time.LocalDateTime startTime,
                                                       @Param("endTime") java.time.LocalDateTime endTime);

    /**
     * 根据用户ID查找最新的投资组合推荐
     */
    @Query("SELECT pr FROM PortfolioRecommendation pr WHERE pr.userId = :userId ORDER BY pr.createdAt DESC LIMIT 1")
    PortfolioRecommendation findLatestByUserId(@Param("userId") Long userId);

    /**
     * 统计用户投资组合推荐数量
     */
    @Query("SELECT pr.userId, COUNT(pr) FROM PortfolioRecommendation pr GROUP BY pr.userId")
    List<Object[]> countRecommendationsByUser();



    /**
     * 根据创建时间倒序查找所有投资组合推荐
     */
    List<PortfolioRecommendation> findAllByOrderByCreatedAtDesc();

    /**
     * 查找指定用户的所有投资组合推荐数量
     */
    Long countByUserId(Long userId);

    /**
     * 查找指定客户的所有投资组合推荐数量
     */
    Long countByCustomerId(Long customerId);

    /**
     * 删除指定工单关联的投资组合推荐
     */
    void deleteByWorkOrderId(Long workOrderId);

    /**
     * 查找没有关联工单的投资组合推荐
     */
    @Query("SELECT pr FROM PortfolioRecommendation pr WHERE pr.workOrderId IS NULL")
    List<PortfolioRecommendation> findUnassignedRecommendations();

    /**
     * 统计今日创建的投资组合推荐数
     */
    @Query("SELECT COUNT(pr) FROM PortfolioRecommendation pr WHERE DATE(pr.createdAt) = CURRENT_DATE")
    Long countTodayRecommendations();

    /**
     * 根据产品ID查找包含该产品的投资组合推荐
     * 使用LIKE操作符匹配JSON字符串中的产品ID
     */
    @Query("SELECT pr FROM PortfolioRecommendation pr WHERE pr.productIds LIKE CONCAT('%', :productId, '%')")
    List<PortfolioRecommendation> findByProductId(@Param("productId") String productId);

}
