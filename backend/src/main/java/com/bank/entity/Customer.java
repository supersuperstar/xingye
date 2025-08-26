package com.bank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Customer entity for storing customer information
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Data
@Entity
@Table(name = "customers")
@EqualsAndHashCode(callSuper = true)
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "phone", nullable = false, unique = true, length = 20)
    private String phone;

    @Column(name = "id_card", nullable = false, unique = true, length = 18)
    private String idCard;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "age")
    private Integer age;

    @Column(name = "occupation", length = 100)
    private String occupation;

    @Column(name = "annual_income")
    private Double annualIncome;

    @Column(name = "investment_experience", length = 50)
    @Enumerated(EnumType.STRING)
    private InvestmentExperience investmentExperience;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private CustomerStatus status = CustomerStatus.ACTIVE;

    /**
     * Customer status enumeration
     */
    public enum CustomerStatus {
        ACTIVE,     // Active customer
        INACTIVE,   // Inactive customer
        SUSPENDED,  // Suspended customer
        DELETED     // Deleted customer
    }

    /**
     * Investment experience enumeration
     */
    public enum InvestmentExperience {
        NONE,           // No investment experience
        BEGINNER,       // Beginner (less than 1 year)
        INTERMEDIATE,   // Intermediate (1-3 years)
        ADVANCED,       // Advanced (3-5 years)
        EXPERT          // Expert (more than 5 years)
    }
}
