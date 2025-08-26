package com.bank.assessment.dto;

import com.bank.common.entity.RiskAssessment;
import lombok.Data;

/**
 * Risk Assessment Result DTO - 风险评估结果数据传输对象
 *
 * @author Bank System
 * @since 1.0.0
 */
@Data
public class RiskAssessmentResultDto {

    /**
     * 风险评估ID
     */
    private Long assessmentId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 风险评分
     */
    private Integer riskScore;

    /**
     * 风险等级
     */
    private String riskLevel;

    /**
     * 风险等级描述
     */
    private String riskLevelDescription;

    /**
     * 投资金额
     */
    private String investmentAmount;

    /**
     * 评估状态
     */
    private String status;

    /**
     * 评估建议
     */
    private String recommendation;

    /**
     * 创建时间
     */
    private String createdAt;

    /**
     * 从实体转换为DTO
     */
    public static RiskAssessmentResultDto fromEntity(RiskAssessment assessment) {
        RiskAssessmentResultDto dto = new RiskAssessmentResultDto();
        dto.setAssessmentId(assessment.getId());
        dto.setCustomerId(assessment.getCustomerId());
        dto.setRiskScore(assessment.getRiskScore());
        dto.setRiskLevel(assessment.getRiskLevel().name());
        dto.setRiskLevelDescription(assessment.getRiskLevel().getDescription());
        dto.setInvestmentAmount(assessment.getInvestmentAmount().toString());
        dto.setStatus(assessment.getStatus().name());
        dto.setCreatedAt(assessment.getCreatedAt().toString());

        // 根据风险等级设置建议
        switch (assessment.getRiskLevel()) {
            case CONSERVATIVE:
                dto.setRecommendation("建议投资低风险产品，如货币基金、国债等");
                break;
            case MODERATE:
                dto.setRecommendation("建议投资中等风险产品，如混合基金、优质债券等");
                break;
            case AGGRESSIVE:
                dto.setRecommendation("建议投资高风险产品，如股票基金、成长型产品等");
                break;
        }

        return dto;
    }
}
