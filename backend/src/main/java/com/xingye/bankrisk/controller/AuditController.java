package com.xingye.bankrisk.controller;

import com.xingye.bankrisk.entity.WorkOrder;
import com.xingye.bankrisk.service.AuthService;
import com.xingye.bankrisk.service.WorkflowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 审核控制器
 * 处理审核流程相关的请求
 */
@Slf4j
@RestController
@RequestMapping("/audit")
@RequiredArgsConstructor
@Tag(name = "审核管理", description = "审核流程相关接口")
public class AuditController {

    private final WorkflowService workflowService;
    private final AuthService authService;

    /**
     * 获取审核任务列表
     */
    @GetMapping("/tasks")
    @Operation(summary = "获取审核任务", description = "获取当前用户的审核任务列表")
    public ResponseEntity<Map<String, Object>> getAuditTasks(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String status) {

        log.info("[INFO]AuditController::getAuditTasks: 获取审核任务列表");

        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            String userRole = authService.getRoleFromToken(actualToken);

            // 根据用户角色确定审核员类型
            WorkOrder.WorkOrderStatus workOrderStatus = null;
            if (status != null) {
                workOrderStatus = WorkOrder.WorkOrderStatus.valueOf(status);
            }

            List<WorkOrder> tasks;
            if (workOrderStatus != null) {
                // 获取指定状态的任务
                tasks = workflowService.getPendingWorkOrders(convertToUserRole(userRole));
            } else {
                // 获取所有待审核任务
                tasks = workflowService.getPendingWorkOrders(convertToUserRole(userRole));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", tasks);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuditController::getAuditTasks: 获取审核任务失败", e);
            return createErrorResponse("获取审核任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取我的审核任务
     */
    @GetMapping("/tasks/my")
    @Operation(summary = "获取我的审核任务", description = "获取当前审核员的任务列表")
    public ResponseEntity<Map<String, Object>> getMyAuditTasks(@RequestHeader("Authorization") String token) {
        log.info("[INFO]AuditController::getMyAuditTasks: 获取我的审核任务");

        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long reviewerId = authService.getUserIdFromToken(actualToken);

            List<WorkOrder> tasks = workflowService.getReviewerTasks(reviewerId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", tasks);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuditController::getMyAuditTasks: 获取我的审核任务失败", e);
            return createErrorResponse("获取我的审核任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取审核任务详情
     */
    @GetMapping("/tasks/{id}")
    @Operation(summary = "获取审核任务详情", description = "获取指定审核任务的详细信息")
    public ResponseEntity<Map<String, Object>> getAuditTaskDetail(@PathVariable Long id) {
        log.info("[INFO]AuditController::getAuditTaskDetail: 获取审核任务详情 - ID: {}", id);

        try {
            WorkOrder workOrder = workflowService.getWorkOrderDetail(id)
                    .orElseThrow(() -> new RuntimeException("审核任务不存在: " + id));

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", workOrder);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuditController::getAuditTaskDetail: 获取审核任务详情失败", e);
            return createErrorResponse("获取审核任务详情失败: " + e.getMessage());
        }
    }

    /**
     * 认领审核任务
     */
    @PostMapping("/tasks/{id}/claim")
    @Operation(summary = "认领审核任务", description = "认领指定的审核任务")
    public ResponseEntity<Map<String, Object>> claimAuditTask(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {

        log.info("[INFO]AuditController::claimAuditTask: 认领审核任务 - ID: {}", id);

        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long reviewerId = authService.getUserIdFromToken(actualToken);

            WorkOrder workOrder = workflowService.claimWorkOrder(id, reviewerId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "审核任务认领成功");
            response.put("data", workOrder);

            log.info("[INFO]AuditController::claimAuditTask: 审核任务认领成功 - ID: {}", id);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuditController::claimAuditTask: 认领审核任务失败", e);
            return createErrorResponse("认领审核任务失败: " + e.getMessage());
        }
    }

    /**
     * 完成审核任务
     */
    @PostMapping("/tasks/{id}/complete")
    @Operation(summary = "完成审核任务", description = "完成审核任务并推进工作流")
    public ResponseEntity<Map<String, Object>> completeAuditTask(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token,
            @RequestBody AuditDecisionRequest request) {

        log.info("[INFO]AuditController::completeAuditTask: 完成审核任务 - ID: {}, Decision: {}", id, request.getDecision());

        try {
            String actualToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Long reviewerId = authService.getUserIdFromToken(actualToken);

            WorkOrder workOrder = workflowService.advanceWorkflow(id, reviewerId, request.getDecision(), request.getComments());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "审核任务完成");
            response.put("data", workOrder);

            log.info("[INFO]AuditController::completeAuditTask: 审核任务完成 - ID: {}, NewStatus: {}", id, workOrder.getStatus());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuditController::completeAuditTask: 完成审核任务失败", e);
            return createErrorResponse("完成审核任务失败: " + e.getMessage());
        }
    }

    /**
     * 获取工作流统计数据
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取审核统计", description = "获取审核流程的统计数据")
    public ResponseEntity<Map<String, Object>> getAuditStatistics() {
        log.info("[INFO]AuditController::getAuditStatistics: 获取审核统计");

        try {
            List<Object[]> statistics = workflowService.getWorkflowStatistics();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuditController::getAuditStatistics: 获取审核统计失败", e);
            return createErrorResponse("获取审核统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取超期工单
     */
    @GetMapping("/overdue")
    @Operation(summary = "获取超期工单", description = "获取已超期的审核任务")
    public ResponseEntity<Map<String, Object>> getOverdueWorkOrders() {
        log.info("[INFO]AuditController::getOverdueWorkOrders: 获取超期工单");

        try {
            List<WorkOrder> overdueWorkOrders = workflowService.getOverdueWorkOrders();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", overdueWorkOrders);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("[ERROR]AuditController::getOverdueWorkOrders: 获取超期工单失败", e);
            return createErrorResponse("获取超期工单失败: " + e.getMessage());
        }
    }

    /**
     * 转换角色字符串为UserRole枚举
     */
    private User.UserRole convertToUserRole(String role) {
        return switch (role) {
            case "AUDITOR_JUNIOR" -> User.UserRole.AUDITOR_JUNIOR;
            case "AUDITOR_MID" -> User.UserRole.AUDITOR_MID;
            case "AUDITOR_SENIOR" -> User.UserRole.AUDITOR_SENIOR;
            case "INVEST_COMMITTEE" -> User.UserRole.INVEST_COMMITTEE;
            default -> User.UserRole.CUSTOMER;
        };
    }

    /**
     * 创建错误响应
     */
    private ResponseEntity<Map<String, Object>> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("code", "AUDIT_ERROR");

        return ResponseEntity.badRequest().body(response);
    }

    // 请求DTO类

    /**
     * 审核决策请求
     */
    public static class AuditDecisionRequest {
        private String decision; // APPROVE 或 REJECT
        private String comments; // 审核意见

        // Getters and Setters
        public String getDecision() { return decision; }
        public void setDecision(String decision) { this.decision = decision; }

        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
    }
}
