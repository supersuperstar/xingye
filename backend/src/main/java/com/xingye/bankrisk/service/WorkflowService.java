package com.xingye.bankrisk.service;

import com.xingye.bankrisk.entity.*;
import com.xingye.bankrisk.repository.UserRepository;
import com.xingye.bankrisk.repository.WorkOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 工作流服务类
 * 管理多级审核流程
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkflowService {

    private final WorkOrderRepository workOrderRepository;
    private final UserRepository userRepository;
    private final PortfolioGenerationService portfolioGenerationService;

    /**
     * 创建工单
     */
    @Transactional
    public WorkOrder createWorkOrder(Long customerId, Integer riskScore, User.RiskLevel riskCategory) {
        log.info("[INFO]WorkflowService::createWorkOrder: 创建工单 - CustomerID: {}, RiskScore: {}", customerId, riskScore);

        WorkOrder workOrder = WorkOrder.builder()
                .customerId(customerId)
                .status(WorkOrder.WorkOrderStatus.PENDING_JUNIOR)
                .priority(determinePriority(riskScore))
                .slaDeadline(calculateSLADeadline(WorkOrder.WorkOrderStatus.PENDING_JUNIOR))
                .riskScore(riskScore)
                .riskCategory(riskCategory)
                .build();

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        log.info("[INFO]WorkflowService::createWorkOrder: 工单创建成功 - ID: {}", savedWorkOrder.getId());
        return savedWorkOrder;
    }

    /**
     * 推进工作流
     */
    @Transactional
    public WorkOrder advanceWorkflow(Long workOrderId, Long reviewerId, String decision, String comments) {
        log.info("[INFO]WorkflowService::advanceWorkflow: 推进工作流 - WorkOrderID: {}, ReviewerID: {}, Decision: {}",
                workOrderId, reviewerId, decision);

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("工单不存在: " + workOrderId));

        WorkOrder.WorkOrderStatus currentStatus = workOrder.getStatus();
        WorkOrder.WorkOrderStatus nextStatus = determineNextStatus(currentStatus, decision);

        // 记录审核意见
        recordReviewComments(workOrder, reviewerId, currentStatus, comments);

        // 更新工单状态
        workOrder.setStatus(nextStatus);
        workOrder.setReviewerId(null); // 清空当前处理人
        workOrder.setSlaDeadline(calculateSLADeadline(nextStatus));

        // 如果流程结束，生成投资组合
        if (isWorkflowCompleted(nextStatus)) {
            generateFinalPortfolio(workOrder);
        }

        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        log.info("[INFO]WorkflowService::advanceWorkflow: 工作流推进完成 - ID: {}, From: {}, To: {}",
                workOrderId, currentStatus, nextStatus);
        return savedWorkOrder;
    }

    /**
     * 认领审核任务
     */
    @Transactional
    public WorkOrder claimWorkOrder(Long workOrderId, Long reviewerId) {
        log.info("[INFO]WorkflowService::claimWorkOrder: 认领审核任务 - WorkOrderID: {}, ReviewerID: {}", workOrderId, reviewerId);

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("工单不存在: " + workOrderId));

        if (workOrder.getReviewerId() != null) {
            throw new RuntimeException("工单已被其他审核员认领");
        }

        workOrder.setReviewerId(reviewerId);
        WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);

        log.info("[INFO]WorkflowService::claimWorkOrder: 审核任务认领成功 - ID: {}, ReviewerID: {}", workOrderId, reviewerId);
        return savedWorkOrder;
    }

    /**
     * 获取待审核工单列表
     */
    public List<WorkOrder> getPendingWorkOrders(User.UserRole reviewerRole) {
        return switch (reviewerRole) {
            case AUDITOR_JUNIOR -> workOrderRepository.findJuniorPendingWorkOrders();
            case AUDITOR_MID -> workOrderRepository.findMidPendingWorkOrders();
            case AUDITOR_SENIOR -> workOrderRepository.findSeniorPendingWorkOrders();
            case INVEST_COMMITTEE -> workOrderRepository.findCommitteePendingWorkOrders();
            default -> List.of();
        };
    }

    /**
     * 获取审核员的任务列表
     */
    public List<WorkOrder> getReviewerTasks(Long reviewerId) {
        return workOrderRepository.findPendingWorkOrdersByReviewer(reviewerId);
    }

    /**
     * 获取工单详情（包含工作流历史）
     */
    public Optional<WorkOrder> getWorkOrderDetail(Long workOrderId) {
        return workOrderRepository.findById(workOrderId);
    }

    /**
     * 获取超期工单
     */
    public List<WorkOrder> getOverdueWorkOrders() {
        return workOrderRepository.findOverdueWorkOrders();
    }

    /**
     * 统计工作流状态
     */
    public List<Object[]> getWorkflowStatistics() {
        return workOrderRepository.countWorkOrdersByStatus();
    }

    // 私有辅助方法

    private WorkOrder.Priority determinePriority(Integer riskScore) {
        if (riskScore == null) {
            return WorkOrder.Priority.MEDIUM;
        }

        if (riskScore >= 80) {
            return WorkOrder.Priority.CRITICAL; // 高风险需要优先处理
        } else if (riskScore >= 60) {
            return WorkOrder.Priority.HIGH;
        } else if (riskScore >= 40) {
            return WorkOrder.Priority.MEDIUM;
        } else {
            return WorkOrder.Priority.LOW;
        }
    }

    private LocalDateTime calculateSLADeadline(WorkOrder.WorkOrderStatus status) {
        int hours = switch (status) {
            case PENDING_JUNIOR -> 2;
            case PENDING_MID -> 4;
            case PENDING_SENIOR -> 8;
            case PENDING_COMMITTEE -> 24;
            default -> 24;
        };

        return LocalDateTime.now().plusHours(hours);
    }

    private WorkOrder.WorkOrderStatus determineNextStatus(WorkOrder.WorkOrderStatus currentStatus, String decision) {
        return switch (currentStatus) {
            case PENDING_JUNIOR -> {
                if ("APPROVE".equals(decision)) {
                    yield WorkOrder.WorkOrderStatus.PENDING_MID;
                } else {
                    yield WorkOrder.WorkOrderStatus.REJECTED;
                }
            }
            case PENDING_MID -> {
                if ("APPROVE".equals(decision)) {
                    yield WorkOrder.WorkOrderStatus.PENDING_SENIOR;
                } else {
                    yield WorkOrder.WorkOrderStatus.REJECTED;
                }
            }
            case PENDING_SENIOR -> {
                if ("APPROVE".equals(decision)) {
                    // 根据风险等级决定是否需要委员会审核
                    yield WorkOrder.WorkOrderStatus.PENDING_COMMITTEE;
                } else {
                    yield WorkOrder.WorkOrderStatus.REJECTED;
                }
            }
            case PENDING_COMMITTEE -> {
                if ("APPROVE".equals(decision)) {
                    yield WorkOrder.WorkOrderStatus.APPROVED;
                } else {
                    yield WorkOrder.WorkOrderStatus.REJECTED;
                }
            }
            default -> currentStatus;
        };
    }

    private void recordReviewComments(WorkOrder workOrder, Long reviewerId,
                                    WorkOrder.WorkOrderStatus status, String comments) {
        LocalDateTime now = LocalDateTime.now();

        switch (status) {
            case PENDING_JUNIOR -> {
                workOrder.setJuniorReviewerId(reviewerId);
                workOrder.setJuniorComment(comments);
                workOrder.setJuniorCommitTime(now);
            }
            case PENDING_MID -> {
                workOrder.setMidReviewerId(reviewerId);
                workOrder.setMidComment(comments);
                workOrder.setMidCommitTime(now);
            }
            case PENDING_SENIOR -> {
                workOrder.setSeniorReviewerId(reviewerId);
                workOrder.setSeniorComment(comments);
                workOrder.setSeniorCommitTime(now);
            }
            case PENDING_COMMITTEE -> {
                workOrder.setCommitteeReviewerId(reviewerId);
                workOrder.setCommitteeComment(comments);
                workOrder.setCommitteeCommitTime(now);
            }
        }
    }

    private boolean isWorkflowCompleted(WorkOrder.WorkOrderStatus status) {
        return status == WorkOrder.WorkOrderStatus.APPROVED ||
               status == WorkOrder.WorkOrderStatus.REJECTED;
    }

    private void generateFinalPortfolio(WorkOrder workOrder) {
        if (workOrder.getStatus() == WorkOrder.WorkOrderStatus.APPROVED) {
            try {
                portfolioGenerationService.generatePortfolio(
                        workOrder.getCustomerId(),
                        workOrder.getCustomerId(),
                        workOrder.getId()
                );
                log.info("[INFO]WorkflowService::generateFinalPortfolio: 投资组合生成成功 - WorkOrderID: {}", workOrder.getId());
            } catch (Exception e) {
                log.error("[ERROR]WorkflowService::generateFinalPortfolio: 投资组合生成失败 - WorkOrderID: {}", workOrder.getId(), e);
            }
        }
    }

    /**
     * 获取工作流阶段定义
     */
    public List<WorkOrder.WorkflowStage> getWorkflowStages() {
        return List.of(
                WorkOrder.WorkflowStage.JUNIOR,
                WorkOrder.WorkflowStage.MID,
                WorkOrder.WorkflowStage.SENIOR,
                WorkOrder.WorkflowStage.COMMITTEE
        );
    }

    /**
     * 检查用户是否有审核权限
     */
    public boolean hasReviewPermission(Long userId, WorkOrder.WorkOrderStatus status) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        return switch (status) {
            case PENDING_JUNIOR -> user.getAccount().contains("AUDITOR_JUNIOR") ||
                                 user.getAccount().contains("ADMIN");
            case PENDING_MID -> user.getAccount().contains("AUDITOR_MID") ||
                              user.getAccount().contains("ADMIN");
            case PENDING_SENIOR -> user.getAccount().contains("AUDITOR_SENIOR") ||
                                user.getAccount().contains("ADMIN");
            case PENDING_COMMITTEE -> user.getAccount().contains("INVEST_COMMITTEE") ||
                                    user.getAccount().contains("ADMIN");
            default -> false;
        };
    }
}
