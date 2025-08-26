package com.bank.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ReviewProcess entity - 审核流程实体
 *
 * @author Bank System
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "review_processes")
@EqualsAndHashCode(callSuper = false)
public class ReviewProcess extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 客户ID
     */
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    /**
     * 风险评估ID
     */
    @Column(name = "assessment_id", nullable = false)
    private Long assessmentId;

    /**
     * 流程实例ID
     */
    @Column(name = "process_instance_id", length = 100)
    private String processInstanceId;

    /**
     * 当前审核级别
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "current_level", length = 20)
    private ReviewLevel currentLevel;

    /**
     * 审核状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private ReviewStatus status = ReviewStatus.PENDING;

    /**
     * 优先级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private Priority priority = Priority.NORMAL;

    /**
     * 预计完成时间
     */
    @Column(name = "expected_completion_time")
    private LocalDateTime expectedCompletionTime;

    /**
     * 实际完成时间
     */
    @Column(name = "actual_completion_time")
    private LocalDateTime actualCompletionTime;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 审核级别枚举
     */
    public enum ReviewLevel {
        PRIMARY("初级审核"),
        INTERMEDIATE("中级审核"),
        SENIOR("高级审核"),
        COMMITTEE("投资委员会");

        private final String description;

        ReviewLevel(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 审核状态枚举
     */
    public enum ReviewStatus {
        PENDING("待审核"),
        IN_PROGRESS("审核中"),
        APPROVED("已通过"),
        REJECTED("已拒绝"),
        RETURNED("已退回");

        private final String description;

        ReviewStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * 优先级枚举
     */
    public enum Priority {
        LOW("低"),
        NORMAL("普通"),
        HIGH("高"),
        URGENT("紧急");

        private final String description;

        Priority(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
