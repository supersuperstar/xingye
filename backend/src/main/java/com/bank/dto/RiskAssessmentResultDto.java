package com.bank.dto;

import com.bank.entity.RiskAssessment;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO for returning risk assessment results
 *
 * @author Bank Risk Assessment Team
 * @version 1.0.0
 */
@Data
public class RiskAssessmentResultDto {

    private Long assessmentId;
    private Long customerId;
    private Integer riskScore;
    private RiskAssessment.RiskLevel riskLevel;
    private String riskLevelDescription;
    private Double investmentAmount;
    private RiskAssessment.AssessmentStatus status;
    private String recommendation;
    private LocalDateTime createdAt;

    /**
     * Convert RiskAssessment entity to DTO
     */
    public static RiskAssessmentResultDto fromEntity(RiskAssessment assessment) {
        RiskAssessmentResultDto dto = new RiskAssessmentResultDto();
        dto.setAssessmentId(assessment.getId());
        dto.setCustomerId(assessment.getCustomerId());
        dto.setRiskScore(assessment.getRiskScore());
        dto.setRiskLevel(assessment.getRiskLevel());
        dto.setInvestmentAmount(assessment.getInvestmentAmount());
        dto.setStatus(assessment.getStatus());
        dto.setCreatedAt(assessment.getCreatedAt());

        // Set risk level description
        switch (assessment.getRiskLevel()) {
            case CONSERVATIVE:
                dto.setRiskLevelDescription("保守型投资者 - 适合低风险投资产品");
                dto.setRecommendation("建议投资货币基金、债券基金等低风险产品，年化收益率预期3-5%");
                break;
            case MODERATE:
                dto.setRiskLevelDescription("稳健型投资者 - 适合中等风险投资产品");
                dto.setRecommendation("建议投资混合基金、指数基金等中等风险产品，年化收益率预期5-8%");
                break;
            case AGGRESSIVE:
                dto.setRiskLevelDescription("激进型投资者 - 适合高风险投资产品");
                dto.setRecommendation("建议投资股票基金、期货等高风险产品，年化收益率预期8-15%");
                break;
        }

        return dto;
    }
}
