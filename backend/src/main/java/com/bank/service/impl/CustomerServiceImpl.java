package com.bank.service.impl;

import com.bank.dto.CustomerRegistrationDto;
import com.bank.entity.Customer;
import com.bank.repository.CustomerRepository;
import com.bank.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of CustomerService
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public Customer register(CustomerRegistrationDto registrationDto) {
        log.info("[INFO]CustomerServiceImpl::register: Registering new customer with phone: {}", registrationDto.getPhone());

        // Check if customer already exists
        if (customerRepository.existsByPhone(registrationDto.getPhone())) {
            throw new RuntimeException("Customer with phone " + registrationDto.getPhone() + " already exists");
        }

        if (customerRepository.existsByIdCard(registrationDto.getIdCard())) {
            throw new RuntimeException("Customer with ID card " + registrationDto.getIdCard() + " already exists");
        }

        // Create new customer
        Customer customer = new Customer();
        customer.setName(registrationDto.getName());
        customer.setPhone(registrationDto.getPhone());
        customer.setIdCard(registrationDto.getIdCard());
        customer.setEmail(registrationDto.getEmail());
        customer.setAge(registrationDto.getAge());
        customer.setOccupation(registrationDto.getOccupation());
        customer.setAnnualIncome(registrationDto.getAnnualIncome());
        customer.setInvestmentExperience(registrationDto.getInvestmentExperience());
        customer.setStatus(Customer.CustomerStatus.ACTIVE);

        Customer savedCustomer = customerRepository.save(customer);
        log.info("[INFO]CustomerServiceImpl::register: Customer registered successfully with ID: {}", savedCustomer.getId());

        return savedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(Long id) {
        log.debug("[DEBUG]CustomerServiceImpl::getCustomerById: Getting customer by ID: {}", id);
        return customerRepository.findById(id);
    }

    @Override
    public Customer updateCustomer(Long id, CustomerRegistrationDto updateDto) {
        log.info("[INFO]CustomerServiceImpl::updateCustomer: Updating customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        // Update fields
        customer.setName(updateDto.getName());
        customer.setEmail(updateDto.getEmail());
        customer.setAge(updateDto.getAge());
        customer.setOccupation(updateDto.getOccupation());
        customer.setAnnualIncome(updateDto.getAnnualIncome());
        customer.setInvestmentExperience(updateDto.getInvestmentExperience());

        Customer updatedCustomer = customerRepository.save(customer);
        log.info("[INFO]CustomerServiceImpl::updateCustomer: Customer updated successfully");

        return updatedCustomer;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifyCustomer(String phone, String idCard) {
        log.debug("[DEBUG]CustomerServiceImpl::verifyCustomer: Verifying customer with phone: {}", phone);
        return customerRepository.findByPhoneAndIdCard(phone, idCard).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByPhone(String phone) {
        log.debug("[DEBUG]CustomerServiceImpl::getCustomerByPhone: Getting customer by phone: {}", phone);
        return customerRepository.findByPhone(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerByIdCard(String idCard) {
        log.debug("[DEBUG]CustomerServiceImpl::getCustomerByIdCard: Getting customer by ID card: {}", idCard);
        return customerRepository.findByIdCard(idCard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getActiveCustomers() {
        log.debug("[DEBUG]CustomerServiceImpl::getActiveCustomers: Getting all active customers");
        return customerRepository.findActiveCustomers();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getCustomersByStatus(Customer.CustomerStatus status) {
        log.debug("[DEBUG]CustomerServiceImpl::getCustomersByStatus: Getting customers by status: {}", status);
        return customerRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getCustomersByExperience(Customer.InvestmentExperience experience) {
        log.debug("[DEBUG]CustomerServiceImpl::getCustomersByExperience: Getting customers by experience: {}", experience);
        return customerRepository.findByInvestmentExperience(experience);
    }

    @Override
    public void deleteCustomer(Long id) {
        log.info("[INFO]CustomerServiceImpl::deleteCustomer: Soft deleting customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        customer.setDeleted(true);
        customer.setDeletedAt(LocalDateTime.now());
        customer.setStatus(Customer.CustomerStatus.DELETED);

        customerRepository.save(customer);
        log.info("[INFO]CustomerServiceImpl::deleteCustomer: Customer soft deleted successfully");
    }

    @Override
    public Customer activateCustomer(Long id) {
        log.info("[INFO]CustomerServiceImpl::activateCustomer: Activating customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        customer.setStatus(Customer.CustomerStatus.ACTIVE);
        customer.setDeleted(false);
        customer.setDeletedAt(null);

        Customer activatedCustomer = customerRepository.save(customer);
        log.info("[INFO]CustomerServiceImpl::activateCustomer: Customer activated successfully");

        return activatedCustomer;
    }

    @Override
    public Customer suspendCustomer(Long id) {
        log.info("[INFO]CustomerServiceImpl::suspendCustomer: Suspending customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with ID: " + id));

        customer.setStatus(Customer.CustomerStatus.SUSPENDED);

        Customer suspendedCustomer = customerRepository.save(customer);
        log.info("[INFO]CustomerServiceImpl::suspendCustomer: Customer suspended successfully");

        return suspendedCustomer;
    }
}
