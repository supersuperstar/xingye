package com.bank.customer.service;

import com.bank.common.entity.Customer;
import com.bank.customer.dto.CustomerRegistrationDto;

/**
 * Customer Service Interface - 客户服务接口
 *
 * @author Bank System
 * @since 1.0.0
 */
public interface CustomerService {

    /**
     * 客户注册
     *
     * @param registrationDto 注册信息
     * @return 注册成功的客户信息
     */
    Customer register(CustomerRegistrationDto registrationDto);

    /**
     * 根据ID获取客户信息
     *
     * @param id 客户ID
     * @return 客户信息
     */
    Customer getCustomerById(Long id);

    /**
     * 更新客户信息
     *
     * @param id 客户ID
     * @param updateDto 更新信息
     * @return 更新后的客户信息
     */
    Customer updateCustomer(Long id, CustomerRegistrationDto updateDto);

    /**
     * 验证客户身份
     *
     * @param phone 手机号
     * @param idCard 身份证号
     * @return 验证结果
     */
    boolean verifyCustomer(String phone, String idCard);

    /**
     * 根据手机号获取客户信息
     *
     * @param phone 手机号
     * @return 客户信息
     */
    Customer getCustomerByPhone(String phone);

    /**
     * 根据身份证号获取客户信息
     *
     * @param idCard 身份证号
     * @return 客户信息
     */
    Customer getCustomerByIdCard(String idCard);
}
