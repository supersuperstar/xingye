package com.bank.assessment.service;

import com.bank.assessment.dto.RiskAssessmentDto;
import com.bank.assessment.dto.RiskAssessmentResultDto;
import com.bank.common.entity.RiskAssessment;

/**
 * Risk Assessment Service Interface - 风险评估服务接口
 *
 * @author Bank System
 * @since 1.0.0
 */
public interface RiskAssessmentService {

    /**
     * 提交风险评估
     *
     * @param assessmentDto 评估数据
     * @return 评估结果
     */
    RiskAssessmentResultDto submitAssessment(RiskAssessmentDto assessmentDto);

    /**
     * 根据ID获取风险评估
     *
     * @param id 评估ID
     * @return 评估信息
     */
    RiskAssessment getAssessmentById(Long id);

    /**
     * 根据客户ID获取风险评估
     *
     * @param customerId 客户ID
     * @return 评估信息
     */
    RiskAssessment getAssessmentByCustomerId(Long customerId);

    /**
     * 重新计算风险评分
     *
     * @param id 评估ID
     * @return 评估结果
     */
    RiskAssessmentResultDto recalculateRiskScore(Long id);

    /**
     * 计算风险评分
     *
     * @param assessmentDto 评估数据
     * @return 风险评分
     */
    int calculateRiskScore(RiskAssessmentDto assessmentDto);

    /**
     * 确定风险等级
     *
     * @param riskScore 风险评分
     * @return 风险等级
     */
    RiskAssessment.RiskLevel determineRiskLevel(int riskScore);
}
