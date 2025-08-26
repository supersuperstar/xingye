package com.bank.dto;

import com.bank.entity.Customer;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for customer registration and update requests
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Data
public class CustomerRegistrationDto {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "Invalid phone number format")
    private String phone;

    @NotBlank(message = "ID card number is required")
    @Pattern(regexp = "^\\d{17}[\\dXx]$", message = "Invalid ID card number format")
    private String idCard;

    @Email(message = "Invalid email format")
    private String email;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age must be at most 100")
    private Integer age;

    @Size(max = 100, message = "Occupation must not exceed 100 characters")
    private String occupation;

    @DecimalMin(value = "0.0", message = "Annual income must be positive")
    private Double annualIncome;

    private Customer.InvestmentExperience investmentExperience;
}
