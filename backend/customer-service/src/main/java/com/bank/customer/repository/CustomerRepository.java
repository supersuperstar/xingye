package com.bank.customer.repository;

import com.bank.common.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Customer Repository - 客户数据访问接口
 *
 * @author Bank System
 * @since 1.0.0
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * 根据手机号查找客户
     *
     * @param phone 手机号
     * @return 客户信息
     */
    Optional<Customer> findByPhone(String phone);

    /**
     * 根据身份证号查找客户
     *
     * @param idCard 身份证号
     * @return 客户信息
     */
    Optional<Customer> findByIdCard(String idCard);

    /**
     * 根据手机号和身份证号查找客户
     *
     * @param phone 手机号
     * @param idCard 身份证号
     * @return 客户信息
     */
    Customer findByPhoneAndIdCard(String phone, String idCard);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return 是否存在
     */
    boolean existsByPhone(String phone);

    /**
     * 检查身份证号是否存在
     *
     * @param idCard 身份证号
     * @return 是否存在
     */
    boolean existsByIdCard(String idCard);

    /**
     * 根据状态查找客户
     *
     * @param status 客户状态
     * @return 客户列表
     */
    @Query("SELECT c FROM Customer c WHERE c.status = :status AND c.deleted = false")
    java.util.List<Customer> findByStatus(@Param("status") Customer.CustomerStatus status);
}
