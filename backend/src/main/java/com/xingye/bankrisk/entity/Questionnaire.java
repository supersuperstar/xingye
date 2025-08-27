package com.xingye.bankrisk.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 问卷实体类
 * 对应数据库中的 questionnaires 表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "questionnaires")
public class Questionnaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 问卷提交时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "ctime", columnDefinition = "DATETIME DEFAULT '2200-01-01 00:00:00'")
    private LocalDateTime ctime;

    /**
     * 是否为最新问卷
     */
    @Column(name = "is_latest", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isLatest;

    /**
     * 年龄
     */
    @Column(name = "age")
    private Integer age;

    /**
     * 年收入
     */
    @Column(name = "annual", precision = 14, scale = 2)
    private BigDecimal annual;

    /**
     * 投资年限
     */
    @Column(name = "invest_time")
    private Integer investTime;

    /**
     * 最大亏损承受比例（百分比）
     */
    @Column(name = "max_loss", precision = 6, scale = 2)
    private BigDecimal maxLoss;

    /**
     * 投资目标
     */
    @Column(name = "target", length = 64)
    private String target;

    /**
     * 计划投资年限
     */
    @Column(name = "year_for_invest")
    private Integer yearForInvest;

    /**
     * 风险评分
     */
    @Column(name = "score")
    private Integer score;

    /**
     * 风险等级
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "ENUM('conservative','moderate','aggressive')")
    private User.RiskLevel status;

    /**
     * 问卷答案（JSON格式）
     */
    @Column(name = "answers", columnDefinition = "TEXT")
    private String answers;

    /**
     * 评分明细（JSON格式）
     */
    @Column(name = "score_breakdown", columnDefinition = "TEXT")
    private String scoreBreakdown;

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

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (ctime == null) {
            ctime = LocalDateTime.of(2200, 1, 1, 0, 0, 0);
        }
        if (isLatest == null) {
            isLatest = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
