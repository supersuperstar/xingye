package com.bank.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Customer entity - 客户信息实体
 *
 * @author Bank System
 * @since 1.0.0
 */
@Data
@Entity
@Table(name = "customers")
@EqualsAndHashCode(callSuper = false)
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 客户姓名
     */
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    /**
     * 手机号码
     */
    @Column(name = "phone", nullable = false, length = 20, unique = true)
    private String phone;

    /**
     * 身份证号
     */
    @Column(name = "id_card", nullable = false, length = 18, unique = true)
    private String idCard;

    /**
     * 电子邮箱
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 年龄
     */
    @Column(name = "age")
    private Integer age;

    /**
     * 职业
     */
    @Column(name = "occupation", length = 100)
    private String occupation;

    /**
     * 年收入
     */
    @Column(name = "annual_income", precision = 15, scale = 2)
    private BigDecimal annualIncome;

    /**
     * 投资经验（年）
     */
    @Column(name = "investment_experience")
    private Integer investmentExperience;

    /**
     * 客户状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private CustomerStatus status = CustomerStatus.ACTIVE;

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
     * 客户状态枚举
     */
    public enum CustomerStatus {
        ACTIVE("激活"),
        INACTIVE("未激活"),
        SUSPENDED("暂停"),
        CLOSED("关闭");

        private final String description;

        CustomerStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
