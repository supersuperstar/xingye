package com.xingye.bankrisk.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 对应数据库中的 users 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户账号（登录名）
     */
    @Column(name = "account", nullable = false, unique = true, length = 64)
    private String account;

    /**
     * 密码哈希
     */
    @Column(name = "key_hash", nullable = false, length = 255)
    @JsonIgnore
    private String keyHash;

    /**
     * 用户姓名
     */
    @Column(name = "name", nullable = false, length = 128)
    private String name;

    /**
     * 手机号
     */
    @Column(name = "telephone", length = 32)
    private String telephone;

    /**
     * 身份证号（需脱敏/加密存储）
     */
    @Column(name = "nuid", unique = true, length = 32)
    private String nuid;

    /**
     * 邮箱
     */
    @Column(name = "email", length = 160)
    private String email;

    /**
     * 职业
     */
    @Column(name = "occupation", length = 128)
    private String occupation;

    /**
     * 投资金额
     */
    @Column(name = "invest_amount", precision = 16, scale = 2, columnDefinition = "DECIMAL(16,2) DEFAULT 0")
    private BigDecimal investAmount;

    /**
     * 用户状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('ACTIVE','LOCKED','DELETED') DEFAULT 'ACTIVE'")
    private UserStatus status;

    /**
     * 评估时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "evaluation_time", columnDefinition = "DATETIME DEFAULT '2200-01-01 00:00:00'")
    private LocalDateTime evaluationTime;

    /**
     * 风险等级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", columnDefinition = "ENUM('conservative','moderate','aggressive')")
    private RiskLevel riskLevel;

    /**
     * 最新问卷ID
     */
    @Column(name = "latest_questionnaire_id")
    private Long latestQuestionnaireId;

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
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE, LOCKED, DELETED
    }

    /**
     * 风险等级枚举
     */
    public enum RiskLevel {
        CONSERVATIVE, MODERATE, AGGRESSIVE
    }

    /**
     * 用户角色枚举（用于权限管理）
     */
    public enum UserRole {
        CUSTOMER,           // 普通客户
        AUDITOR_JUNIOR,     // 初级审核员
        AUDITOR_MID,        // 中级审核员
        AUDITOR_SENIOR,     // 高级审核员
        INVEST_COMMITTEE,   // 投资委员会成员
        ADMIN               // 管理员
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
            status = UserStatus.ACTIVE;
        }
        if (evaluationTime == null) {
            evaluationTime = LocalDateTime.of(2200, 1, 1, 0, 0, 0);
        }
        if (investAmount == null) {
            investAmount = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
