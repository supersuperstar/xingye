package com.bank.controller;

import com.bank.dto.CustomerRegistrationDto;
import com.bank.entity.Customer;
import com.bank.response.ApiResponse;
import com.bank.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for customer-related operations
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * Register a new customer
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Customer>> registerCustomer(@Valid @RequestBody CustomerRegistrationDto registrationDto) {
        log.info("[INFO]CustomerController::registerCustomer: Registering new customer: {}", registrationDto.getName());

        try {
            Customer customer = customerService.register(registrationDto);
            return ResponseEntity.ok(ApiResponse.success("Customer registered successfully", customer));
        } catch (Exception e) {
            log.error("[ERROR]CustomerController::registerCustomer: Error registering customer: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    /**
     * Get customer by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable Long id) {
        log.info("[INFO]CustomerController::getCustomerById: Getting customer with ID: {}", id);

        return customerService.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(ApiResponse.success(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update customer information
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRegistrationDto updateDto) {
        log.info("[INFO]CustomerController::updateCustomer: Updating customer with ID: {}", id);

        try {
            Customer customer = customerService.updateCustomer(id, updateDto);
            return ResponseEntity.ok(ApiResponse.success("Customer updated successfully", customer));
        } catch (Exception e) {
            log.error("[ERROR]CustomerController::updateCustomer: Error updating customer: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    /**
     * Verify customer identity
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Boolean>> verifyCustomer(
            @RequestParam String phone,
            @RequestParam String idCard) {
        log.info("[INFO]CustomerController::verifyCustomer: Verifying customer with phone: {}", phone);

        boolean isValid = customerService.verifyCustomer(phone, idCard);
        return ResponseEntity.ok(ApiResponse.success("Customer verification completed", isValid));
    }

    /**
     * Get customer by phone number
     */
    @GetMapping("/phone/{phone}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerByPhone(@PathVariable String phone) {
        log.info("[INFO]CustomerController::getCustomerByPhone: Getting customer with phone: {}", phone);

        return customerService.getCustomerByPhone(phone)
                .map(customer -> ResponseEntity.ok(ApiResponse.success(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get customer by ID card number
     */
    @GetMapping("/idcard/{idCard}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerByIdCard(@PathVariable String idCard) {
        log.info("[INFO]CustomerController::getCustomerByIdCard: Getting customer with ID card: {}", idCard);

        return customerService.getCustomerByIdCard(idCard)
                .map(customer -> ResponseEntity.ok(ApiResponse.success(customer)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all active customers
     */
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<Customer>>> getActiveCustomers() {
        log.info("[INFO]CustomerController::getActiveCustomers: Getting all active customers");

        List<Customer> customers = customerService.getActiveCustomers();
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * Get customers by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<Customer>>> getCustomersByStatus(
            @PathVariable Customer.CustomerStatus status) {
        log.info("[INFO]CustomerController::getCustomersByStatus: Getting customers by status: {}", status);

        List<Customer> customers = customerService.getCustomersByStatus(status);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * Get customers by investment experience
     */
    @GetMapping("/experience/{experience}")
    public ResponseEntity<ApiResponse<List<Customer>>> getCustomersByExperience(
            @PathVariable Customer.InvestmentExperience experience) {
        log.info("[INFO]CustomerController::getCustomersByExperience: Getting customers by experience: {}", experience);

        List<Customer> customers = customerService.getCustomersByExperience(experience);
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    /**
     * Delete customer (soft delete)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        log.info("[INFO]CustomerController::deleteCustomer: Deleting customer with ID: {}", id);

        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(ApiResponse.success("Customer deleted successfully", null));
        } catch (Exception e) {
            log.error("[ERROR]CustomerController::deleteCustomer: Error deleting customer: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    /**
     * Activate customer
     */
    @PostMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<Customer>> activateCustomer(@PathVariable Long id) {
        log.info("[INFO]CustomerController::activateCustomer: Activating customer with ID: {}", id);

        try {
            Customer customer = customerService.activateCustomer(id);
            return ResponseEntity.ok(ApiResponse.success("Customer activated successfully", customer));
        } catch (Exception e) {
            log.error("[ERROR]CustomerController::activateCustomer: Error activating customer: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }

    /**
     * Suspend customer
     */
    @PostMapping("/{id}/suspend")
    public ResponseEntity<ApiResponse<Customer>> suspendCustomer(@PathVariable Long id) {
        log.info("[INFO]CustomerController::suspendCustomer: Suspending customer with ID: {}", id);

        try {
            Customer customer = customerService.suspendCustomer(id);
            return ResponseEntity.ok(ApiResponse.success("Customer suspended successfully", customer));
        } catch (Exception e) {
            log.error("[ERROR]CustomerController::suspendCustomer: Error suspending customer: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }
}
