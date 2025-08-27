package com.xingye.bankrisk.repository;

import com.xingye.bankrisk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 用户数据访问层
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据账号查找用户
     */
    Optional<User> findByAccount(String account);

    /**
     * 根据手机号查找用户
     */
    Optional<User> findByTelephone(String telephone);

    /**
     * 根据身份证号查找用户
     */
    Optional<User> findByNuid(String nuid);

    /**
     * 根据邮箱查找用户
     */
    Optional<User> findByEmail(String email);

    /**
     * 根据状态查找用户列表
     */
    List<User> findByStatus(User.UserStatus status);

    /**
     * 根据风险等级查找用户列表
     */
    List<User> findByRiskLevel(User.RiskLevel riskLevel);

    /**
     * 查找活跃用户（状态为ACTIVE）
     */
    @Query("SELECT u FROM User u WHERE u.status = 'ACTIVE'")
    List<User> findActiveUsers();

    /**
     * 根据姓名模糊查询
     */
    @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
    List<User> findByNameLike(@Param("name") String name);

    /**
     * 查找指定时间之后评估的用户
     */
    @Query("SELECT u FROM User u WHERE u.evaluationTime > :evaluationTime")
    List<User> findUsersEvaluatedAfter(@Param("evaluationTime") LocalDateTime evaluationTime);

    /**
     * 统计各风险等级用户数量
     */
    @Query("SELECT u.riskLevel, COUNT(u) FROM User u GROUP BY u.riskLevel")
    List<Object[]> countUsersByRiskLevel();

    /**
     * 查找有最新问卷的用户
     */
    @Query("SELECT u FROM User u WHERE u.latestQuestionnaireId IS NOT NULL")
    List<User> findUsersWithLatestQuestionnaire();

    /**
     * 根据投资金额范围查找用户
     */
    @Query("SELECT u FROM User u WHERE u.investAmount BETWEEN :minAmount AND :maxAmount")
    List<User> findUsersByInvestAmountRange(@Param("minAmount") java.math.BigDecimal minAmount,
                                          @Param("maxAmount") java.math.BigDecimal maxAmount);

    /**
     * 检查账号是否存在
     */
    boolean existsByAccount(String account);

    /**
     * 检查手机号是否存在
     */
    boolean existsByTelephone(String telephone);

    /**
     * 检查身份证号是否存在
     */
    boolean existsByNuid(String nuid);
}
