package com.bank.service;

import com.bank.dto.CustomerRegistrationDto;
import com.bank.entity.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for customer operations
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
public interface CustomerService {

    /**
     * Register a new customer
     */
    Customer register(CustomerRegistrationDto registrationDto);

    /**
     * Get customer by ID
     */
    Optional<Customer> getCustomerById(Long id);

    /**
     * Update customer information
     */
    Customer updateCustomer(Long id, CustomerRegistrationDto updateDto);

    /**
     * Verify customer identity
     */
    boolean verifyCustomer(String phone, String idCard);

    /**
     * Get customer by phone number
     */
    Optional<Customer> getCustomerByPhone(String phone);

    /**
     * Get customer by ID card number
     */
    Optional<Customer> getCustomerByIdCard(String idCard);

    /**
     * Get all active customers
     */
    List<Customer> getActiveCustomers();

    /**
     * Get customers by status
     */
    List<Customer> getCustomersByStatus(Customer.CustomerStatus status);

    /**
     * Get customers by investment experience
     */
    List<Customer> getCustomersByExperience(Customer.InvestmentExperience experience);

    /**
     * Delete customer (soft delete)
     */
    void deleteCustomer(Long id);

    /**
     * Activate customer
     */
    Customer activateCustomer(Long id);

    /**
     * Suspend customer
     */
    Customer suspendCustomer(Long id);
}
