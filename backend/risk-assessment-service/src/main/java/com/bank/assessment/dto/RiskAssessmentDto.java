package com.bank.assessment.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Risk Assessment DTO - 风险评估数据传输对象
 *
 * @author Bank System
 * @since 1.0.0
 */
@Data
public class RiskAssessmentDto {

    /**
     * 客户ID
     */
    @NotNull(message = "客户ID不能为空")
    private Long customerId;

    /**
     * 投资金额
     */
    @NotNull(message = "投资金额不能为空")
    @DecimalMin(value = "0.01", message = "投资金额必须大于0")
    @Digits(integer = 10, fraction = 2, message = "投资金额格式不正确")
    private BigDecimal investmentAmount;

    /**
     * 问卷答案
     */
    @NotNull(message = "问卷答案不能为空")
    private Map<String, String> questionnaireAnswers;

    /**
     * 年龄范围
     */
    @NotBlank(message = "年龄范围不能为空")
    private String ageRange;

    /**
     * 年收入水平
     */
    @NotBlank(message = "年收入水平不能为空")
    private String incomeLevel;

    /**
     * 投资经验
     */
    @NotBlank(message = "投资经验不能为空")
    private String investmentExperience;

    /**
     * 风险承受能力
     */
    @NotBlank(message = "风险承受能力不能为空")
    private String riskTolerance;

    /**
     * 投资目标
     */
    @NotBlank(message = "投资目标不能为空")
    private String investmentGoal;

    /**
     * 投资期限
     */
    @NotBlank(message = "投资期限不能为空")
    private String investmentPeriod;
}
