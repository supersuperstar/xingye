package com.xingye.bankrisk.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

/**
 * 工单实体类（审核流程）
 * 对应数据库中的 work_orders 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "work_orders")
public class WorkOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 客户ID
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * 当前处理人ID
     */
    @Column(name = "reviewer_id")
    private Long reviewerId;

    /**
     * 工单状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false,
            columnDefinition = "ENUM('PENDING_JUNIOR','PENDING_MID','PENDING_SENIOR','PENDING_COMMITTEE','APPROVED','REJECTED') DEFAULT 'PENDING_JUNIOR'")
    private WorkOrderStatus status;

    /**
     * 优先级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", columnDefinition = "ENUM('LOW','MEDIUM','HIGH','CRITICAL') DEFAULT 'MEDIUM'")
    private Priority priority;

    /**
     * 系统/人工建议说明
     */
    @Column(name = "advise", columnDefinition = "TEXT")
    private String advise;

    /**
     * SLA截止时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "sla_deadline")
    private LocalDateTime slaDeadline;

    /**
     * 风险评分
     */
    @Column(name = "risk_score")
    private Integer riskScore;

    /**
     * 风险类别
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category", columnDefinition = "ENUM('conservative','moderate','aggressive')")
    private User.RiskLevel riskCategory;

    /**
     * 用户选择的组合ID
     */
    @Column(name = "user_choice")
    private Long userChoice;

    // 四级审核意见字段
    /**
     * 初级审核员ID
     */
    @Column(name = "junior_reviewer_id")
    private Long juniorReviewerId;

    /**
     * 初级审核意见
     */
    @Column(name = "junior_comment", columnDefinition = "TEXT")
    private String juniorComment;

    /**
     * 初级审核提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "junior_commit_time")
    private LocalDateTime juniorCommitTime;

    /**
     * 中级审核员ID
     */
    @Column(name = "mid_reviewer_id")
    private Long midReviewerId;

    /**
     * 中级审核意见
     */
    @Column(name = "mid_comment", columnDefinition = "TEXT")
    private String midComment;

    /**
     * 中级审核提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "mid_commit_time")
    private LocalDateTime midCommitTime;

    /**
     * 高级审核员ID
     */
    @Column(name = "senior_reviewer_id")
    private Long seniorReviewerId;

    /**
     * 高级审核意见
     */
    @Column(name = "senior_comment", columnDefinition = "TEXT")
    private String seniorComment;

    /**
     * 高级审核提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "senior_commit_time")
    private LocalDateTime seniorCommitTime;

    /**
     * 委员会审核员ID
     */
    @Column(name = "committee_reviewer_id")
    private Long committeeReviewerId;

    /**
     * 委员会审核意见
     */
    @Column(name = "committee_comment", columnDefinition = "TEXT")
    private String committeeComment;

    /**
     * 委员会审核提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "committee_commit_time")
    private LocalDateTime committeeCommitTime;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "updated_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    /**
     * 工单状态枚举
     */
    public enum WorkOrderStatus {
        PENDING_JUNIOR,    // 待初级审核
        PENDING_MID,       // 待中级审核
        PENDING_SENIOR,    // 待高级审核
        PENDING_COMMITTEE, // 待委员会审核
        APPROVED,          // 已通过
        REJECTED           // 已拒绝
    }

    /**
     * 优先级枚举
     */
    public enum Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (status == null) {
            status = WorkOrderStatus.PENDING_JUNIOR;
        }
        if (priority == null) {
            priority = Priority.MEDIUM;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 获取当前审核阶段
     */
    public WorkflowStage getCurrentStage() {
        return switch (status) {
            case PENDING_JUNIOR -> WorkflowStage.JUNIOR;
            case PENDING_MID -> WorkflowStage.MID;
            case PENDING_SENIOR -> WorkflowStage.SENIOR;
            case PENDING_COMMITTEE -> WorkflowStage.COMMITTEE;
            default -> null;
        };
    }

    /**
     * 工作流阶段枚举
     */
    public enum WorkflowStage {
        JUNIOR, MID, SENIOR, COMMITTEE
    }
}
