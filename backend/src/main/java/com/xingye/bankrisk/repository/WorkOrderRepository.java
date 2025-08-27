package com.xingye.bankrisk.repository;

import com.xingye.bankrisk.entity.User;
import com.xingye.bankrisk.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 工单数据访问层
 */
@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {

    /**
     * 根据客户ID查找工单
     */
    List<WorkOrder> findByCustomerIdOrderByCreatedAtDesc(Long customerId);

    /**
     * 根据审核员ID查找工单
     */
    List<WorkOrder> findByReviewerIdOrderByCreatedAtDesc(Long reviewerId);

    /**
     * 根据状态查找工单
     */
    List<WorkOrder> findByStatus(WorkOrder.WorkOrderStatus status);

    /**
     * 根据优先级查找工单
     */
    List<WorkOrder> findByPriorityOrderByCreatedAtAsc(WorkOrder.Priority priority);

    /**
     * 根据状态和优先级查找工单
     */
    List<WorkOrder> findByStatusAndPriorityOrderByCreatedAtAsc(WorkOrder.WorkOrderStatus status,
                                                             WorkOrder.Priority priority);

    /**
     * 根据风险等级查找工单
     */
    List<WorkOrder> findByRiskCategory(User.RiskLevel riskCategory);

    /**
     * 查找待处理的工单（按优先级和创建时间排序）
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.status IN ('PENDING_JUNIOR', 'PENDING_MID', 'PENDING_SENIOR', 'PENDING_COMMITTEE') " +
           "ORDER BY w.priority DESC, w.createdAt ASC")
    List<WorkOrder> findPendingWorkOrders();

    /**
     * 查找指定审核员的待处理工单
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.reviewerId = :reviewerId AND " +
           "w.status IN ('PENDING_JUNIOR', 'PENDING_MID', 'PENDING_SENIOR', 'PENDING_COMMITTEE')")
    List<WorkOrder> findPendingWorkOrdersByReviewer(@Param("reviewerId") Long reviewerId);

    /**
     * 查找即将到期的工单
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.slaDeadline < :deadline AND " +
           "w.status NOT IN ('APPROVED', 'REJECTED')")
    List<WorkOrder> findWorkOrdersDueBefore(@Param("deadline") LocalDateTime deadline);

    /**
     * 根据风险评分范围查找工单
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.riskScore BETWEEN :minScore AND :maxScore")
    List<WorkOrder> findByRiskScoreRange(@Param("minScore") Integer minScore,
                                        @Param("maxScore") Integer maxScore);

    /**
     * 查找指定时间范围内的工单
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.createdAt BETWEEN :startTime AND :endTime")
    List<WorkOrder> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各状态的工单数量
     */
    @Query("SELECT w.status, COUNT(w) FROM WorkOrder w GROUP BY w.status")
    List<Object[]> countWorkOrdersByStatus();

    /**
     * 统计各优先级的工单数量
     */
    @Query("SELECT w.priority, COUNT(w) FROM WorkOrder w GROUP BY w.priority")
    List<Object[]> countWorkOrdersByPriority();

    /**
     * 查找初级审核员的工单
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.status = 'PENDING_JUNIOR'")
    List<WorkOrder> findJuniorPendingWorkOrders();

    /**
     * 查找中级审核员的工单
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.status = 'PENDING_MID'")
    List<WorkOrder> findMidPendingWorkOrders();

    /**
     * 查找高级审核员的工单
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.status = 'PENDING_SENIOR'")
    List<WorkOrder> findSeniorPendingWorkOrders();

    /**
     * 查找委员会的工单
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.status = 'PENDING_COMMITTEE'")
    List<WorkOrder> findCommitteePendingWorkOrders();

    /**
     * 查找已完成的工单（已通过或已拒绝）
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.status IN ('APPROVED', 'REJECTED')")
    List<WorkOrder> findCompletedWorkOrders();

    /**
     * 根据客户ID查找最新工单
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.customerId = :customerId ORDER BY w.createdAt DESC")
    List<WorkOrder> findLatestWorkOrderByCustomer(@Param("customerId") Long customerId);

    /**
     * 查找超时的工单
     */
    @Query("SELECT w FROM WorkOrder w WHERE w.slaDeadline < CURRENT_TIMESTAMP AND " +
           "w.status NOT IN ('APPROVED', 'REJECTED')")
    List<WorkOrder> findOverdueWorkOrders();

    /**
     * 统计今日创建的工单数
     */
    @Query("SELECT COUNT(w) FROM WorkOrder w WHERE DATE(w.createdAt) = CURRENT_DATE")
    Long countTodayWorkOrders();

    /**
     * 统计本月创建的工单数
     */
    @Query("SELECT COUNT(w) FROM WorkOrder w WHERE YEAR(w.createdAt) = YEAR(CURRENT_DATE) " +
           "AND MONTH(w.createdAt) = MONTH(CURRENT_DATE)")
    Long countMonthWorkOrders();
}
