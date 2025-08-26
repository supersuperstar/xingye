package com.bank.repository;

import com.bank.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Customer entity
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by phone number
     */
    Optional<Customer> findByPhone(String phone);

    /**
     * Find customer by ID card number
     */
    Optional<Customer> findByIdCard(String idCard);

    /**
     * Find customer by phone and ID card
     */
    Optional<Customer> findByPhoneAndIdCard(String phone, String idCard);

    /**
     * Check if customer exists by phone number
     */
    boolean existsByPhone(String phone);

    /**
     * Check if customer exists by ID card number
     */
    boolean existsByIdCard(String idCard);

    /**
     * Find customers by status
     */
    List<Customer> findByStatus(Customer.CustomerStatus status);

    /**
     * Find customers by investment experience
     */
    List<Customer> findByInvestmentExperience(Customer.InvestmentExperience experience);

    /**
     * Find customers by age range
     */
    @Query("SELECT c FROM Customer c WHERE c.age BETWEEN :minAge AND :maxAge")
    List<Customer> findByAgeRange(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);

    /**
     * Find customers by annual income range
     */
    @Query("SELECT c FROM Customer c WHERE c.annualIncome BETWEEN :minIncome AND :maxIncome")
    List<Customer> findByIncomeRange(@Param("minIncome") Double minIncome, @Param("maxIncome") Double maxIncome);

    /**
     * Find active customers
     */
    @Query("SELECT c FROM Customer c WHERE c.status = 'ACTIVE' AND c.deleted = false")
    List<Customer> findActiveCustomers();
}
