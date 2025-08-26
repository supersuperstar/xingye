package com.bank.assessment.service.impl;

import com.bank.assessment.dto.RiskAssessmentDto;
import com.bank.assessment.dto.RiskAssessmentResultDto;
import com.bank.assessment.repository.RiskAssessmentRepository;
import com.bank.assessment.service.RiskAssessmentService;
import com.bank.common.entity.RiskAssessment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Risk Assessment Service Implementation - 风险评估服务实现类
 *
 * @author Bank System
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RiskAssessmentServiceImpl implements RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final ObjectMapper objectMapper;

    @Override
    public RiskAssessmentResultDto submitAssessment(RiskAssessmentDto assessmentDto) {
        log.info("[INFO]RiskAssessmentServiceImpl::submitAssessment: 开始风险评估 - 客户ID: {}", assessmentDto.getCustomerId());

        // 计算风险评分
        int riskScore = calculateRiskScore(assessmentDto);

        // 确定风险等级
        RiskAssessment.RiskLevel riskLevel = determineRiskLevel(riskScore);

        // 创建风险评估实体
        RiskAssessment assessment = new RiskAssessment();
        assessment.setCustomerId(assessmentDto.getCustomerId());
        assessment.setRiskScore(riskScore);
        assessment.setRiskLevel(riskLevel);
        assessment.setInvestmentAmount(assessmentDto.getInvestmentAmount());
        assessment.setStatus(RiskAssessment.AssessmentStatus.ASSESSED);

        // 将问卷答案转换为JSON字符串
        try {
            assessment.setQuestionnaireAnswers(objectMapper.writeValueAsString(assessmentDto.getQuestionnaireAnswers()));
        } catch (JsonProcessingException e) {
            log.error("[ERROR]RiskAssessmentServiceImpl::submitAssessment: 问卷答案序列化失败", e);
            throw new RuntimeException("问卷答案处理失败");
        }

        // 保存风险评估
        RiskAssessment savedAssessment = riskAssessmentRepository.save(assessment);

        log.info("[INFO]RiskAssessmentServiceImpl::submitAssessment: 风险评估完成 - 评分: {}, 等级: {}", riskScore, riskLevel);

        return RiskAssessmentResultDto.fromEntity(savedAssessment);
    }

    @Override
    @Transactional(readOnly = true)
    public RiskAssessment getAssessmentById(Long id) {
        log.info("[INFO]RiskAssessmentServiceImpl::getAssessmentById: 获取风险评估 - ID: {}", id);
        return riskAssessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("风险评估不存在"));
    }

    @Override
    @Transactional(readOnly = true)
    public RiskAssessment getAssessmentByCustomerId(Long customerId) {
        log.info("[INFO]RiskAssessmentServiceImpl::getAssessmentByCustomerId: 获取客户风险评估 - 客户ID: {}", customerId);
        return riskAssessmentRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("客户风险评估不存在"));
    }

    @Override
    public RiskAssessmentResultDto recalculateRiskScore(Long id) {
        log.info("[INFO]RiskAssessmentServiceImpl::recalculateRiskScore: 重新计算风险评分 - ID: {}", id);

        RiskAssessment assessment = getAssessmentById(id);

        // 重新计算风险评分（这里需要重新解析问卷答案）
        // 简化处理，直接返回现有结果
        return RiskAssessmentResultDto.fromEntity(assessment);
    }

    @Override
    public int calculateRiskScore(RiskAssessmentDto assessmentDto) {
        log.info("[INFO]RiskAssessmentServiceImpl::calculateRiskScore: 计算风险评分");

        int score = 50; // 基础分

        // 年龄权重
        score += calculateAgeScore(assessmentDto.getAgeRange());

        // 收入权重
        score += calculateIncomeScore(assessmentDto.getIncomeLevel());

        // 投资经验权重
        score += calculateExperienceScore(assessmentDto.getInvestmentExperience());

        // 风险承受能力权重
        score += calculateRiskToleranceScore(assessmentDto.getRiskTolerance());

        // 投资目标权重
        score += calculateGoalScore(assessmentDto.getInvestmentGoal());

        // 投资期限权重
        score += calculatePeriodScore(assessmentDto.getInvestmentPeriod());

        // 确保评分在0-100范围内
        score = Math.max(0, Math.min(100, score));

        log.info("[INFO]RiskAssessmentServiceImpl::calculateRiskScore: 风险评分计算完成 - {}", score);
        return score;
    }

    @Override
    public RiskAssessment.RiskLevel determineRiskLevel(int riskScore) {
        if (riskScore < 40) {
            return RiskAssessment.RiskLevel.CONSERVATIVE;
        } else if (riskScore < 70) {
            return RiskAssessment.RiskLevel.MODERATE;
        } else {
            return RiskAssessment.RiskLevel.AGGRESSIVE;
        }
    }

    /**
     * 计算年龄评分
     */
    private int calculateAgeScore(String ageRange) {
        switch (ageRange) {
            case "18-30岁": return 15;
            case "31-45岁": return 10;
            case "46-60岁": return 5;
            case "60岁以上": return -10;
            default: return 0;
        }
    }

    /**
     * 计算收入评分
     */
    private int calculateIncomeScore(String incomeLevel) {
        switch (incomeLevel) {
            case "50万以上": return 15;
            case "30-50万": return 10;
            case "10-30万": return 5;
            case "10万以下": return 0;
            default: return 0;
        }
    }

    /**
     * 计算投资经验评分
     */
    private int calculateExperienceScore(String experience) {
        switch (experience) {
            case "5年以上": return 15;
            case "3-5年": return 10;
            case "1-3年": return 5;
            case "无经验": return 0;
            default: return 0;
        }
    }

    /**
     * 计算风险承受能力评分
     */
    private int calculateRiskToleranceScore(String riskTolerance) {
        switch (riskTolerance) {
            case "30%以上": return 20;
            case "15-30%": return 10;
            case "5-15%": return 5;
            case "5%以内": return -10;
            default: return 0;
        }
    }

    /**
     * 计算投资目标评分
     */
    private int calculateGoalScore(String goal) {
        switch (goal) {
            case "追求高收益": return 15;
            case "积极增长": return 10;
            case "稳健增值": return 5;
            case "资产保值": return -5;
            default: return 0;
        }
    }

    /**
     * 计算投资期限评分
     */
    private int calculatePeriodScore(String period) {
        switch (period) {
            case "5年以上": return 10;
            case "3-5年": return 5;
            case "1-3年": return 0;
            case "1年以内": return -10;
            default: return 0;
        }
    }
}
