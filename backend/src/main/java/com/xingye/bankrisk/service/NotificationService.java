package com.xingye.bankrisk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 通知服务类
 * 处理邮件发送和短信通知
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    /**
     * 发送审核完成通知
     */
    public void sendAuditCompletedNotification(String customerPhone, String customerName, boolean approved) {
        String subject = approved ? "投资建议审核通过通知" : "投资建议审核未通过通知";
        String content = buildAuditNotificationContent(customerName, approved);

        // 这里可以集成短信服务，如阿里云短信、腾讯云短信等
        // 暂时使用日志记录代替
        log.info("[INFO]NotificationService::sendAuditCompletedNotification: 发送审核完成通知 - Phone: {}, Subject: {}, Content: {}",
                customerPhone, subject, content);

        // 如果需要发送邮件，可以取消注释下面的代码
        // sendEmail(customerEmail, subject, content);
    }

    /**
     * 发送投资组合生成通知
     */
    public void sendPortfolioGeneratedNotification(String customerPhone, String customerName) {
        String subject = "投资组合建议已生成";
        String content = buildPortfolioNotificationContent(customerName);

        log.info("[INFO]NotificationService::sendPortfolioGeneratedNotification: 发送投资组合生成通知 - Phone: {}, Subject: {}, Content: {}",
                customerPhone, subject, content);

        // sendEmail(customerEmail, subject, content);
    }

    /**
     * 发送审核任务提醒
     */
    public void sendAuditTaskReminder(String reviewerEmail, String reviewerName, int pendingTasks) {
        String subject = "审核任务提醒";
        String content = buildTaskReminderContent(reviewerName, pendingTasks);

        log.info("[INFO]NotificationService::sendAuditTaskReminder: 发送审核任务提醒 - Email: {}, Subject: {}, Content: {}",
                reviewerEmail, subject, content);

        sendEmail(reviewerEmail, subject, content);
    }

    /**
     * 发送工单超期提醒
     */
    public void sendOverdueWorkOrderAlert(String reviewerEmail, String reviewerName, Long workOrderId) {
        String subject = "工单超期提醒";
        String content = buildOverdueAlertContent(reviewerName, workOrderId);

        log.info("[INFO]NotificationService::sendOverdueWorkOrderAlert: 发送工单超期提醒 - Email: {}, Subject: {}, Content: {}",
                reviewerEmail, subject, content);

        sendEmail(reviewerEmail, subject, content);
    }

    /**
     * 发送邮件
     */
    private void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            message.setFrom("noreply@xingye-bank.com");

            mailSender.send(message);
            log.info("[INFO]NotificationService::sendEmail: 邮件发送成功 - To: {}, Subject: {}", to, subject);

        } catch (Exception e) {
            log.error("[ERROR]NotificationService::sendEmail: 邮件发送失败 - To: {}, Subject: {}", to, subject, e);
        }
    }

    /**
     * 构建审核通知内容
     */
    private String buildAuditNotificationContent(String customerName, boolean approved) {
        StringBuilder content = new StringBuilder();
        content.append("尊敬的").append(customerName).append("客户：\n\n");

        if (approved) {
            content.append("恭喜您！您的投资风险评估申请已通过审核。\n");
            content.append("您的个性化投资组合建议已生成，请登录系统查看详细建议。\n\n");
            content.append("如有任何疑问，请联系我们的客户服务团队。\n");
        } else {
            content.append("很抱歉，您的投资风险评估申请暂未通过审核。\n");
            content.append("建议您重新评估风险偏好或联系客户经理获取更多帮助。\n\n");
            content.append("如有任何疑问，请联系我们的客户服务团队。\n");
        }

        content.append("\n此致\n");
        content.append("兴业银行投资风险审核系统\n");
        content.append(java.time.LocalDateTime.now().toLocalDate());

        return content.toString();
    }

    /**
     * 构建投资组合通知内容
     */
    private String buildPortfolioNotificationContent(String customerName) {
        StringBuilder content = new StringBuilder();
        content.append("尊敬的").append(customerName).append("客户：\n\n");
        content.append("您的个性化投资组合建议已成功生成！\n\n");
        content.append("建议内容包括：\n");
        content.append("• 风险等级评估结果\n");
        content.append("• 个性化资产配置建议\n");
        content.append("• 预期收益分析\n");
        content.append("• 风险提示\n\n");
        content.append("请登录系统查看详细的投资建议报告。\n\n");
        content.append("如有任何疑问，请联系我们的投资顾问团队。\n");

        content.append("\n此致\n");
        content.append("兴业银行投资风险审核系统\n");
        content.append(java.time.LocalDateTime.now().toLocalDate());

        return content.toString();
    }

    /**
     * 构建任务提醒内容
     */
    private String buildTaskReminderContent(String reviewerName, int pendingTasks) {
        StringBuilder content = new StringBuilder();
        content.append("尊敬的").append(reviewerName).append("审核员：\n\n");
        content.append("您当前有 ").append(pendingTasks).append(" 个待审核任务需要处理。\n\n");
        content.append("请及时登录审核工作台处理相关任务，确保审核流程的及时性和准确性。\n\n");
        content.append("如有紧急任务，请优先处理高优先级工单。\n");

        content.append("\n此致\n");
        content.append("兴业银行投资风险审核系统\n");
        content.append(java.time.LocalDateTime.now().toLocalDate());

        return content.toString();
    }

    /**
     * 构建超期提醒内容
     */
    private String buildOverdueAlertContent(String reviewerName, Long workOrderId) {
        StringBuilder content = new StringBuilder();
        content.append("尊敬的").append(reviewerName).append("审核员：\n\n");
        content.append("工单 #").append(workOrderId).append(" 已超期！\n\n");
        content.append("请立即处理该工单，以免影响整体审核流程。\n");
        content.append("如有特殊情况，请及时与上级汇报。\n");

        content.append("\n此致\n");
        content.append("兴业银行投资风险审核系统\n");
        content.append(java.time.LocalDateTime.now().toLocalDate());

        return content.toString();
    }
}
