package com.xingye.bankrisk.repository;

import com.xingye.bankrisk.entity.Questionnaire;
import com.xingye.bankrisk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 问卷数据访问层
 */
@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {

    /**
     * 根据用户ID查找所有问卷
     */
    List<Questionnaire> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 查找用户的最新问卷
     */
    @Query("SELECT q FROM Questionnaire q WHERE q.userId = :userId AND q.isLatest = true")
    Optional<Questionnaire> findLatestByUserId(@Param("userId") Long userId);

    /**
     * 根据风险等级查找问卷
     */
    List<Questionnaire> findByStatus(User.RiskLevel status);

    /**
     * 根据分数范围查找问卷
     */
    @Query("SELECT q FROM Questionnaire q WHERE q.score BETWEEN :minScore AND :maxScore")
    List<Questionnaire> findByScoreRange(@Param("minScore") Integer minScore,
                                        @Param("maxScore") Integer maxScore);

    /**
     * 查找指定时间之后创建的问卷
     */
    List<Questionnaire> findByCreatedAtAfter(LocalDateTime createdAt);

    /**
     * 根据年龄范围查找问卷
     */
    @Query("SELECT q FROM Questionnaire q WHERE q.age BETWEEN :minAge AND :maxAge")
    List<Questionnaire> findByAgeRange(@Param("minAge") Integer minAge,
                                      @Param("maxAge") Integer maxAge);

    /**
     * 根据年收入范围查找问卷
     */
    @Query("SELECT q FROM Questionnaire q WHERE q.annual BETWEEN :minAnnual AND :maxAnnual")
    List<Questionnaire> findByAnnualRange(@Param("minAnnual") java.math.BigDecimal minAnnual,
                                         @Param("maxAnnual") java.math.BigDecimal maxAnnual);

    /**
     * 统计各风险等级问卷数量
     */
    @Query("SELECT q.status, COUNT(q) FROM Questionnaire q GROUP BY q.status")
    List<Object[]> countQuestionnairesByStatus();

    /**
     * 查找最新的问卷（按用户分组）
     */
    @Query("SELECT q FROM Questionnaire q WHERE q.id IN " +
           "(SELECT MAX(q2.id) FROM Questionnaire q2 GROUP BY q2.userId)")
    List<Questionnaire> findLatestQuestionnaires();

    /**
     * 根据投资年限范围查找问卷
     */
    @Query("SELECT q FROM Questionnaire q WHERE q.investTime BETWEEN :minYears AND :maxYears")
    List<Questionnaire> findByInvestTimeRange(@Param("minYears") Integer minYears,
                                             @Param("maxYears") Integer maxYears);

    /**
     * 根据最大亏损承受比例范围查找问卷
     */
    @Query("SELECT q FROM Questionnaire q WHERE q.maxLoss BETWEEN :minLoss AND :maxLoss")
    List<Questionnaire> findByMaxLossRange(@Param("minLoss") java.math.BigDecimal minLoss,
                                          @Param("maxLoss") java.math.BigDecimal maxLoss);

    /**
     * 查找指定提交时间范围内的问卷
     */
    @Query("SELECT q FROM Questionnaire q WHERE q.ctime BETWEEN :startTime AND :endTime")
    List<Questionnaire> findByCtimeBetween(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 统计问卷总数
     */
    @Query("SELECT COUNT(q) FROM Questionnaire q")
    Long countAllQuestionnaires();

    /**
     * 统计今日提交的问卷数
     */
    @Query("SELECT COUNT(q) FROM Questionnaire q WHERE DATE(q.createdAt) = CURRENT_DATE")
    Long countTodayQuestionnaires();
}
