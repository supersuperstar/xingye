package com.bank.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Map;

/**
 * DTO for submitting risk assessment data
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Data
public class RiskAssessmentDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotNull(message = "Investment amount is required")
    @DecimalMin(value = "1000.0", message = "Investment amount must be at least 1000")
    @DecimalMax(value = "10000000.0", message = "Investment amount must not exceed 10,000,000")
    private Double investmentAmount;

    @NotNull(message = "Questionnaire answers are required")
    private Map<String, Object> questionnaireAnswers;

    // Individual questionnaire fields for easier validation
    @NotBlank(message = "Age range is required")
    private String ageRange;

    @NotBlank(message = "Income level is required")
    private String incomeLevel;

    @NotBlank(message = "Investment experience is required")
    private String investmentExperience;

    @NotBlank(message = "Risk tolerance is required")
    private String riskTolerance;

    @NotBlank(message = "Investment goal is required")
    private String investmentGoal;

    @NotBlank(message = "Investment period is required")
    private String investmentPeriod;
}
