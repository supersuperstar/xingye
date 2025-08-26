package com.bank.customer.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Customer Registration DTO - 客户注册数据传输对象
 *
 * @author Bank System
 * @since 1.0.0
 */
@Data
public class CustomerRegistrationDto {

    /**
     * 客户姓名
     */
    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度必须在2-50个字符之间")
    private String name;

    /**
     * 手机号码
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
    @Pattern(regexp = "^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$",
             message = "身份证号格式不正确")
    private String idCard;

    /**
     * 电子邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 年龄
     */
    @Min(value = 18, message = "年龄必须大于等于18岁")
    @Max(value = 100, message = "年龄不能超过100岁")
    private Integer age;

    /**
     * 职业
     */
    @Size(max = 100, message = "职业长度不能超过100个字符")
    private String occupation;

    /**
     * 年收入
     */
    @DecimalMin(value = "0.01", message = "年收入必须大于0")
    @Digits(integer = 10, fraction = 2, message = "年收入格式不正确")
    private BigDecimal annualIncome;

    /**
     * 投资经验（年）
     */
    @Min(value = 0, message = "投资经验不能为负数")
    @Max(value = 50, message = "投资经验不能超过50年")
    private Integer investmentExperience;
}
