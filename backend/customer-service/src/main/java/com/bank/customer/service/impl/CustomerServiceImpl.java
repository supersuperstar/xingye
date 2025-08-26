package com.bank.customer.service.impl;

import com.bank.common.entity.Customer;
import com.bank.customer.dto.CustomerRegistrationDto;
import com.bank.customer.repository.CustomerRepository;
import com.bank.customer.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Customer Service Implementation - 客户服务实现类
 *
 * @author Bank System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer register(CustomerRegistrationDto registrationDto) {
        log.info("[INFO]CustomerServiceImpl::register: 开始客户注册 - {}", registrationDto.getName());

        // 检查手机号是否已存在
        if (customerRepository.existsByPhone(registrationDto.getPhone())) {
            throw new RuntimeException("手机号已存在");
        }

        // 检查身份证号是否已存在
        if (customerRepository.existsByIdCard(registrationDto.getIdCard())) {
            throw new RuntimeException("身份证号已存在");
        }

        // 创建客户实体
        Customer customer = new Customer();
        BeanUtils.copyProperties(registrationDto, customer);
        customer.setStatus(Customer.CustomerStatus.ACTIVE);

        // 保存客户信息
        Customer savedCustomer = customerRepository.save(customer);
        log.info("[INFO]CustomerServiceImpl::register: 客户注册成功 - ID: {}", savedCustomer.getId());

        return savedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        log.info("[INFO]CustomerServiceImpl::getCustomerById: 获取客户信息 - ID: {}", id);
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("客户不存在"));
    }

    @Override
    public Customer updateCustomer(Long id, CustomerRegistrationDto updateDto) {
        log.info("[INFO]CustomerServiceImpl::updateCustomer: 更新客户信息 - ID: {}", id);

        Customer existingCustomer = getCustomerById(id);

        // 检查手机号是否被其他客户使用
        if (!existingCustomer.getPhone().equals(updateDto.getPhone()) &&
            customerRepository.existsByPhone(updateDto.getPhone())) {
            throw new RuntimeException("手机号已被其他客户使用");
        }

        // 检查身份证号是否被其他客户使用
        if (!existingCustomer.getIdCard().equals(updateDto.getIdCard()) &&
            customerRepository.existsByIdCard(updateDto.getIdCard())) {
            throw new RuntimeException("身份证号已被其他客户使用");
        }

        // 更新客户信息
        BeanUtils.copyProperties(updateDto, existingCustomer);
        Customer updatedCustomer = customerRepository.save(existingCustomer);

        log.info("[INFO]CustomerServiceImpl::updateCustomer: 客户信息更新成功 - ID: {}", id);
        return updatedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyCustomer(String phone, String idCard) {
        log.info("[INFO]CustomerServiceImpl::verifyCustomer: 验证客户身份 - 手机号: {}", phone);

        Customer customer = customerRepository.findByPhoneAndIdCard(phone, idCard);
        boolean isValid = customer != null && customer.getStatus() == Customer.CustomerStatus.ACTIVE;

        log.info("[INFO]CustomerServiceImpl::verifyCustomer: 身份验证结果 - {}", isValid);
        return isValid;
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerByPhone(String phone) {
        log.info("[INFO]CustomerServiceImpl::getCustomerByPhone: 根据手机号获取客户 - {}", phone);
        return customerRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("客户不存在"));
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerByIdCard(String idCard) {
        log.info("[INFO]CustomerServiceImpl::getCustomerByIdCard: 根据身份证号获取客户 - {}", idCard);
        return customerRepository.findByIdCard(idCard)
                .orElseThrow(() -> new RuntimeException("客户不存在"));
    }
}
