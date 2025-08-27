package com.xingye.bankrisk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xingye.bankrisk.entity.Questionnaire;
import com.xingye.bankrisk.entity.User;
import com.xingye.bankrisk.repository.QuestionnaireRepository;
import com.xingye.bankrisk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 风险评估服务类
 * 负责计算用户的风险评分和风险等级
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RiskAssessmentService {

    private final QuestionnaireRepository questionnaireRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // 风险评分权重配置
    private static final double AGE_WEIGHT = 0.15;
    private static final double ANNUAL_INCOME_WEIGHT = 0.20;
    private static final double INVEST_TIME_WEIGHT = 0.15;
    private static final double MAX_LOSS_WEIGHT = 0.25;
    private static final double INVEST_AMOUNT_WEIGHT = 0.15;
    private static final double QUESTIONNAIRE_WEIGHT = 0.10;

    // 风险等级划分标准
    private static final int CONSERVATIVE_MAX_SCORE = 30;
    private static final int MODERATE_MAX_SCORE = 70;
    // 71+ 为 AGGRESSIVE

    /**
     * 提交风险评估问卷
     */
    @Transactional
    public Questionnaire submitAssessment(Long userId, Map<String, String> answers) {
        log.info("[INFO]RiskAssessmentService::submitAssessment: 用户提交风险评估 - UserID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在: " + userId));

        // 计算风险评分
        int riskScore = calculateRiskScore(user, answers);
        User.RiskLevel riskLevel = determineRiskLevel(riskScore);

        // 创建问卷记录
        Questionnaire questionnaire = Questionnaire.builder()
                .userId(userId)
                .ctime(LocalDateTime.now())
                .isLatest(true)
                .age(user.getTelephone() != null ? calculateAgeFromUser(user) : null)
                .annual(user.getInvestAmount() != null ? user.getInvestAmount() : BigDecimal.ZERO)
                .investTime(extractInvestTimeFromAnswers(answers))
                .maxLoss(extractMaxLossFromAnswers(answers))
                .target(extractTargetFromAnswers(answers))
                .yearForInvest(extractYearForInvestFromAnswers(answers))
                .score(riskScore)
                .status(riskLevel)
                .answers(writeValueAsStringSafely(answers))
                .scoreBreakdown(createScoreBreakdown(user, answers, riskScore))
                .build();

        // 保存问卷
        Questionnaire savedQuestionnaire = questionnaireRepository.save(questionnaire);

        // 更新用户风险等级和最新问卷ID
        user.setRiskLevel(riskLevel);
        user.setEvaluationTime(LocalDateTime.now());
        user.setLatestQuestionnaireId(savedQuestionnaire.getId());
        userRepository.save(user);

        // 将之前的问卷标记为非最新
        List<Questionnaire> previousQuestionnaires = questionnaireRepository.findByUserIdOrderByCreatedAtDesc(userId);
        for (Questionnaire prev : previousQuestionnaires) {
            if (!prev.getId().equals(savedQuestionnaire.getId())) {
                prev.setIsLatest(false);
                questionnaireRepository.save(prev);
            }
        }

        log.info("[INFO]RiskAssessmentService::submitAssessment: 风险评估完成 - UserID: {}, Score: {}, Level: {}",
                userId, riskScore, riskLevel);
        return savedQuestionnaire;
    }

    /**
     * 计算风险评分
     */
    public int calculateRiskScore(User user, Map<String, String> answers) {
        double totalScore = 0;

        // 年龄评分（年龄越大风险偏好越低）
        if (user.getTelephone() != null) {
            int age = calculateAgeFromUser(user);
            double ageScore = Math.max(0, 100 - age); // 年龄越大得分越低
            totalScore += ageScore * AGE_WEIGHT;
        }

        // 年收入评分（收入越高风险承受能力越强）
        if (user.getInvestAmount() != null && user.getInvestAmount().compareTo(BigDecimal.ZERO) > 0) {
            double incomeScore = calculateIncomeScore(user.getInvestAmount());
            totalScore += incomeScore * ANNUAL_INCOME_WEIGHT;
        }

        // 投资经验评分
        Integer investTime = extractInvestTimeFromAnswers(answers);
        if (investTime != null) {
            double investTimeScore = Math.min(100, investTime * 10); // 投资年限越长得分越高
            totalScore += investTimeScore * INVEST_TIME_WEIGHT;
        }

        // 最大亏损承受评分
        BigDecimal maxLoss = extractMaxLossFromAnswers(answers);
        if (maxLoss != null) {
            double maxLossScore = maxLoss.doubleValue() * 100; // 直接转换为百分比分数
            totalScore += maxLossScore * MAX_LOSS_WEIGHT;
        }

        // 投资金额评分（投资金额越大风险承受能力相对越强）
        if (user.getInvestAmount() != null) {
            double amountScore = calculateAmountScore(user.getInvestAmount());
            totalScore += amountScore * INVEST_AMOUNT_WEIGHT;
        }

        // 问卷答案评分
        double questionnaireScore = calculateQuestionnaireScore(answers);
        totalScore += questionnaireScore * QUESTIONNAIRE_WEIGHT;

        return (int) Math.round(Math.max(0, Math.min(100, totalScore)));
    }

    /**
     * 确定风险等级
     */
    public User.RiskLevel determineRiskLevel(int riskScore) {
        if (riskScore <= CONSERVATIVE_MAX_SCORE) {
            return User.RiskLevel.CONSERVATIVE;
        } else if (riskScore <= MODERATE_MAX_SCORE) {
            return User.RiskLevel.MODERATE;
        } else {
            return User.RiskLevel.AGGRESSIVE;
        }
    }

    /**
     * 获取用户最新评估结果
     */
    public Optional<Questionnaire> getLatestAssessment(Long userId) {
        return questionnaireRepository.findLatestByUserId(userId);
    }

    /**
     * 获取用户评估历史
     */
    public List<Questionnaire> getAssessmentHistory(Long userId) {
        return questionnaireRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * 重新计算风险评分
     */
    @Transactional
    public Questionnaire recalculateRiskScore(Long questionnaireId) {
        log.info("[INFO]RiskAssessmentService::recalculateRiskScore: 重新计算风险评分 - QuestionnaireID: {}", questionnaireId);

        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new RuntimeException("问卷不存在: " + questionnaireId));

        User user = userRepository.findById(questionnaire.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在: " + questionnaire.getUserId()));

        // 重新计算评分
        Map<String, String> answers = objectMapper.convertValue(questionnaire.getAnswers(), Map.class);
        int newScore = calculateRiskScore(user, answers);
        User.RiskLevel newRiskLevel = determineRiskLevel(newScore);

        // 更新问卷
        questionnaire.setScore(newScore);
        questionnaire.setStatus(newRiskLevel);
        questionnaire.setScoreBreakdown(createScoreBreakdown(user, answers, newScore));

        Questionnaire savedQuestionnaire = questionnaireRepository.save(questionnaire);

        // 更新用户风险等级
        user.setRiskLevel(newRiskLevel);
        user.setEvaluationTime(LocalDateTime.now());
        userRepository.save(user);

        log.info("[INFO]RiskAssessmentService::recalculateRiskScore: 风险评分重新计算完成 - ID: {}, NewScore: {}", questionnaireId, newScore);
        return savedQuestionnaire;
    }

    // 私有辅助方法

    private int calculateAgeFromUser(User user) {
        // 简化的年龄计算，实际应该从身份证号解析
        return 30; // 默认年龄
    }

    private double calculateIncomeScore(BigDecimal annualIncome) {
        // 年收入评分算法
        double income = annualIncome.doubleValue();
        if (income < 50000) return 20;
        else if (income < 100000) return 40;
        else if (income < 200000) return 60;
        else if (income < 500000) return 80;
        else return 100;
    }

    private double calculateAmountScore(BigDecimal investAmount) {
        // 投资金额评分算法
        double amount = investAmount.doubleValue();
        if (amount < 10000) return 20;
        else if (amount < 50000) return 40;
        else if (amount < 100000) return 60;
        else if (amount < 500000) return 80;
        else return 100;
    }

    private Integer extractInvestTimeFromAnswers(Map<String, String> answers) {
        String investTimeStr = answers.get("invest_time");
        return investTimeStr != null ? Integer.valueOf(investTimeStr) : null;
    }

    private BigDecimal extractMaxLossFromAnswers(Map<String, String> answers) {
        String maxLossStr = answers.get("max_loss");
        return maxLossStr != null ? new BigDecimal(maxLossStr).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP) : null;
    }

    private String extractTargetFromAnswers(Map<String, String> answers) {
        return answers.get("target");
    }

    private Integer extractYearForInvestFromAnswers(Map<String, String> answers) {
        String yearStr = answers.get("year_for_invest");
        return yearStr != null ? Integer.valueOf(yearStr) : null;
    }

    private double calculateQuestionnaireScore(Map<String, String> answers) {
        // 问卷答案评分算法（可根据实际问卷题目调整）
        double score = 0;
        int questionCount = 0;

        for (Map.Entry<String, String> entry : answers.entrySet()) {
            if (!Arrays.asList("invest_time", "max_loss", "target", "year_for_invest").contains(entry.getKey())) {
                try {
                    score += Double.parseDouble(entry.getValue());
                    questionCount++;
                } catch (NumberFormatException e) {
                    // 忽略非数字答案
                }
            }
        }

        return questionCount > 0 ? score / questionCount * 20 : 50; // 标准化到0-100分
    }

    private String createScoreBreakdown(User user, Map<String, String> answers, int totalScore) {
        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("total_score", totalScore);
        breakdown.put("risk_level", determineRiskLevel(totalScore).toString());

        // 详细评分明细
        Map<String, Double> scoreDetails = new HashMap<>();
        if (user.getInvestAmount() != null) {
            scoreDetails.put("annual_income", calculateIncomeScore(user.getInvestAmount()) * ANNUAL_INCOME_WEIGHT);
        }
        if (user.getTelephone() != null) {
            int age = calculateAgeFromUser(user);
            scoreDetails.put("age", (100 - age) * AGE_WEIGHT);
        }

        Integer investTime = extractInvestTimeFromAnswers(answers);
        if (investTime != null) {
            scoreDetails.put("invest_time", Math.min(100, investTime * 10) * INVEST_TIME_WEIGHT);
        }

        BigDecimal maxLoss = extractMaxLossFromAnswers(answers);
        if (maxLoss != null) {
            scoreDetails.put("max_loss", maxLoss.doubleValue() * 100 * MAX_LOSS_WEIGHT);
        }

        breakdown.put("score_details", scoreDetails);
        try {
            return objectMapper.writeValueAsString(breakdown);
        } catch (Exception e) {
            log.error("[ERROR]RiskAssessmentService::createScoreBreakdown: JSON序列化失败", e);
            return "{}";
        }
    }

    /**
     * 安全地将对象转换为JSON字符串
     */
    private String writeValueAsStringSafely(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.error("[ERROR]RiskAssessmentService::writeValueAsStringSafely: JSON序列化失败", e);
            return "{}";
        }
    }
}
