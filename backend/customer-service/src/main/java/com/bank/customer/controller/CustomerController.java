package com.bank.customer.controller;

import com.bank.common.entity.Customer;
import com.bank.common.response.ApiResponse;
import com.bank.customer.dto.CustomerRegistrationDto;
import com.bank.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * Customer Controller - 客户控制器
 *
 * @author Bank System
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    /**
     * 客户注册
     */
    @PostMapping("/register")
    public ApiResponse<Customer> register(@Valid @RequestBody CustomerRegistrationDto registrationDto) {
        log.info("[INFO]CustomerController::register: 客户注册请求 - {}", registrationDto.getName());
        Customer customer = customerService.register(registrationDto);
        return ApiResponse.success("客户注册成功", customer);
    }

    /**
     * 获取客户信息
     */
    @GetMapping("/{id}")
    public ApiResponse<Customer> getCustomer(@PathVariable Long id) {
        log.info("[INFO]CustomerController::getCustomer: 获取客户信息 - {}", id);
        Customer customer = customerService.getCustomerById(id);
        return ApiResponse.success(customer);
    }

    /**
     * 更新客户信息
     */
    @PutMapping("/{id}")
    public ApiResponse<Customer> updateCustomer(@PathVariable Long id,
                                              @Valid @RequestBody CustomerRegistrationDto updateDto) {
        log.info("[INFO]CustomerController::updateCustomer: 更新客户信息 - {}", id);
        Customer customer = customerService.updateCustomer(id, updateDto);
        return ApiResponse.success("客户信息更新成功", customer);
    }

    /**
     * 验证客户身份
     */
    @PostMapping("/verify")
    public ApiResponse<Boolean> verifyCustomer(@RequestParam String phone,
                                             @RequestParam String idCard) {
        log.info("[INFO]CustomerController::verifyCustomer: 验证客户身份 - {}", phone);
        boolean isValid = customerService.verifyCustomer(phone, idCard);
        return ApiResponse.success("身份验证完成", isValid);
    }
}
