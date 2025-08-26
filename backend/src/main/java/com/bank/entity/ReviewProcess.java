package com.bank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * Review process entity for managing multi-level review process
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Data
@Entity
@Table(name = "review_processes")
@EqualsAndHashCode(callSuper = true)
public class ReviewProcess extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "assessment_id", nullable = false)
    private Long assessmentId;

    @Column(name = "process_instance_id", length = 100)
    private String processInstanceId;

    @Column(name = "current_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewLevel currentLevel = ReviewLevel.PRIMARY;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PENDING;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.NORMAL;

    @Column(name = "expected_completion_time")
    private LocalDateTime expectedCompletionTime;

    @Column(name = "actual_completion_time")
    private LocalDateTime actualCompletionTime;

    /**
     * Review level enumeration
     */
    public enum ReviewLevel {
        PRIMARY,        // Primary reviewer
        INTERMEDIATE,   // Intermediate reviewer
        SENIOR,         // Senior reviewer
        COMMITTEE       // Investment committee
    }

    /**
     * Review status enumeration
     */
    public enum ReviewStatus {
        PENDING,        // Pending review
        IN_PROGRESS,    // Review in progress
        APPROVED,       // Approved
        REJECTED,       // Rejected
        ESCALATED,      // Escalated to next level
        COMPLETED       // Review completed
    }

    /**
     * Priority enumeration
     */
    public enum Priority {
        LOW,            // Low priority
        NORMAL,         // Normal priority
        HIGH,           // High priority
        URGENT          // Urgent priority
    }
}
